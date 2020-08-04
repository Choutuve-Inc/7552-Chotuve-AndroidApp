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
        private const val DEF_IMAGE: String = "https://firebasestorage.googleapis.com/v0/b/chotuve-android-media.appspot.com/o/userPic%2Fdefault_userPic.jpg?alt=media&token=fde26300-a5dc-434f-a5d0-17096e6a7d5a"

        fun getFriendListFromHTTP():JSONArray {
            val jsonList = JSONArray()
            val requestsURL = "${ApplicationContext.getServerURL()}/friendlist"
            val (request, response, result) = requestsURL.httpGet()
                .appendHeader("user", ApplicationContext.getConnectedUsername())
                .appendHeader("token", ApplicationContext.getConnectedToken())
                .jsonBody(
                    "{ \"user\" : \"${ApplicationContext.getConnectedUsername()}\"," +
                            " \"token\" : \"${ApplicationContext.getConnectedToken()}\"" +
                            "}"
                )
                .responseJson()
            when (result) {
                is Result.Success -> {
                    Log.d(TAG, "HTTP Success [getFriendListFromHTTP]")
                    val body = response.body()
                    val result = JSONObject(body.asString("application/json"))
                    return getUsersListFromHTTP(result["friends"] as JSONArray)
                }
                is Result.Failure -> {
                    //Look up code and choose what to do.
                    Log.d(TAG, "Error obtaining Users.")
                }
            }
            return jsonList
        }

        fun getFriendRequestsFromHTTP(): JSONArray {
            val jsonList = JSONArray()
            val requestsURL = "${ApplicationContext.getServerURL()}/request"
            val (request, response, result) = requestsURL.httpGet()
                .appendHeader("user", ApplicationContext.getConnectedUsername())
                .appendHeader("token", ApplicationContext.getConnectedToken())
                .jsonBody(
                    "{ \"user\" : \"${ApplicationContext.getConnectedUsername()}\"," +
                            " \"token\" : \"${ApplicationContext.getConnectedToken()}\"" +
                            "}"
                )
                .responseJson()
            when (result) {
                is Result.Success -> {
                    Log.d(TAG, "HTTP Success [getFriendRequestsFromHTTP]")
                    val body = response.body()
                    val result = JSONObject(body.asString("application/json"))
                    return getUsersListFromHTTP(result["requests"] as JSONArray)
                }
                is Result.Failure -> {
                    //Look up code and choose what to do.
                    Log.d(TAG, "Error obtaining Users.")
                }
            }
            return jsonList
        }

        private fun getUsersListFromHTTP(jsonList: JSONArray): JSONArray {
            var users = ","

            for (i in 0 until jsonList.length()) {
                users = "$users${jsonList.get(i)},"
            }

            val usersURL =  "${ApplicationContext.getServerURL()}/users/list/$users"
            val (request, response, result) = usersURL.httpGet()
                .appendHeader("user", ApplicationContext.getConnectedUsername())
                .appendHeader("token", ApplicationContext.getConnectedToken())
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
            val singleVideoURL = "${ApplicationContext.getServerURL()}/users/$userID"
            val (request, response, result) = singleVideoURL.httpGet()
                .appendHeader("user", ApplicationContext.getConnectedUsername())
                .appendHeader("token", ApplicationContext.getConnectedToken())
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
                    Log.d(TAG, "Error obtaining Friend by id ${userID}.")
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