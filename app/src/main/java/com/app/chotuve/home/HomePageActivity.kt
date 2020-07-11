package com.app.chotuve.home

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.chotuve.R
import com.app.chotuve.openchats.OpenChatsActivity
import com.app.chotuve.context.ApplicationContext
import com.app.chotuve.login.LoginActivity
import com.app.chotuve.upload.UploadActivity
import com.app.chotuve.utils.TopSpacingItemDecoration
import com.app.chotuve.video.VideoActivity
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.result.Result
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_home_page.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomePageActivity  : AppCompatActivity(), VideoFeedRecyclerAdapter.OnVideoListener {

    private val TAG: String = "Home Screen"
    private lateinit var videoFeedAdapter: VideoFeedRecyclerAdapter
    private var videoItems: ArrayList<ModelVideo> = ArrayList()

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
                logout()
            }
            R.id.top_home_upload -> {
                Log.d(TAG, "Upload Button Clicked")
                val intentToUploadPage = Intent(this@HomePageActivity, UploadActivity::class.java)
                startActivity(intentToUploadPage)
            }
            R.id.top_home_chats -> {
                Log.d(TAG, "Chats Button Clicked")
                val intentToUploadPage = Intent(this@HomePageActivity, OpenChatsActivity::class.java)
                intentToUploadPage.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intentToUploadPage)
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
                        val intentToLoginPage = Intent(this@HomePageActivity, LoginActivity::class.java)
                        intentToLoginPage.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                        toastMessage("Correctly Logged Out")
                        startActivity(intentToLoginPage)
                    }
                    is Result.Failure -> {
                        val builder = AlertDialog.Builder(this@HomePageActivity)
                        builder.setTitle("Error")
                        builder.setMessage("Something went terribly wrong.\nPlease try Again.")
                        val dialog: AlertDialog = builder.create()
                        dialog.show()
                    }

                }
            }

    }

    private fun initRecycleView(){
        rec_home_feed.apply {
            layoutManager = LinearLayoutManager(this@HomePageActivity)
            val topSpacingDecoration =
                TopSpacingItemDecoration(15)
            addItemDecoration(topSpacingDecoration)

            videoFeedAdapter = VideoFeedRecyclerAdapter(this@HomePageActivity)
            adapter = videoFeedAdapter
        }
    }

    private fun addDataSet() {
        CoroutineScope(IO).launch {
            getData()
        }
    }

    private suspend fun getData() {
        val videos = VideoDataSource.getVideosFromHTTP()
        for (i in 0 until videos.length()) {
            val item = videos.getJSONObject(i)
            val date = item["date"] as String
            val title = item["title"] as String
            val user = item["user"] as String
            val thumbURL: String = item["thumbnail"] as String
            val vidID: Int = item["id"] as Int
            CoroutineScope(IO).launch{
                val video = VideoDataSource.getVideoFromFirebase(date, title, user, thumbURL, vidID)
                addVideoToRecyclerView(video)
            }
        }
        Log.d(TAG, "Videos got: ${videos.length()}.")
    }

    private suspend fun addVideoToRecyclerView(video:ModelVideo){
        withContext(Main){
            Log.d(TAG, "Add Item Sent.")
            videoFeedAdapter.addItem(video)
            videoItems.add(video)
            videoFeedAdapter.notifyDataSetChanged()
        }
    }


    private fun toastMessage(message: String) {
        Toast.makeText(this@HomePageActivity, message, Toast.LENGTH_LONG).show()
    }

    override fun onVideoClick(position: Int) {
        CoroutineScope(IO).launch {
            val selectedVideo = videoItems[position]
            val storage = FirebaseStorage.getInstance().reference
            val video = VideoDataSource.getSingleVideoFromHTTP(selectedVideo.videoID)

            storage.child("videos/").child(video["url"] as String).downloadUrl
                .addOnSuccessListener {
                    var videoURL = it.toString()
                    val intentToVideo = Intent(this@HomePageActivity, VideoActivity::class.java)
                    intentToVideo.putExtra("videoURL", videoURL)
                    intentToVideo.putExtra("title", selectedVideo.title)
                    intentToVideo.putExtra("username", selectedVideo.username)
                    intentToVideo.putExtra("date", selectedVideo.date)
                    intentToVideo.putExtra("description", video["description"] as String)
                    intentToVideo.putExtra("videoID", selectedVideo.videoID)
                    startActivity(intentToVideo)
                    Log.d(TAG, "Success $videoURL.")
                }.addOnFailureListener {
                    Log.d(TAG, "Error obtaining Video: ${it.message}.")
                }
            Log.d(TAG, "Video clicked")
        }
    }

}