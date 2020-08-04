package com.app.chotuve.profile

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.chotuve.R
import com.app.chotuve.context.ApplicationContext
import com.app.chotuve.home.ModelVideo
import com.app.chotuve.home.VideoDataSource
import com.app.chotuve.utils.TopSpacingItemDecoration
import com.app.chotuve.video.VideoActivity
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.layout_profile_popup.*
import kotlinx.android.synthetic.main.layout_profile_popup.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.*

class ProfileActivity : AppCompatActivity(), VideoProfileRecyclerAdapter.OnVideoListener {
    private val TAG: String = "Profile Screen"
    private lateinit var userID: String
    private var selectedPhotoUri: Uri? = null
    private lateinit var videoListAdapter: VideoProfileRecyclerAdapter
    private var videoItems: ArrayList<ModelVideo> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        userID = intent.getStringExtra("userID")
        val currentUser = ApplicationContext.getConnectedUsername()
        if (currentUser != userID){
            disableEdition()
        }

        btn_profile_image.setOnClickListener{
            Log.d(TAG, "Photo Button Clicked")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        btn_profile_edit_username.setOnClickListener {
            Log.d(TAG, "Username edit clicked")
            openUsernameDialog()
        }

        btn_profile_edit_mail.setOnClickListener {
            Log.d(TAG, "Email edit clicked")
            openEmailDialog()
        }

        btn_profile_edit_phone.setOnClickListener {
            Log.d(TAG, "Phone edit clicked")
            openPhoneDialog()
        }
        //INFO
        setCurrentUserData()

        //VIDEOS
        initVideosRecycleView()
        setCurrentUserVideos()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    //INFO

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            selectedPhotoUri = data.data
            changePhoto()
        }
    }

    private fun changePhoto(){
        bar_profile_progress.visibility = View.VISIBLE
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/userPic/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener{
                ref.downloadUrl
                    .addOnSuccessListener {
                        val photoUrl = it.toString()
                        FuelManager.instance.forceMethods = true
                        Fuel.patch("${ApplicationContext.getServerURL()}/users/${ApplicationContext.getConnectedUsername()}")
                            .appendHeader("user", ApplicationContext.getConnectedUsername())
                            .appendHeader("token", ApplicationContext.getConnectedToken())
                            .jsonBody(
                                "{" +
                                        " \"Nimage\" : \"${photoUrl}\"" +
                                        "}"
                            )
                            .responseJson { request, response, result ->
                                when (result) {
                                    is Result.Success -> {
                                        Log.d(TAG, "HTTP Success [getUserPhoto]")
                                        val body = response.body()
                                        val json = JSONObject(body.asString("application/json"))

                                        Picasso.get().load(json.getString("photoURL")).into(img_profile_user)
                                        bar_profile_progress.visibility = View.INVISIBLE
                                        toastMessage("Your user photo was updated!")
                                    }
                                    is Result.Failure -> {
                                        //Look up code and choose what to do.
                                        Log.d(TAG, "Error obtaining User.")
                                        Log.d(TAG, "Error Code: ${response.statusCode}")
                                        Log.d(TAG, "Error Message: ${result.error}")
                                        Log.d(TAG, "request was: $request")
                                        bar_profile_progress.visibility = View.INVISIBLE
                                    }
                                }
                            }
                    }
                    .addOnFailureListener{
                        Log.d(TAG, "Error obtaining URL")
                        bar_profile_progress.visibility = View.INVISIBLE
                    }
            }
            .addOnFailureListener {
                Log.d(TAG, "Error uploading photo")
                bar_profile_progress.visibility = View.INVISIBLE
            }
    }

    private fun setCurrentUserData(){
        val userURL = "${ApplicationContext.getServerURL()}/users/${userID}"
        Log.d(TAG, "setCurrentUserData: ${userURL}")
        userURL.httpGet()
            .appendHeader("user", ApplicationContext.getConnectedUsername())
            .appendHeader("token", ApplicationContext.getConnectedToken())
            .jsonBody(
                "{ \"user\" : \"${ApplicationContext.getConnectedUsername()}\"," +
                        " \"token\" : \"${ApplicationContext.getConnectedToken()}\"" +
                        "}"
            )
            .responseJson { request, response, result ->
                when (result) {
                    is Result.Success -> {
                        Log.d(TAG, "HTTP Success [setCurrentUserData]")
                        val body = response.body()
                        val user = JSONObject(body.asString("application/json"))
                        Picasso.get().load(user.getString("photoURL")).into(img_profile_user)
                        val username = user.getString("displayName")
                        supportActionBar!!.title = "${username}'s Profile"
                        lbl_profile_username_value.text = username
                        lbl_profile_email_value.text = user.getString("email")
                        lbl_profile_phone_value.text = user.getString("phoneNumber")

                    }
                    is Result.Failure -> {
                        //Look up code and choose what to do.
                        Log.d(TAG, "Error obtaining connected user.")
                        Log.d(TAG, "Error code: ${response.statusCode}")
                        Log.d(TAG, "Error Message: ${result.error}")
                    }
                }
            }
    }

    private fun openUsernameDialog(){
        val editAlert = AlertDialog.Builder(this).create()
        val editView = layoutInflater.inflate(R.layout.layout_profile_popup, null)
        editView.lbl_profile_popup_text.text ="Fill below with your New Username to be displayed:"
        editView.txt_profile_popup_input.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
        editAlert.setView(editView)
        editAlert.setButton(AlertDialog.BUTTON_POSITIVE, "ACCEPT") { _, _ ->
            if (validateUsername(editAlert.txt_profile_popup_input.text.toString())) changeUsername(editAlert.txt_profile_popup_input.text.toString())
        }
        editAlert.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", DialogInterface.OnClickListener { dialog, which ->  })
        editAlert.show()
    }

    private fun validateUsername(username: String): Boolean{
        val validStrings = Regex("([A-Za-z0-9\\-\\_]+)")
        if (username.isBlank() || !validStrings.matches(username)){
            toastMessage("Invalid Username")
            return false
        }
        return true
    }

    private fun changeUsername(username: String){
        bar_profile_progress.visibility = View.VISIBLE
        FuelManager.instance.forceMethods = true
        Fuel.patch("${ApplicationContext.getServerURL()}/users/${ApplicationContext.getConnectedUsername()}")
            .appendHeader("user", ApplicationContext.getConnectedUsername())
            .appendHeader("token", ApplicationContext.getConnectedToken())
            .jsonBody(
                "{" +
                        " \"Nusername\" : \"${username}\"" +
                        "}"
            )
            .responseJson { request, response, result ->
                when (result) {
                    is Result.Success -> {
                        Log.d(TAG, "HTTP Success [changeUsername]")
                        val body = response.body()
                        val json = JSONObject(body.asString("application/json"))
                        val username = json.getString("displayName")
                        lbl_profile_username_value.text = username
                        supportActionBar!!.title = "${username}'s Profile"
                        bar_profile_progress.visibility = View.INVISIBLE
                        toastMessage("Your Username was updated!")
                    }
                    is Result.Failure -> {
                        //Look up code and choose what to do.
                        Log.d(TAG, "Error obtaining User.")
                        Log.d(TAG, "Error Code: ${response.statusCode}")
                        Log.d(TAG, "Error Message: ${result.error}")
                        Log.d(TAG, "request was: $request")
                        bar_profile_progress.visibility = View.INVISIBLE
                    }
                }
            }
    }

    private fun openEmailDialog(){
        val editAlert = AlertDialog.Builder(this).create()
        val editView = layoutInflater.inflate(R.layout.layout_profile_popup, null)
        editView.lbl_profile_popup_text.text ="Fill below with your New Email Address:"
        editView.txt_profile_popup_input.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        editAlert.setView(editView)
        editAlert.setButton(AlertDialog.BUTTON_POSITIVE, "ACCEPT") { _, _ ->
            if (validateEmail(editAlert.txt_profile_popup_input.text.toString())) changeEmail(editAlert.txt_profile_popup_input.text.toString())
        }
        editAlert.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", DialogInterface.OnClickListener { dialog, which ->  })
        editAlert.show()
    }

    private fun validateEmail(mail: String): Boolean{
        if (mail.isBlank()){
            toastMessage("Invalid Email Address")
            return false
        }
        return true
    }

    private fun changeEmail(email: String){
        bar_profile_progress.visibility = View.VISIBLE
        FuelManager.instance.forceMethods = true
        Fuel.patch("${ApplicationContext.getServerURL()}/users/${ApplicationContext.getConnectedUsername()}")
            .appendHeader("user", ApplicationContext.getConnectedUsername())
            .appendHeader("token", ApplicationContext.getConnectedToken())
            .jsonBody(
                "{" +
                        " \"Nemail\" : \"${email}\"" +
                        "}"
            )
            .responseJson { request, response, result ->
                when (result) {
                    is Result.Success -> {
                        Log.d(TAG, "HTTP Success [changeEmail]")
                        val body = response.body()
                        val json = JSONObject(body.asString("application/json"))
                        val email = json.getString("email")
                        lbl_profile_email_value.text = email
                        bar_profile_progress.visibility = View.INVISIBLE
                        toastMessage("Your Email was updated!")
                    }
                    is Result.Failure -> {
                        //Look up code and choose what to do.
                        when (response.statusCode) {
                            406 -> {
                                toastMessage("Invalid Email Address")
                            }
                            409 ->{
                                toastMessage("There's another Account registered with that Email Address")
                            }
                            else -> {
                                Log.d(TAG, "Error obtaining User.")
                                Log.d(TAG, "Error Code: ${response.statusCode}")
                                Log.d(TAG, "Error Message: ${result.error}")
                                Log.d(TAG, "request was: $request")
                            }
                        }
                        bar_profile_progress.visibility = View.INVISIBLE
                    }
                }
            }
    }

    private fun openPhoneDialog(){
        val editAlert = AlertDialog.Builder(this).create()
        val editView = layoutInflater.inflate(R.layout.layout_profile_popup, null)
        editView.lbl_profile_popup_text.text ="Fill below with your New Phone Number:"
        editView.txt_profile_popup_input.inputType = InputType.TYPE_CLASS_PHONE
        editAlert.setView(editView)
        editAlert.setButton(AlertDialog.BUTTON_POSITIVE, "ACCEPT") { _, _ ->
            if (validatePhone(editAlert.txt_profile_popup_input.text.toString())) changePhone(editAlert.txt_profile_popup_input.text.toString())
        }
        editAlert.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", DialogInterface.OnClickListener { dialog, which ->  })
        editAlert.show()
    }

    private fun validatePhone(phone: String): Boolean{
        if (phone.isBlank()){
            toastMessage("Invalid Phone Number")
            return false
        }
        return true
    }

    private fun changePhone(phoneNumber: String){
        bar_profile_progress.visibility = View.VISIBLE
        FuelManager.instance.forceMethods = true
        Fuel.patch("${ApplicationContext.getServerURL()}/users/${ApplicationContext.getConnectedUsername()}")
            .appendHeader("user", ApplicationContext.getConnectedUsername())
            .appendHeader("token", ApplicationContext.getConnectedToken())
            .jsonBody(
                "{" +
                        " \"Nphone\" : \"${phoneNumber}\"" +
                        "}"
            )
            .responseJson { request, response, result ->
                when (result) {
                    is Result.Success -> {
                        Log.d(TAG, "HTTP Success [changePhone]")
                        val body = response.body()
                        val json = JSONObject(body.asString("application/json"))
                        val phone = json.getString("phoneNumber")
                        lbl_profile_phone_value.text = phone
                        bar_profile_progress.visibility = View.INVISIBLE
                        toastMessage("Your Phone was updated!")
                    }
                    is Result.Failure -> {
                        //Look up code and choose what to do.
                        val statusCode = response.statusCode

                        when (statusCode) {
                            406 -> {
                                toastMessage("Invalid Phone Number")
                            }
                            409 ->{
                                toastMessage("There's another Account registered with that Number")
                            }
                            else -> {
                                Log.d(TAG, "Error obtaining User.")
                                Log.d(TAG, "Error Code: ${response.statusCode}")
                                Log.d(TAG, "Error Message: ${result.error}")
                                Log.d(TAG, "request was: $request")
                            }
                        }
                        bar_profile_progress.visibility = View.INVISIBLE
                    }
                }
            }
    }

    //VIDEOS

    private fun initVideosRecycleView(){
        rec_profile_videos.apply {
            layoutManager = LinearLayoutManager(this@ProfileActivity)
            val topSpacingDecoration =
                TopSpacingItemDecoration(15)
            addItemDecoration(topSpacingDecoration)

            videoListAdapter = VideoProfileRecyclerAdapter(this@ProfileActivity)
            adapter = videoListAdapter
        }
    }

    private fun setCurrentUserVideos(){
        CoroutineScope(Dispatchers.IO).launch {
            getVideosData()
        }
    }

    private suspend fun getVideosData() {
        var videos = VideoDataSource.getUserVideosFromHTTP(userID)
        for (i in 0 until videos.length()) {
            val item = videos.getJSONObject(i)
            val date = item["date"] as String
            val title = item["title"] as String
            val userID = item["user"] as String
            val thumbURL: String = item["thumbnail"] as String
            val vidID: Int = item["id"] as Int
            Thread.sleep(10) //Needed for correct order on the video feed list
            CoroutineScope(Dispatchers.IO).launch{
                val username = getUsernameFromHTTP(userID)
                val video = VideoDataSource.getVideoFromFirebase(date, title, username, thumbURL, vidID, userID)
                addVideoToRecyclerView(video)
            }
        }
        Log.d(TAG, "Videos got: ${videos.length()}.")
    }

    private fun getUsernameFromHTTP(videoUserID: String): String {
        val serverURL = "${ApplicationContext.getServerURL()}/users/${videoUserID}"
        val (request, response, result) = serverURL.httpGet()
            .appendHeader("user", ApplicationContext.getConnectedUsername())
            .appendHeader("token", ApplicationContext.getConnectedToken())
            .jsonBody(
                "{ \"user\" : \"${ApplicationContext.getConnectedUsername()}\"," +
                        " \"token\" : \"${ApplicationContext.getConnectedToken()}\"" +
                        "}"
            )
            .responseJson()
        when (result) {
            is Result.Success -> {
                Log.d(TAG, "HTTP Success [getUsernameFromHTTP]")
                val body = response.body()
                val json = JSONObject(body.asString("application/json"))
                return json.getString("displayName")
            }
            is Result.Failure -> {
                //Look up code and choose what to do.
                Log.d(TAG, "Error obtaining User by id ${userID}.")
                return userID
            }
        }
    }

    private suspend fun addVideoToRecyclerView(video:ModelVideo){
        withContext(Dispatchers.Main){
            Log.d(TAG, "Add Item Sent.")
            videoListAdapter.addItem(video)
            videoItems.add(video)
            videoListAdapter.notifyDataSetChanged()
        }
    }

    override fun onVideoClick(position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val selectedVideo = videoItems[position]
            val storage = FirebaseStorage.getInstance().reference
            val video = VideoDataSource.getSingleVideoFromHTTP(selectedVideo.videoID)

            storage.child("videos/").child(video["url"] as String).downloadUrl
                .addOnSuccessListener {
                    var videoURL = it.toString()
                    val intentToVideo = Intent(this@ProfileActivity, VideoActivity::class.java)
                    intentToVideo.putExtra("videoURL", videoURL)
                    intentToVideo.putExtra("title", selectedVideo.title)
                    intentToVideo.putExtra("username", selectedVideo.username)
                    intentToVideo.putExtra("userID", selectedVideo.userID)
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


    private fun disableEdition(){
        btn_profile_image.isEnabled = false
        img_profile_edit_username.visibility = View.INVISIBLE
        btn_profile_edit_username.isEnabled = false
        img_profile_edit_mail.visibility = View.INVISIBLE
        btn_profile_edit_mail.isEnabled = false
        img_profile_edit_phone.visibility = View.INVISIBLE
        btn_profile_edit_phone.isEnabled = false
    }

    private fun toastMessage(message: String) {
        Toast.makeText(this@ProfileActivity, message, Toast.LENGTH_LONG).show()
    }
}