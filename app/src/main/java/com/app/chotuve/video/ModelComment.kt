package com.app.chotuve.video

import com.app.chotuve.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class ModelComment(
    var comment: Comment
) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.layout_comment_list_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
    }
}

class Comment {}