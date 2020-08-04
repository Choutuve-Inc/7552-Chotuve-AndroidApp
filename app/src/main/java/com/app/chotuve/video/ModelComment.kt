package com.app.chotuve.video

import android.util.Log
import com.app.chotuve.R
import com.app.chotuve.context.ApplicationContext
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.layout_comment_list_item.view.*
import org.json.JSONObject

class ModelComment(
    var comment: Comment
) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.layout_comment_list_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        getUsernameFromHTTP(viewHolder, comment.userID)
        viewHolder.itemView.lbl_comment_comment.text = comment.text
    }

    private fun getUsernameFromHTTP(viewHolder: GroupieViewHolder, userID: String) {
        val serverURL = "${ApplicationContext.getServerURL()}/users/${userID}"
        serverURL.httpGet()
            .appendHeader("user", ApplicationContext.getConnectedUsername())
            .appendHeader("token", ApplicationContext.getConnectedToken())
            .jsonBody(
                "{ \"user\" : \"${ApplicationContext.getConnectedUsername()}\"," +
                        " \"token\" : \"${ApplicationContext.getConnectedToken()}\"" +
                        "}"
            )
            .responseJson { request, response, result ->
                when (result) {
                    is Result.Success -> {
                        Log.d("ModelComment", "HTTP Success [getUsernameFromHTTP]")
                        val body = response.body()
                        val json = JSONObject(body.asString("application/json"))
                        viewHolder.itemView.lbl_comment_user.text =  json.getString("displayName")
                    }
                    is Result.Failure -> {
                        //Look up code and choose what to do.
                        Log.d("ModelComment", "Error obtaining User by id ${userID}.")
                        viewHolder.itemView.lbl_comment_user.text =  userID
                    }
                }
            }
    }
}

class Comment(
    var userID: String,
    var text: String
) {
    constructor() : this("","")
}