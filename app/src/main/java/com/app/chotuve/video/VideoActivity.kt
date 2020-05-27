package com.app.chotuve.video

import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import com.app.chotuve.R
import kotlinx.android.synthetic.main.activity_video.*

class VideoActivity : AppCompatActivity() {

    private var playbackPosition = 0
    private var rtStreamUrl = ""
    private lateinit var mediaController: MediaController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        rtStreamUrl = intent.getStringExtra("videoURL")
        txt_video_title.text = intent.getStringExtra("title")
        txt_video_user.text = intent.getStringExtra("username")
        txt_video_date.text = intent.getStringExtra("date")

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
}
