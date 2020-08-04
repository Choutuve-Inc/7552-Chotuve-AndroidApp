package com.app.chotuve.video

import android.app.AlertDialog
import android.content.DialogInterface
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.chotuve.R
import com.app.chotuve.context.ApplicationContext
import com.app.chotuve.friendlist.FriendsDataSource
import com.app.chotuve.friendlist.ModelFriend
import com.app.chotuve.utils.JSONArraySorter
import com.app.chotuve.utils.TopSpacingItemDecoration
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_video.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class VideoActivity : AppCompatActivity() {

    private var playbackPosition = 0
    private var rtStreamUrl = ""
    private lateinit var mediaController: MediaController
    private val TAG: String = "Video Screen"
    private val commentsAdapter = GroupAdapter<GroupieViewHolder>()
    private lateinit var vidID: String
    private lateinit var vidUserID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        rtStreamUrl = intent.getStringExtra("videoURL")
        txt_video_title.text = intent.getStringExtra("title")
        txt_video_user.text = intent.getStringExtra("username")
        txt_video_date.text = intent.getStringExtra("date")
        txt_video_desc.text = intent.getStringExtra("description")
        vidUserID = intent.getStringExtra("userID")
        vidID = intent.getIntExtra("videoID", -1).toString()

        if (ApplicationContext.getConnectedUsername() == vidUserID){
            btn_video_add_friend.isEnabled = false
            btn_video_add_friend.visibility = View.INVISIBLE
        }

        val btnAddFriend: Button = findViewById(R.id.btn_video_add_friend)
        val btnUpvote: Button = findViewById(R.id.btn_video_upvote)
        val btnDownvote: Button = findViewById(R.id.btn_video_downvote)
        val btnPostComment: Button = findViewById(R.id.btn_video_send)
        btnAddFriend.isEnabled = true
        if (txt_video_user.text == ApplicationContext.getConnectedUsername()){
            //btnAddFriend.isEnabled = false
        }

        btnAddFriend.setOnClickListener {
            Log.d(TAG, "Add Friend Button Clicked")
            showAddFriendDialog()
        }
        updateLikeCount()

        btnUpvote.setOnClickListener {
            Log.d(TAG, "Upvoted!")
            votePositiveOnVideo(true)
        }

        btnDownvote.setOnClickListener {
            Log.d(TAG, "Downvoted!")
            votePositiveOnVideo(false)
        }

        btnPostComment.setOnClickListener {
            Log.d(TAG, "Comment SEND clicked")
            if (txt_video_comment.text.toString() != ""){
                performPostComment()
                txt_video_comment.text.clear()
            }
        }

        mediaController = MediaController(this)

        vid_video_video_player.setOnPreparedListener{
            mediaController.setAnchorView(frm_video_container)
            vid_video_video_player.setMediaController(mediaController)
            vid_video_video_player.seekTo(playbackPosition)
            vid_video_video_player.start()
        }

        vid_video_video_player.setOnInfoListener{ player, what, extras ->
            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START)
                bar_video_progress.visibility = View.INVISIBLE
            true
        }

        rec_video_comments.adapter = commentsAdapter
        rec_video_comments.apply {
                val topSpacingDecoration = TopSpacingItemDecoration(12)
                addItemDecoration(topSpacingDecoration)
            }

        refreshComments()

    }

    private fun refreshComments() {
        CoroutineScope(Dispatchers.IO).launch {
            getData()
        }
    }

    private suspend fun getData() {
        val commentURL = "${ApplicationContext.getServerURL()}/videos/$vidID/comments"
        var vidComments = CommentsDataSource.getCommentsFromHTTP(commentURL)
        withContext(Dispatchers.Main){
            commentsAdapter.clear()
        }
        vidComments = JSONArraySorter.sortJSONArrayByFirstLevelIntField(vidComments, "id")
        for (i in 0 until vidComments.length()) {
            val item = vidComments.getJSONObject(i)
            val username = item["user"] as String
            val text = item["text"] as String
            Thread.sleep(10) //Needed for correct order on the comment list
            CoroutineScope(Dispatchers.IO).launch{
                addVideoToRecyclerView(ModelComment(Comment(username,text)))
            }
        }
        Log.d(TAG, "Comments got: ${vidComments.length()}.")
    }

    private suspend fun addVideoToRecyclerView(comment: ModelComment){
        withContext(Dispatchers.Main){
            Log.d(TAG, "Friend add Item Sent.")
            commentsAdapter.add(comment)
            commentsAdapter.notifyDataSetChanged()
        }
    }

    override fun onStart() {
        super.onStart()

        val uri = Uri.parse(rtStreamUrl)
        vid_video_video_player.setVideoURI(uri)
        bar_video_progress.visibility = View.VISIBLE
    }

    override fun onPause() {
        super.onPause()

        vid_video_video_player.pause()
        playbackPosition = vid_video_video_player.currentPosition
    }

    override fun onStop() {
        vid_video_video_player.stopPlayback()

        super.onStop()
    }

    private fun toastMessage(message: String) {
        Toast.makeText(this@VideoActivity, message, Toast.LENGTH_LONG).show()
    }

    private fun votePositiveOnVideo(value: Boolean){
        val videoLikesURL = "${ApplicationContext.getServerURL()}/videos/$vidID/likes"
        videoLikesURL.httpPost()
            .appendHeader("user", ApplicationContext.getConnectedUsername())
            .appendHeader("token", ApplicationContext.getConnectedToken())
            .jsonBody(
                "{" +
                        " \"user\": \"${ApplicationContext.getConnectedUsername()}\", " +
                        " \"token\": \"${ApplicationContext.getConnectedToken()}\", " +
                        " \"value\" : $value " +
                        "}"
            )
            .response { request, response, result ->
                when (result){
                    is Result.Success -> {
                        Log.d(TAG, "Vote posted correctly")
                        updateLikeCount()
                    }
                    is Result.Failure -> {
                        Log.d(TAG, "Error posting vote")
                        Log.d(TAG, "Error code: ${response.statusCode}")
                        Log.d(TAG, "Message: ${result.error}")
                    }
                }
            }
    }

    private fun updateLikeCount(){
        val videoLikesURL = "${ApplicationContext.getServerURL()}/videos/$vidID/likes"
        videoLikesURL.httpGet()
            .appendHeader("user", ApplicationContext.getConnectedUsername())
            .appendHeader("token", ApplicationContext.getConnectedToken())
            .responseJson{request, response, result ->
                when (result){
                    is Result.Success -> {
                        Log.d(TAG, "video reactions successfully retrieved. Showing...")
                        val body = response.body()
                        val json = JSONObject(body.asString("application/json"))
                        val react = json["reactions"] as JSONObject
                        val upvoteCount = react["likes"] as Int
                        val downvoteCount = react["dislike"] as Int
                        modifyVoteCount(upvoteCount, downvoteCount)

                    }
                    is Result.Failure -> {
                        Log.d(TAG, "Error loading reactions")
                        Log.d(TAG, "Error code: ${response.statusCode}")
                        Log.d(TAG, "Message: ${result.error}")
                    }
                }
            }
    }

    private fun modifyVoteCount(upvote: Int, downvote: Int){
        var upvoteCount: String
        var downvoteCount: String
        if(upvote>0){
            lbl_video_upvote_count.setTextColor(0xFF00A800.toInt())
            upvoteCount = String.format("%03d", upvote)
        }else{
            lbl_video_upvote_count.setTextColor(-0x1000000)
            upvoteCount = "000"
        }

        if (downvote>0){
            lbl_video_downvote_count.setTextColor(-0x10000)
            downvoteCount = String.format("%04d", -downvote)
        }else{
            lbl_video_downvote_count.setTextColor(-0x1000000)
            downvoteCount = "000"
        }

        lbl_video_upvote_count.text = upvoteCount
        lbl_video_downvote_count.text = downvoteCount
    }

    private fun showAddFriendDialog(){
        // Late initialize an alert dialog object
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Friend Request")
        builder.setMessage("Do you want to add ${txt_video_user.text} as a friend?")

        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
            when(which){
                DialogInterface.BUTTON_POSITIVE -> {
                    sendFriendRequest()
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                }
            }
        }
        builder.setPositiveButton("YES",dialogClickListener)
        builder.setNegativeButton("NO",dialogClickListener)
        dialog = builder.create()
        dialog.show()
    }

    private fun sendFriendRequest() {
        Fuel.post("${ApplicationContext.getServerURL()}/request")
            .appendHeader("user", ApplicationContext.getConnectedUsername())
            .appendHeader("token", ApplicationContext.getConnectedToken())
            .jsonBody(
                "{ \"user\" : \"${ApplicationContext.getConnectedUsername()}\"," +
                        " \"token\" : \"${ApplicationContext.getConnectedToken()}\"," +
                        "\"id\" : \"$vidUserID\"" +
                        "}"
            )
            .response { request, response, result ->
                when(result) {
                    is Result.Success -> {
                        Log.d(TAG, "HTTP Success [sendFriendRequest]")
                    }
                    is Result.Failure -> {
                        //Look up code and choose what to do.
                        Log.d(TAG, "Error Sending Friend Request.")
                        Log.d(TAG, "Error Code: ${response.statusCode}")
                        Log.d(TAG, "Error Message: ${result.error}")
                        if (response.statusCode == 404){
                            val builder = AlertDialog.Builder(this@VideoActivity)
                            builder.setTitle("Already a Friend")
                            builder.setMessage("That User is already in your Friend List!")
                            val dialog: AlertDialog = builder.create()
                            dialog.show()
                        }
                    }
                }
            }
    }

    private fun performPostComment() {
        val commentURL = "${ApplicationContext.getServerURL()}/videos/$vidID/comments"
        commentURL.httpPost()
            .appendHeader("user", ApplicationContext.getConnectedUsername())
            .appendHeader("token", ApplicationContext.getConnectedToken())
            .jsonBody(
                "{ \"user\" : \"${ApplicationContext.getConnectedUsername()}\"," +
                        " \"token\" : \"${ApplicationContext.getConnectedToken()}\"," +
                        "\"text\" : \"${txt_video_comment.text}\"" +
                        "}"
            )
            .response { request, response, result ->
                when (result) {
                    is Result.Success -> {
                        Log.d(TAG, "HTTP Success [performPostComment]")
                        refreshComments()
                    }
                    is Result.Failure -> {
                        //Look up code and choose what to do.
                        Log.d(TAG, "Error posting Comment.")
                        Log.d(TAG, "Error Code: ${response.statusCode}")
                        Log.d(TAG, "Error Message: ${result.error}")
                    }
                }
            }
    }
}
