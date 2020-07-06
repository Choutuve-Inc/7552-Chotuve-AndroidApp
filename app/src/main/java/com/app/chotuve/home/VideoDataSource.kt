package com.app.chotuve.home

import android.util.Log
import com.app.chotuve.context.ApplicationContext
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject


class VideoDataSource {

    companion object {
        private const val TAG: String = "Video Data Source"
        private const val serverURL: String = "https://arcane-thicket-79100.herokuapp.com/videos"
        //TODO private const val serverURL: String = "https://arcane-thicket-79100.herokuapp.com/videos"

        fun getVideosFromHTTP(): JSONArray {
            val jsonList = JSONArray()
            val (request, response, result) = serverURL.httpGet()
                .jsonBody(
                    "{ \"user\" : \"${ApplicationContext.getConnectedUsername()}\"," +
                            " \"token\" : \"${ApplicationContext.getConnectedToken()}\"" +
                            "}"
                )
                .responseJson()
            when (result) {
                is Result.Success -> {
                    Log.d(TAG, "HTTP Success [getVideosFromHTTP]")
                    val body = response.body()
                    return JSONArray(body.asString("application/json"))
                }
                is Result.Failure -> {
                    //Look up code and choose what to do.
                    Log.d(TAG, "Error obtaining Videos.")
                }
            }
            return jsonList
        }

        fun getSingleVideoFromHTTP(vidID: Int): JSONObject {
            val json = JSONObject()
            val singleVideoURL = "$serverURL/$vidID"
            val (request, response, result) = singleVideoURL.httpGet()
                .jsonBody(
                    "{ \"user\" : \"${ApplicationContext.getConnectedUsername()}\"," +
                            " \"token\" : \"${ApplicationContext.getConnectedToken()}\"" +
                            "}"
                )
                .responseJson()
            when (result) {
                is Result.Success -> {
                    Log.d(TAG, "HTTP Success [getSingleVideoFromHTTP]")
                    val body = response.body()
                    return JSONObject(body.asString("application/json"))
                }
                is Result.Failure -> {
                    //Look up code and choose what to do.
                    Log.d(TAG, "Error obtaining Videos.")
                }
            }
            return json
        }

        suspend fun getVideoFromFirebase(date: String, title:String, user:String, thumbID: String, vidID: Int): ModelVideo  {
            val storage = FirebaseStorage.getInstance().reference
            var video = ModelVideo("title", "usr", "img", "dat", 0)
            storage.child("thumbnails/").child(thumbID).downloadUrl
                .addOnSuccessListener {
                    var thumbURL = it.toString()
                    video = ModelVideo(title, user, thumbURL, date, vidID)
                    Log.d(TAG, "Success $thumbURL.")
                }.addOnFailureListener {
                    Log.d(TAG, "Error obtaining Video: ${it.message}.")
                    video.image = ""
                }.await()
            Log.d(TAG, "Retrieved Video from Firebase.")
            return video
        }
    }
}