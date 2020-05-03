package com.app.chotuve.upload

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.app.chotuve.R
import com.app.chotuve.home.HomePageActivity
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.*


class UploadActivity  : AppCompatActivity() {
    private val TAG: String = "Upload Screen"
    private var selectedVideo: Uri? = null
    private var selectedVideoThumbnail: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        val btnAccept: Button = findViewById(R.id.btn_upload_accept)
        val btnCancel: Button = findViewById(R.id.btn_upload_cancel)
        val btnSelectVideo: Button = findViewById(R.id.btn_upload_select_video)
        val progressBar: ProgressBar = findViewById(R.id.bar_upload_progress_upload)
        val txtProgress: TextView = findViewById(R.id.txt_upload_progress)
        progressBar.visibility = ProgressBar.INVISIBLE
        txtProgress.visibility = TextView.INVISIBLE


        btnAccept.setOnClickListener(View.OnClickListener {
            Log.d(TAG, "Accept Button Clicked")
            //upload video logic here
            val builder = AlertDialog.Builder(this@UploadActivity)
            if (selectedVideo == null || selectedVideoThumbnail == null){
                builder.setTitle("Select a Video")
                builder.setMessage("You must select a video first")
                val dialog: AlertDialog = builder.create()

                // Display the alert dialog on app interface
                dialog.show()
            }else {
                buttonEnableController(false)
                val filename = uploadVideoToStorage()
            }
        })

        btnCancel.setOnClickListener(View.OnClickListener {
            Log.d(TAG, "Cancel Button Clicked")
            val intentCancel = Intent(this@UploadActivity, HomePageActivity::class.java)
            startActivity(intentCancel)
        })

        btnSelectVideo.setOnClickListener(View.OnClickListener {
            Log.d(TAG, "Select Video clicked")
            val uploadIntent = Intent(Intent.ACTION_PICK)
            uploadIntent.type = "video/*"
            startActivityForResult(uploadIntent, 0)
        })
    }



    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val imgThumbnail: ImageView = findViewById(R.id.img_upload_thumbnail)
        Log.d(TAG, "resultCode: $resultCode")
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) {
            Log.d(TAG, "Cancel selection")
            return
        }
        if (data != null && data.data != null) {
            selectedVideo = data.data


            val mMMR = MediaMetadataRetriever()
            mMMR.setDataSource(applicationContext, selectedVideo)
            selectedVideoThumbnail = mMMR.frameAtTime


            imgThumbnail.setImageBitmap(selectedVideoThumbnail)
        }

    }

    private fun uploadVideoToStorage(): String {
        val filename = UUID.randomUUID().toString()
        val refVideos = FirebaseStorage.getInstance().getReference("/videos/$filename")


        val refThumbnails = FirebaseStorage.getInstance().getReference("/thumbnails/$filename")
        val baos = ByteArrayOutputStream()
        selectedVideoThumbnail!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val thumbnailData = baos.toByteArray()

        val progressBar: ProgressBar = findViewById(R.id.bar_upload_progress_upload)
        val txtProgress: TextView = findViewById(R.id.txt_upload_progress)
        progressBar.visibility = ProgressBar.VISIBLE
        txtProgress.visibility = ProgressBar.VISIBLE

        refVideos.putFile(selectedVideo!!)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Video uploaded successfully")
                    //send filename to API
                    val intentToLoginPage = Intent(this@UploadActivity, HomePageActivity::class.java)
                    buttonEnableController(true)
                    startActivity(intentToLoginPage)
                } else {
                    Log.d(TAG, "failed to upload: ${task.exception?.message}")
                    buttonEnableController(true)
                }
            }
            .addOnProgressListener {
                val progress: Long = (100 * it.bytesTransferred).div(it.totalByteCount)
                progressBar.progress = progress.toInt()
                txtProgress.text = "$progress%"
                Log.d(TAG, "Progress: $progress")
            }
        refThumbnails.putBytes(thumbnailData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Thumbnail uploaded successfully")

                } else {
                    Log.d(TAG, "failed to upload: ${task.exception?.message}")
                    buttonEnableController(true)
                }
            }

        return filename

    }

    private fun buttonEnableController(boolean: Boolean){
        val btnAccept: Button = findViewById(R.id.btn_upload_accept)
        val btnCancel: Button = findViewById(R.id.btn_upload_cancel)
        val btnSelectVideo: Button = findViewById(R.id.btn_upload_select_video)

        btnAccept.isEnabled = boolean
        btnCancel.isEnabled = boolean
        btnSelectVideo.isEnabled = boolean
    }

    private fun toastMessage(message: String) {
        Toast.makeText(this@UploadActivity, message, Toast.LENGTH_LONG).show()
    }
}
