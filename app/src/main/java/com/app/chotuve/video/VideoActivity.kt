package com.app.chotuve.video

import android.app.AlertDialog
import android.content.DialogInterface
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.MediaController
import android.widget.Toast
import com.app.chotuve.R
import com.app.chotuve.context.ApplicationContext
import kotlinx.android.synthetic.main.activity_video.*

class VideoActivity : AppCompatActivity() {

    private var playbackPosition = 0
    private var rtStreamUrl = ""
    private lateinit var mediaController: MediaController
    private val TAG: String = "Video Screen"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        rtStreamUrl = intent.getStringExtra("videoURL")
        txt_video_title.text = intent.getStringExtra("title")
        txt_video_user.text = intent.getStringExtra("username")
        txt_video_date.text = intent.getStringExtra("date")
        txt_video_desc.text = intent.getStringExtra("description")

        val btnAddFriend: Button = findViewById(R.id.btn_video_add_friend)
        btnAddFriend.isEnabled = true
        if (txt_video_user.text == ApplicationContext.getConnectedUsername()){
            btnAddFriend.isEnabled = false
        }

        btnAddFriend.setOnClickListener {
            Log.d(TAG, "Add Friend Button Clicked")
            val isFriend = false
            if(isFriend){//Check if already friend
                showDeleteFriendDialog()
            }else {
                showAddFriendDialog()
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

    private fun showAddFriendDialog(){
        // Late initialize an alert dialog object
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Friend Request")
        builder.setMessage("Do you want to add ${txt_video_user.text} as a friend?")

        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
            when(which){
                DialogInterface.BUTTON_POSITIVE -> {
                    toastMessage("Yes button clicked.")
                    //Send friend request
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    toastMessage("No button clicked.")
                }
            }
        }
        builder.setPositiveButton("YES",dialogClickListener)
        builder.setNegativeButton("NO",dialogClickListener)
        dialog = builder.create()
        dialog.show()
    }

    private fun showDeleteFriendDialog(){
        // Late initialize an alert dialog object
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Friend Remove")
        builder.setMessage("Do you want to remove ${txt_video_user.text} from your friend list?")

        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
            when(which){
                DialogInterface.BUTTON_POSITIVE -> {
                    toastMessage("Yes button clicked.")
                    //Send friend request
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    toastMessage("No button clicked.")
                }
            }
        }
        builder.setPositiveButton("YES",dialogClickListener)
        builder.setNegativeButton("NO",dialogClickListener)
        dialog = builder.create()
        dialog.show()
    }
}
