package com.app.chotuve.video

import com.app.chotuve.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.layout_comment_list_item.view.*

class ModelComment(
    var comment: Comment
) : Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.layout_comment_list_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.lbl_comment_user.text = comment.userID
        viewHolder.itemView.lbl_comment_comment.text = comment.text
    }
}

class Comment(
    var userID: String,
    var text: String
) {
    constructor() : this("","")
}