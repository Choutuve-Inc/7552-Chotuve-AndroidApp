package com.app.chotuve.home

import android.util.Log
import com.app.chotuve.context.ApplicationContext
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import com.google.firebase.storage.FirebaseStorage
import org.json.JSONArray


class VideoDataSource {

    companion object {
        private val TAG: String = "Video Data Source"
        private const val serverURL: String = "https://choutuve-app-server.herokuapp.com/videos"
        fun createDataSet(): ArrayList<ModelVideo> {
            val storage = FirebaseStorage.getInstance().reference
            val list = ArrayList<ModelVideo>()
            Fuel.get(serverURL)
                .jsonBody("{ \"user\" : \"${ApplicationContext.getConnectedUsername()}\"," +
                        " \"token\" : \"${ApplicationContext.getConnectedUsername()}\"" +
                        "}")
                .responseJson { request, response, result ->
                    when (result) {
                        is Result.Success -> {
                            val body = response.body()
                            val jsonList = JSONArray(body.asString("application/json"))
                            for (i in 0 until jsonList.length()) {
                                val item = jsonList.getJSONObject(i)
                                val date = item["date"] as String
                                val title = item["title"] as String
                                val user = item["user"] as String
                                val thumbURL: String = item["thumbnail"] as String

                                storage.child("thumbnails/").child(thumbURL).downloadUrl
                                    .addOnSuccessListener {
                                        var url = it.toString()
                                        list.add(
                                            ModelVideo(
                                                title,
                                                user,
                                                url,
                                                date
                                            )
                                        )
                                        Log.d(TAG, "Success $url.")
                                    }.addOnFailureListener {
                                        Log.d(TAG, "Error obtaining Video: ${it.message}.")
                                    }
                            }
                        }
                        is Result.Failure -> {
                            //Look up code and choose what to do.
                            Log.d(TAG, "Error obtaining Videos.")
                        }
                    }
                }
            return list
        }
    }
}