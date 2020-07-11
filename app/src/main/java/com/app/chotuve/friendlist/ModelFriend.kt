package com.app.chotuve.friendlist

import android.os.Parcelable
import com.app.chotuve.R
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.layout_friend_list_item.view.*

class ModelFriend(
    var friend: Friend
) : Item<GroupieViewHolder>(){
    override fun getLayout(): Int {
        return R.layout.layout_friend_list_item
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.lbl_friends_username.text = friend.username

        Picasso.get().load(friend.imageID).into(viewHolder.itemView.img_friends_user)
    }

}

@Parcelize
class Friend(
    var username: String,
    var imageID: String,
    var userID: String
): Parcelable {
    constructor() : this("", "", "") {}
}