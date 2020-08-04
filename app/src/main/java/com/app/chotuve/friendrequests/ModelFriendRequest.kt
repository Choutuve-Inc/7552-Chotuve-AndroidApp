package com.app.chotuve.friendrequests

import android.opengl.Visibility
import android.util.Log
import android.view.View
import com.app.chotuve.R
import com.app.chotuve.context.ApplicationContext
import com.app.chotuve.friendlist.Friend
import com.app.chotuve.friendlist.FriendsDataSource
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.layout_friend_request_item.view.*

class ModelFriendRequest(val friend: Friend): Item<GroupieViewHolder>() {
    private val TAG = "ModelFriendRequest"

    override fun getLayout(): Int {
        return R.layout.layout_friend_request_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.lbl_friends_request_username.text = friend.username

        Picasso.get().load(friend.imageID).into(viewHolder.itemView.img_friends_request_user)

        viewHolder.itemView.btn_friend_request_accept.setOnClickListener {
            Log.d(TAG, "Accept clicked for user: ${friend.username}")
            answerFriendRequest(viewHolder,true)

        }
        viewHolder.itemView.btn_friend_request_cancel.setOnClickListener {
            Log.d(TAG, "Cancel clicked for user: ${friend.username}")
            answerFriendRequest(viewHolder,false)

        }
    }

    private fun answerFriendRequest(viewHolder: GroupieViewHolder, value: Boolean){
        Fuel.post("${ApplicationContext.getServerURL()}/request/confirm")
            .appendHeader("user", ApplicationContext.getConnectedUsername())
            .appendHeader("token", ApplicationContext.getConnectedToken())
            .jsonBody(
                "{ \"user\" : \"${ApplicationContext.getConnectedUsername()}\"," +
                        " \"token\" : \"${ApplicationContext.getConnectedToken()}\"," +
                        " \"id\" : \"${friend.userID}\"," +
                        " \"value\" : $value" +
                        "}"
            )
            .responseJson{ request, response, result ->
                when (result) {
                    is Result.Success -> {
                        Log.d(TAG, "HTTP Success [answerFriendRequest]")
                        if (value){
                            viewHolder.itemView.btn_friend_request_cancel.visibility = View.INVISIBLE
                            viewHolder.itemView.btn_friend_request_accept.isClickable = false
                        }else{
                            viewHolder.itemView.btn_friend_request_accept.visibility = View.INVISIBLE
                            viewHolder.itemView.btn_friend_request_cancel.isClickable = false
                        }
                    }
                    is Result.Failure -> {
                        //Look up code and choose what to do.
                        Log.d(TAG, "Error answering request.")
                    }
                }
            }
    }
}