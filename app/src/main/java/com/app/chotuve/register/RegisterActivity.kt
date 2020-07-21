package com.app.chotuve.register

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.chotuve.R
import com.app.chotuve.context.ApplicationContext
import com.app.chotuve.home.ModelVideo
import com.app.chotuve.home.VideoDataSource
import com.app.chotuve.login.LoginActivity
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.result.Result
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.tasks.await
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private val TAG: String = "Sign Up Screen"
    private val serverURL: String = "https://choutuve-app-server.herokuapp.com/create"
    private var selectedPhotoUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnAccept: Button = findViewById(R.id.btn_register_accept)
        val btnCancel: Button = findViewById(R.id.btn_register_cancel)
        val btnPhoto: Button = findViewById(R.id.btn_register_photo)

        btnAccept.setOnClickListener {
            Log.d(TAG, "Accept Button Clicked")
            performRegister()
        }

        btnCancel.setOnClickListener{
            Log.d(TAG, "Cancel Button Clicked")
            val intentCancel = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intentCancel)
        }

        btnPhoto.setOnClickListener{
            Log.d(TAG, "Photo Button Clicked")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            img_register_user.setImageBitmap(bitmap)
            btn_register_photo.alpha = 0f

//            val drawable = BitmapDrawable(bitmap)
//            btn_register_photo.setBackgroundDrawable(drawable)
        }

    }

    private fun toastMessage(message: String) {
        Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_LONG).show()
    }

    private fun performRegister(){
        //Account creation logic here
        if (validateParameters()){
            setButtonEnable(false)
            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/userPic/$filename")
            ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    Log.d(TAG,"Image added to Firebase")
                    val user = txt_register_username.text
                    val email = txt_register_email.text
                    val password = txt_register_password.text
                    val phone = txt_register_phone.text
                    val tipo = "mailPass"
                    ref.downloadUrl
                        .addOnSuccessListener {
                            val url = it.toString()

                            Fuel.post(serverURL)
                                .jsonBody(
                                    "{ \"email\" : \"$email\"," +
                                            " \"phone\" : \"$phone\"," +
                                            " \"username\" : \"$user\", " +
                                            " \"password\" : \"$password\", " +
                                            " \"tipo\" : \"$tipo\", " +
                                            " \"image\" : \"${url}\" " +
                                            "}"
                                )
                                .response { request, response, result ->
                                    Log.d(TAG, request.toString())

                                    when (result) {
                                        is Result.Success -> {
                                            Log.d(TAG, "Account Created")
                                            setButtonEnable(true)
                                            val intentToLoginPage = Intent(this@RegisterActivity, LoginActivity::class.java)
                                            intentToLoginPage.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            toastMessage("Account Created")
                                            startActivity(intentToLoginPage)
                                        }
                                        is Result.Failure -> {

                                            val builder = AlertDialog.Builder(this@RegisterActivity)
                                            if (response.statusCode == -1){
                                                builder.setTitle("Error")
                                                builder.setMessage("Something happened.\nPlease try Again.")
                                            }else {
                                                builder.setTitle("Credentials Error")
                                                builder.setMessage("${result.error.message}.")
                                            }
                                            val dialog: AlertDialog = builder.create()
                                            dialog.show()

                                            Log.d(TAG, "Unsuccessful login: ${response.statusCode}")
                                            setButtonEnable(true)
                                        }
                                    }
                                }

                            Log.d(TAG, "Success: $url.")
                        }.addOnFailureListener {
                            Log.d(TAG, "Error obtaining userPic: ${it.message}.")
                        }
                }.addOnFailureListener{
                    Log.d(TAG, "Error uploading image to Firebase: ${it.message}")
                    setButtonEnable(true)
                }
        }
    }

    private fun setButtonEnable(enable: Boolean) {
        btn_register_photo.isEnabled = enable
        btn_register_accept.isEnabled = enable
        btn_register_cancel.isEnabled = enable
    }

    private fun validateParameters(): Boolean{
        val validStrings = Regex("([A-Za-z0-9\\-\\_]+)")
        val user = txt_register_username.text
        val email = txt_register_email.text
        val phone = txt_register_phone.text
        val password = txt_register_password.text
        val password2 = txt_register_password_2.text
        val builder = AlertDialog.Builder(this@RegisterActivity)

        if (selectedPhotoUri == null){
            builder.setTitle("Required Field")
            builder.setMessage("Must complete the field: Photo")
            val dialog: AlertDialog = builder.create()
            dialog.show()
            return false
        }
        if (email.isBlank()){
            builder.setTitle("Required Field")
            builder.setMessage("Must complete the field: Email")
            val dialog: AlertDialog = builder.create()
            dialog.show()
            return false
        }
        if (user.isBlank()){
            builder.setTitle("Required Field")
            builder.setMessage("Must complete the field: Username")
            val dialog: AlertDialog = builder.create()
            dialog.show()
            return false
        }
        if (phone.isBlank()){
            builder.setTitle("Required Field")
            builder.setMessage("Must complete the field: Phone Number")
            val dialog: AlertDialog = builder.create()
            dialog.show()
            return false
        }
        if (password.isBlank()){
            builder.setTitle("Required Field")
            builder.setMessage("Must complete the field: Password")
            val dialog: AlertDialog = builder.create()
            dialog.show()
            return false
        }
        if (password.isBlank()){
            builder.setTitle("Required Field")
            builder.setMessage("Must complete the field: Password")
            val dialog: AlertDialog = builder.create()
            dialog.show()
            return false
        }
        if (!validStrings.matches(user)){
            builder.setTitle("Invalid Username")
            builder.setMessage("Username can only contain letters, numbers, underscore or dash")
            val dialog: AlertDialog = builder.create()
            dialog.show()
            return false
        }
        if (!validStrings.matches(user)){
            builder.setTitle("Invalid Username")
            builder.setMessage("Username con only contain letters, numbers, underscore or dash")
            val dialog: AlertDialog = builder.create()
            dialog.show()
            return false
        }
        if (!validStrings.matches(password)){
            builder.setTitle("Invalid Password")
            builder.setMessage("Password con only contain letters, numbers, underscore or dash")
            val dialog: AlertDialog = builder.create()
            dialog.show()
            return false
        }
        if (password.toString() != password2.toString()){
            builder.setTitle("Unmatching Passwords")
            builder.setMessage("Passwords don't match")
            val dialog: AlertDialog = builder.create()
            dialog.show()
            return false
        }
        // Display the alert dialog on app interface

        return true
    }
}