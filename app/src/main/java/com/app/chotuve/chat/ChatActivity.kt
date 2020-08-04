package com.app.chotuve.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.app.chotuve.R
import com.app.chotuve.context.ApplicationContext
import com.app.chotuve.friendlist.Friend
import com.app.chotuve.friendlist.FriendsActivity
import com.app.chotuve.friendlist.FriendsDataSource
import com.app.chotuve.home.HomePageActivity
import com.app.chotuve.profile.ProfileActivity
import com.app.chotuve.utils.TopSpacingItemDecoration
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.layout_chat_list_item.view.*
import org.json.JSONObject

class ChatActivity : AppCompatActivity() {
    private val TAG = "Chat Activity"
    private val FRIEND_KEY = "FRIEND_KEY"
    private lateinit var chattingFriend: Friend
    private val chatAdapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chattingFriend = intent.getParcelableExtra<Friend>(FRIEND_KEY)


        supportActionBar?.title = "${chattingFriend.username}'s Chat"
        rec_chat_log.adapter = chatAdapter

        rec_chat_log.apply {
            val topSpacingDecoration = TopSpacingItemDecoration(16)
            addItemDecoration(topSpacingDecoration)
        }

        btn_chat_send.setOnClickListener {
            Log.d(TAG, "Send Button Clicked")
            if (txt_chat_enter_message.text.toString() != ""){
                //Save Message to Firebase
                //Update view
                performSendMessage()
                txt_chat_enter_message.text.clear()
            }
        }

        listenForMessages()
    }

    override fun onPause() {
        super.onPause()
        ApplicationContext.setShowNotifications(true)
    }

    override fun onResume() {
        super.onResume()
        ApplicationContext.setShowNotifications(false)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu_chat_page, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId){
            R.id.top_chat_profile -> {
                Log.d(TAG, "Profile Button Clicked")
                val intentToProfilePage = Intent(this@ChatActivity, ProfileActivity::class.java)
                intentToProfilePage.putExtra("userID", chattingFriend.userID)
                startActivity(intentToProfilePage)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun listenForMessages() {
        val fromID = ApplicationContext.getConnectedUsername()
        val toID = chattingFriend.userID
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromID/$toID")

        ref.addChildEventListener(object: ChildEventListener{

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)
                if(chatMessage!=null){
                    if (chatMessage.fromID == fromID ){
                        Log.d(TAG, "Chat Outgoing Message: ${chatMessage.text}")
                        chatAdapter.add(ModelChatOutgoingItem(chatMessage.text, null))
                    }else if (chatMessage.toID == fromID){
                        Log.d(TAG, "Chat Incoming Message: ${chatMessage.text}")
                        chatAdapter.add(ModelChatIncomingItem(chatMessage.text, chattingFriend.imageID))
                    }
                }
                rec_chat_log.smoothScrollToPosition(chatAdapter.itemCount)
            }

            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
            override fun onChildRemoved(p0: DataSnapshot) {}

        })
    }

    private fun performSendMessage() {
        val txt = txt_chat_enter_message.text.toString()
        val fromID = ApplicationContext.getConnectedUsername()
        val toID = chattingFriend.userID
        val fromRef = FirebaseDatabase.getInstance().getReference("/user-messages/$fromID/$toID").push()
        val toRef = FirebaseDatabase.getInstance().getReference("/user-messages/$toID/$fromID").push()

        val chatMessage = ChatMessage(fromRef.key!!, txt, fromID, toID, System.currentTimeMillis())
        fromRef.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved from chat message: ${fromRef.key}")
            }
        toRef.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved to chat message: ${toRef.key}")
            }

        //Open Chats View
        val ocFromRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromID/$toID")
        ocFromRef.setValue(chatMessage)

        val ocToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toID/$fromID")
        ocToRef.setValue(chatMessage)

        postNotification()
    }


    private fun postNotification(){
        val deviceId = ApplicationContext.getDeviceID()
        Log.d(TAG,"device: $deviceId")
        Fuel.post("${ApplicationContext.getServerURL()}/notifications")
            .appendHeader("user", ApplicationContext.getConnectedUsername())
            .appendHeader("token", ApplicationContext.getConnectedToken())
            .jsonBody(
                "{ \"idSender\" : \"${ApplicationContext.getConnectedUsername()}\"," +
                        " \"idReceiver\" : \"${chattingFriend.userID}\", " +
                        "\"message\" : \"${txt_chat_enter_message.text}\"" +
                        "}"
            )
            .response { request, response, result ->
                Log.d(TAG, request.toString())
                when (result) {

                    is Result.Success -> {
                        Log.d(TAG, "Successful Notification Send")
                    }
                    is Result.Failure -> {
                        Log.d(TAG, "Notification ERROR")


                        Log.d(TAG, "Unsuccessful Notification: ${response.statusCode}")
                    }
                }
            }
    }
}
