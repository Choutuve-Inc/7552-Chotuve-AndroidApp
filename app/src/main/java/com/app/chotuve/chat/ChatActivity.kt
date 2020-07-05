package com.app.chotuve.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app.chotuve.R
import com.app.chotuve.friendlist.Friend
import com.app.chotuve.utils.TopSpacingItemDecoration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_friends.*

class ChatActivity : AppCompatActivity() {
    private val TAG = "Chat Activity"
    private val FRIEND_KEY = "FRIEND_KEY"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val friend = intent.getParcelableExtra<Friend>(FRIEND_KEY)

        supportActionBar?.title = "${friend.username}'s Chat"
        val chatAdapter = GroupAdapter<GroupieViewHolder>()

        chatAdapter.add(ModelChatOutgoingItem("Texto 1"))
        chatAdapter.add(ModelChatOutgoingItem("Este es un segundo mensaje"))
        chatAdapter.add(ModelChatIncomingItem("Te respondo a tu mensaje"))
        chatAdapter.add(ModelChatOutgoingItem("Mensaje laaaargo. Espero que este sea un mensaje que tome demasiadas líneas"))
        chatAdapter.add(ModelChatIncomingItem("Haha respuesta larga goes brrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr"))
        chatAdapter.add(ModelChatOutgoingItem("Mensaje corto"))
        chatAdapter.add(ModelChatOutgoingItem("Texto 1"))
        chatAdapter.add(ModelChatOutgoingItem("Este es un segundo mensaje"))
        chatAdapter.add(ModelChatIncomingItem("Te respondo a tu mensaje"))
        chatAdapter.add(ModelChatOutgoingItem("Mensaje laaaargo. Espero que este sea un mensaje que tome demasiadas líneas"))
        chatAdapter.add(ModelChatIncomingItem("Haha respuesta larga goes brrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr"))
        chatAdapter.add(ModelChatOutgoingItem("Mensaje corto"))
        chatAdapter.add(ModelChatOutgoingItem("Texto 1"))
        chatAdapter.add(ModelChatOutgoingItem("Este es un segundo mensaje"))
        chatAdapter.add(ModelChatIncomingItem("Te respondo a tu mensaje"))
        chatAdapter.add(ModelChatOutgoingItem("Mensaje laaaargo. Espero que este sea un mensaje que tome demasiadas líneas"))
        chatAdapter.add(ModelChatIncomingItem("Haha respuesta larga goes brrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr"))
        chatAdapter.add(ModelChatOutgoingItem("Mensaje corto"))

        rec_chat_log.adapter = chatAdapter

        rec_chat_log.apply {
            val topSpacingDecoration = TopSpacingItemDecoration(16)
            addItemDecoration(topSpacingDecoration)
        }
    }
}
