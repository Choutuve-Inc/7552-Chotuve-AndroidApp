package com.app.chotuve.friendlist

import android.util.Log
import com.app.chotuve.context.ApplicationContext
import com.app.chotuve.home.ModelVideo
import com.app.chotuve.home.VideoDataSource
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject

class FriendsDataSource {
    companion object {
        private const val TAG: String = "Friends Data Source"
        private const val serverURL: String = "https://serene-shelf-10674.herokuapp.com/users"
        private const val DEF_IMAGE: String = "https://firebasestorage.googleapis.com/v0/b/chotuve-android-media.appspot.com/o/userPic%2Fdefault_userPic.jpg?alt=media&token=fde26300-a5dc-434f-a5d0-17096e6a7d5a"

        fun getUsersFromHTTP(): JSONArray{
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
                    Log.d(TAG, "HTTP Success [getUsersFromHTTP]")
                    val body = response.body()
                    return JSONArray(body.asString("application/json"))
                }
                is Result.Failure -> {
                    //Look up code and choose what to do.
                    Log.d(TAG, "Error obtaining Users.")
                }
            }
            return jsonList
        }

        fun getSingleUserFromHTTP(userID: String): JSONObject {
            val json = JSONObject()
            val singleVideoURL = "$serverURL/$userID"
            val (request, response, result) = singleVideoURL.httpGet()
                .jsonBody(
                    "{ \"user\" : \"${ApplicationContext.getConnectedUsername()}\"," +
                            " \"token\" : \"${ApplicationContext.getConnectedToken()}\"" +
                            "}"
                )
                .responseJson()
            when (result) {
                is Result.Success -> {
                    Log.d(TAG, "HTTP Success [getSingleUserFromHTTP]")
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

        fun getFriendFromFirebase(username: String, userID: String, imageID: String): Friend {
            var img = imageID
            if (img == null){
                img = DEF_IMAGE
            }
            return Friend(username, img, userID)
        }
    }
}