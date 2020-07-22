package com.app.chotuve.openchats

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.app.chotuve.R
import com.app.chotuve.chat.ChatActivity
import com.app.chotuve.chat.ChatMessage
import com.app.chotuve.context.ApplicationContext
import com.app.chotuve.friendlist.Friend
import com.app.chotuve.friendlist.FriendsActivity
import com.app.chotuve.friendlist.ModelFriend
import com.app.chotuve.home.HomePageActivity
import com.app.chotuve.login.LoginActivity
import com.app.chotuve.profile.ProfileActivity
import com.app.chotuve.utils.TopSpacingItemDecoration
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.result.Result
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_open_chats.*

class OpenChatsActivity : AppCompatActivity() {
    private val TAG: String = "Chats Screen"
    private val FRIEND_KEY = "FRIEND_KEY"
    private val openChatsAdapter = GroupAdapter<GroupieViewHolder>()
    private val openChatsMap = HashMap<String, ModelChat>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_chats)

        rec_open_chats.adapter = openChatsAdapter

        openChatsAdapter.setOnItemClickListener { item, view ->

            val modelChat = item as ModelChat

            val intent = Intent(this@OpenChatsActivity, ChatActivity::class.java)
            intent.putExtra(FRIEND_KEY, modelChat.openChat.friend)
            startActivity(intent)
        }

        rec_open_chats.apply {
            val topSpacingDecoration =
                TopSpacingItemDecoration(8)
            addItemDecoration(topSpacingDecoration)
        }

        listenForChats()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId){
            R.id.top_home_page_log_out -> {
                Log.d(TAG, "Log Out Button Clicked")
                logout()
            }
            R.id.top_chats_friends -> {
                Log.d(TAG, "New Chat Button Clicked")
                val intentToLoginPage = Intent(this@OpenChatsActivity, FriendsActivity::class.java)
                startActivity(intentToLoginPage)
            }
            R.id.top_chats_videos -> {
                Log.d(TAG, "Video Feed Button Clicked")
                val intentToHomePage = Intent(this@OpenChatsActivity, HomePageActivity::class.java)
                intentToHomePage.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intentToHomePage)
            }
            R.id.top_chats_profile -> {
                Log.d(TAG, "Profile Button Clicked")
                val intentToProfilePage = Intent(this@OpenChatsActivity, ProfileActivity::class.java)
                intentToProfilePage.putExtra("userID", ApplicationContext.getConnectedUsername())
                startActivity(intentToProfilePage)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun logout(){
        val serverURL = "https://serene-shelf-10674.herokuapp.com/logout"
        Fuel.post(serverURL)
            .jsonBody(
                "{ \"device\" : \"${ApplicationContext.getDeviceID()}\"}"
            )
            .response { request, response, result ->
                when (result){
                    is Result.Success -> {
                        ApplicationContext.LogUserOut()
                        val intentToLoginPage = Intent(this@OpenChatsActivity, LoginActivity::class.java)
                        intentToLoginPage.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        toastMessage("Correctly Logged Out")
                        startActivity(intentToLoginPage)
                    }
                    is Result.Failure -> {
                        val builder = AlertDialog.Builder(this@OpenChatsActivity)
                        builder.setTitle("Error")
                        builder.setMessage("Something went terribly wrong.\nPlease try Again.")
                        val dialog: AlertDialog = builder.create()
                        dialog.show()
                    }

                }
            }

    }

    private fun refreshRecView() {
        openChatsAdapter.clear()
        openChatsMap.values.forEach{
            openChatsAdapter.add(it)
        }
    }

    private fun listenForChats(){
        val userID = ApplicationContext.getConnectedUsername()
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$userID")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                receiveUpdateView(chatMessage, p0.key!!)
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                receiveUpdateView(chatMessage, p0.key!!)
            }

            override fun onChildRemoved(p0: DataSnapshot) {}
            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
        })
    }

    private fun receiveUpdateView(chatMessage: ChatMessage, key: String) {
        val myID = ApplicationContext.getConnectedUsername()
        var friendID = chatMessage.fromID
        if (chatMessage.fromID == myID) friendID = chatMessage.toID
        val text = chatMessage.text
        val time = chatMessage.timestamp
        val friend = Friend(friendID,"",friendID)
        //getFriendFromFirebase() para obtener a mi amigo de verdad!
        val modelChat = ModelChat(OpenChat(text, friend, time))
        openChatsMap[key]= modelChat
        refreshRecView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu_open_chats_page, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun toastMessage(message: String) {
        Toast.makeText(this@OpenChatsActivity, message, Toast.LENGTH_LONG).show()
    }
}