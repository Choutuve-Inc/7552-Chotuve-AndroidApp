package com.app.chotuve.friendlist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.app.chotuve.R
import com.app.chotuve.chat.ChatActivity
import com.app.chotuve.friendlist.FriendsDataSource.Companion.getFriendFromFirebase
import com.app.chotuve.utils.TopSpacingItemDecoration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_friends.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FriendsActivity : AppCompatActivity() {
    private val TAG = "Friends List"
    private val FRIEND_KEY = "FRIEND_KEY"
    private val friendlistAdapter = GroupAdapter<GroupieViewHolder>()
    private var friendItems: ArrayList<ModelFriend> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        addDataSet()
        rec_friends_list.adapter = friendlistAdapter
        
        friendlistAdapter.setOnItemClickListener { item, view ->

            val friendItem = item as ModelFriend

            val intent = Intent(this@FriendsActivity, ChatActivity::class.java)
            intent.putExtra(FRIEND_KEY, friendItem.friend)
            startActivity(intent)

            finish()
        }
        
        rec_friends_list.apply {
            val topSpacingDecoration = TopSpacingItemDecoration(8)
            addItemDecoration(topSpacingDecoration)
        }
    }

    private fun addDataSet() {
        CoroutineScope(Dispatchers.IO).launch {
            getData()
        }
    }

    private suspend fun getData() {
        val friends = FriendsDataSource.getUsersFromHTTP()
        for (i in 0 until friends.length()) {
            val item = friends.getJSONObject(i)
            val username = item["displayName"] as String
            val userID = item["uid"] as String
            val imageID = item["photoURL"] as String //photoURL
            CoroutineScope(Dispatchers.IO).launch{
                val friend = getFriendFromFirebase(username, userID, imageID)
                addVideoToRecyclerView(ModelFriend(friend))
            }
        }
        Log.d(TAG, "Videos got: ${friends.length()}.")
    }

    private suspend fun addVideoToRecyclerView(friend: ModelFriend){
        withContext(Dispatchers.Main){
            Log.d(TAG, "Friend add Item Sent.")
            friendlistAdapter.add(friend)
            friendItems.add(friend)
            friendlistAdapter.notifyDataSetChanged()
        }
    }

    private fun toastMessage(message: String) {
        Toast.makeText(this@FriendsActivity, message, Toast.LENGTH_LONG).show()
    }
}