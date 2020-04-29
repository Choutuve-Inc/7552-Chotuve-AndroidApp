package com.app.chotuve.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.chotuve.login.LoginActivity
import com.app.chotuve.R
import com.app.chotuve.upload.UploadActivity
import kotlinx.android.synthetic.main.activity_home_page.*

class HomePageActivity  : AppCompatActivity() {

    private val TAG: String = "Home Screen"
    private lateinit var videoFeedAdapter: VideoFeedRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        initRecycleView()
        val btnLogOut: Button = findViewById(R.id.btn_log_out)
        val btnUpload: Button = findViewById(R.id.btn_upload)

        addDataSet()
        btnLogOut.setOnClickListener(View.OnClickListener {
            Log.d(TAG, "Log Out Button Clicked")
            val intentToLoginPage = Intent(this@HomePageActivity, LoginActivity::class.java)
            toastMessage("Correctly Logged Out")
            startActivity(intentToLoginPage)
        })

        btnUpload.setOnClickListener(View.OnClickListener {
            Log.d(TAG, "Upload Button Clicked")
            val intentToUploadPage = Intent(this@HomePageActivity, UploadActivity::class.java)
            startActivity(intentToUploadPage)
        })
    }

    private fun initRecycleView(){
        recycler_view_video_feed.apply {
            layoutManager = LinearLayoutManager(this@HomePageActivity)
            val topSpacingDecoration = TopSpacingItemDecoration(15)
            addItemDecoration(topSpacingDecoration)
            videoFeedAdapter = VideoFeedRecyclerAdapter()
            adapter = videoFeedAdapter
        }
    }

    private fun addDataSet(){
        val data = DummyVideoDataSource.createDummyDataSet()
        videoFeedAdapter.submitList(data)
    }

    private fun toastMessage(message: String) {
        Toast.makeText(this@HomePageActivity, message, Toast.LENGTH_LONG).show()
    }

    private fun getDefaultPreview(): Int {
        return resources.getIdentifier("@drawable/video_no_thumbnail", null, this.packageName)
    }
}