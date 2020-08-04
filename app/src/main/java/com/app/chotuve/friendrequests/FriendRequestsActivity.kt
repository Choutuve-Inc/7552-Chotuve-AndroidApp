package com.app.chotuve.friendrequests

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.app.chotuve.R
import com.app.chotuve.context.ApplicationContext
import com.app.chotuve.friendlist.FriendsDataSource
import com.app.chotuve.utils.JSONArraySorter
import com.app.chotuve.utils.TopSpacingItemDecoration
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_friend_requests.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray

class FriendRequestsActivity : AppCompatActivity() {
    private val TAG: String = "Friend Request Screen"
    private val friendRequestAdapter = GroupAdapter<GroupieViewHolder>()
    private val friendRequestMap: ArrayList<ModelFriendRequest> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_requests)

        addDataSet()
        rec_friend_requests.adapter = friendRequestAdapter

        rec_friend_requests.apply {
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
        var friends = FriendsDataSource.getFriendRequestsFromHTTP()
        withContext(Dispatchers.Main){
            friendRequestAdapter.clear()
        }
        friends = JSONArraySorter.sortJSONArrayByFirstLevelStringField(friends, "displayName")
        if (friends.length() > 0) {
            lbl_friend_request_nothing.visibility = View.INVISIBLE
        }
        for (i in 0 until friends.length()) {
            val item = friends.getJSONObject(i)
            val username = item["displayName"] as String
            val userID = item["uid"] as String
            val imageID = item["photoURL"] as String //photoURL
            Thread.sleep(10)//Needed for correct order on the friend list
            CoroutineScope(Dispatchers.IO).launch{
                val friend = FriendsDataSource.getFriendFromFirebase(username, userID, imageID)
                addVideoToRecyclerView(ModelFriendRequest(friend))
            }
        }
        Log.d(TAG, "Videos got: ${friends.length()}.")
    }

    private suspend fun addVideoToRecyclerView(request: ModelFriendRequest){
        withContext(Dispatchers.Main){
            Log.d(TAG, "Friend add Item Sent.")
            friendRequestAdapter.add(request)
            friendRequestMap.add(request)
            friendRequestAdapter.notifyDataSetChanged()
        }
    }
}