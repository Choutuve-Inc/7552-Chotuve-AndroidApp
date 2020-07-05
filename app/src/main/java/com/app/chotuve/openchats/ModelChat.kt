package com.app.chotuve.openchats

import com.app.chotuve.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class ModelChat: Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.layout_chat_list_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
    }

}