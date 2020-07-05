package com.app.chotuve.chat

import com.app.chotuve.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.layout_chat_from_item.view.*

class ModelChatOutgoingItem(var messageText: String) : Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.layout_chat_from_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.lbl_chat_message.text = messageText

    }

}

class ModelChatIncomingItem(var messageText: String) : Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.layout_chat_to_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.lbl_chat_message.text = messageText

    }

}