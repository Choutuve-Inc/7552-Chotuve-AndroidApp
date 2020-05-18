package com.app.chotuve.home

import android.util.Log
import com.app.chotuve.context.ApplicationContext
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.tasks.Tasks.await
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONArray


class VideoDataSource {

    companion object {
        private const val TAG: String = "Video Data Source"
        private const val serverURL: String = "https://choutuve-app-server.herokuapp.com/videos"

        fun getVideosFromHTTP(): JSONArray {
            val jsonList: JSONArray = JSONArray()
            val (request, response, result) = serverURL.httpGet()
                .jsonBody(
                    "{ \"user\" : \"${ApplicationContext.getConnectedUsername()}\"," +
                            " \"token\" : \"${ApplicationContext.getConnectedToken()}\"" +
                            "}"
                )
                .responseJson()
            when (result) {
                is Result.Success -> {
                    Log.d(TAG, "HTTP Success")
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

        suspend fun getVideoFromFirebase(date: String, title:String, user:String, thumbURL: String): ModelVideo  {
            val storage = FirebaseStorage.getInstance().reference
            var video = ModelVideo("title", "usr", "img", "dat")
            storage.child("thumbnails/").child(thumbURL).downloadUrl
                .addOnSuccessListener {
                    var url = it.toString()
                    video = ModelVideo(title, user, url, date)
                    Log.d(TAG, "Success $url.")
                }.addOnFailureListener {
                    Log.d(TAG, "Error obtaining Video: ${it.message}.")
                    video.image = ""
                }.await()
            Log.d(TAG, "Retrieved Video from Firebase.")
            return video
        }
    }
}