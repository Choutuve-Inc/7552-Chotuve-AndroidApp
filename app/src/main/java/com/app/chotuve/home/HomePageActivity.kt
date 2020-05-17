package com.app.chotuve.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.chotuve.R
import com.app.chotuve.context.ApplicationContext
import com.app.chotuve.login.LoginActivity
import com.app.chotuve.upload.UploadActivity
import kotlinx.android.synthetic.main.activity_home_page.*

class HomePageActivity  : AppCompatActivity() {

    private val TAG: String = "Home Screen"
    private lateinit var videoFeedAdapter: VideoFeedRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)
        initRecycleView()
        addDataSet()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu_home_page, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId){
            R.id.top_home_page_log_out -> {
                Log.d(TAG, "Log Out Button Clicked")
                ApplicationContext.LogUserOut()
                val intentToLoginPage = Intent(this@HomePageActivity, LoginActivity::class.java)
                toastMessage("Correctly Logged Out")
                startActivity(intentToLoginPage)
            }
            R.id.top_home_upload -> {
                Log.d(TAG, "Upload Button Clicked")
                val intentToUploadPage = Intent(this@HomePageActivity, UploadActivity::class.java)
                startActivity(intentToUploadPage)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initRecycleView(){
        rec_home_feed.apply {
            layoutManager = LinearLayoutManager(this@HomePageActivity)
            val topSpacingDecoration = TopSpacingItemDecoration(15)
            addItemDecoration(topSpacingDecoration)
            videoFeedAdapter = VideoFeedRecyclerAdapter()
            adapter = videoFeedAdapter
        }
    }

    private fun addDataSet(){
        val data = VideoDataSource.createDataSet()
        videoFeedAdapter.submitList(data)
    }

    private fun toastMessage(message: String) {
        Toast.makeText(this@HomePageActivity, message, Toast.LENGTH_LONG).show()
    }

}