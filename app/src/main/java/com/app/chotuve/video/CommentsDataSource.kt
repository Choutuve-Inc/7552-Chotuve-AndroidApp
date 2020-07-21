package com.app.chotuve.video

import android.util.Log
import com.app.chotuve.context.ApplicationContext
import com.app.chotuve.friendlist.FriendsDataSource
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import org.json.JSONArray
import org.json.JSONObject

class CommentsDataSource {
    companion object {
        private const val TAG: String = "Comments Data Source"

        fun getCommentsFromHTTP(url: String): JSONArray {
            val jsonList = JSONArray()
            val (request, response, result) = url.httpGet()
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
                    Log.d(TAG, "HTTP Success [getCommentsFromHTTP]")
                    val body = response.body()
                    val json = JSONObject(body.asString("application/json"))
                    return json["comments"] as JSONArray
                }
                is Result.Failure -> {
                    //Look up code and choose what to do.
                    Log.d(TAG, "Error obtaining Comments.")
                    Log.d(TAG, "Error Code: ${response.statusCode}")
                    Log.d(TAG, "Error Message: ${result.error}")
                }
            }
            return jsonList
        }
    }
}