package com.app.chotuve.chat

import android.util.Log
import com.app.chotuve.R
import com.app.chotuve.context.ApplicationContext
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.layout_chat_from_item.view.*
import org.json.JSONObject



class ModelChatOutgoingItem(var messageText: String, var imageURL: String?) : Item<GroupieViewHolder>(){
    private val serverURL: String = "https://serene-shelf-10674.herokuapp.com/users"
    private val TAG: String = "ModelChatItem"
    override fun getLayout(): Int {
        return R.layout.layout_chat_from_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.lbl_chat_message.text = messageText

        if (imageURL!=null){
            Picasso.get().load(imageURL).into(viewHolder.itemView.img_chat_user)
        }else{
            "$serverURL/${ApplicationContext.getConnectedUsername()}".httpGet()
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
                            val myPhoto = user["photoURL"] as String
                            Picasso.get().load(myPhoto).into(viewHolder.itemView.img_chat_user)
                        }
                        is Result.Failure -> {
                            //Look up code and choose what to do.
                            Log.d(TAG, "Error obtaining User photo.")
                            Log.d(TAG, "Code: ${result.error}")
                        }
                    }
                }
        }
    }

}

class ModelChatIncomingItem(var messageText: String, var imageURL: String?) : Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.layout_chat_to_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.lbl_chat_message.text = messageText

        if (imageURL!=null) Picasso.get().load(imageURL).into(viewHolder.itemView.img_chat_user)
    }

}