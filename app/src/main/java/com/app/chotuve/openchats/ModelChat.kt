package com.app.chotuve.openchats

import android.util.Log
import com.app.chotuve.R
import com.app.chotuve.context.ApplicationContext
import com.app.chotuve.friendlist.FriendsDataSource
import com.app.chotuve.friendlist.FriendsDataSource.Companion.getFriendFromFirebase
import com.app.chotuve.friendlist.FriendsDataSource.Companion.getSingleUserFromHTTP
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.layout_chat_from_item.view.*
import kotlinx.android.synthetic.main.layout_chat_list_item.view.*
import org.json.JSONObject

class ModelChat(val openChat: OpenChat): Item<GroupieViewHolder>() {
    private val serverURL: String = "https://serene-shelf-10674.herokuapp.com/users"
    private val TAG = "ModelChat"

    override fun getLayout(): Int {
        return R.layout.layout_chat_list_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.lbl_open_chats_text.text = openChat.text


        "$serverURL/${openChat.friend.userID}".httpGet()
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
                        Log.d(TAG, "HTTP Success [getSingleUserFromHTTP]")
                        val body = response.body()
                        val user = JSONObject(body.asString("application/json"))

                        openChat.friend = getFriendFromFirebase(user["displayName"] as String, user["uid"] as String, user["photoURL"] as String)

                        viewHolder.itemView.lbl_open_chats_username.text = openChat.friend.username
                        if (openChat.friend.imageID!="") Picasso.get().load(openChat.friend.imageID).into(viewHolder.itemView.img_open_chats_user)

                    }
                    is Result.Failure -> {
                        //Look up code and choose what to do.
                        Log.d(TAG, "Error obtaining User with ID: ${openChat.friend.userID}.")
                        Log.d(TAG, "Code: ${result.error}")
                    }
                }
            }
    }

}