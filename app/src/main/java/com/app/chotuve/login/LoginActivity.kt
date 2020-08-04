package com.app.chotuve.login

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.app.chotuve.R
import com.app.chotuve.context.ApplicationContext
import com.app.chotuve.forgottenpassword.ChangePasswordActivity
import com.app.chotuve.register.RegisterActivity
import com.app.chotuve.home.HomePageActivity
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.layout_login_forgotten_password.*
import kotlinx.android.synthetic.main.layout_profile_popup.*
import kotlinx.android.synthetic.main.layout_profile_popup.view.*
import kotlinx.android.synthetic.main.layout_profile_popup.view.txt_profile_popup_input
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private val TAG: String = "Login Screen"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        FirebaseInstanceId.getInstance().id //Setup en caso de 1er uso de la app.
        val uid = ApplicationContext.getDeviceID()
        Log.d(TAG, "FirebaseInstance: ${uid}") //doaT2eFiDvA
        if (uid != null) alreadyLoggedUser(uid)

        val btnSignIn: Button = findViewById(R.id.btn_login_login)
        val btnRegister: Button = findViewById(R.id.btn_login_register)
        val txtUser: EditText = findViewById(R.id.txt_login_username)
        val txtPass: EditText = findViewById(R.id.txt_login_password)

        btnSignIn.setOnClickListener(View.OnClickListener {
            Log.d(TAG, "Sign In Button Clicked")
            //Login Auth logic here
            var email: String = txtUser.text.toString()
            var pass: String = txtPass.text.toString()
            loginRequest(email, pass)

        })

        btnRegister.setOnClickListener(View.OnClickListener {
            Log.d(TAG, "Sign Up Button Clicked")
            val intentToRegister = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intentToRegister)
        })

        lbl_login_forget_password.setOnClickListener{
            Log.d(TAG, "Forgotten Password clicked!")
            openMailDialog()
        }
    }

    private fun toastMessage(message: String) {
        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_LONG).show()
    }

    private fun loginRequest(email: String, password: String) {
        val tipo: String = "mailPass"
        val deviceId = ApplicationContext.getDeviceID()
        Log.d(TAG,"device: $deviceId")
        Fuel.post("${ApplicationContext.getServerURL()}/login")
            .jsonBody(
                "{ \"email\" : \"$email\"," +
                        " \"password\" : \"$password\", " +
                        "\"device\" : \"$deviceId\"," +
                        " \"tipo\" : \"$tipo\" " +
                        "}"
            )
            .response { request, response, result ->
                when (result) {
                    is Result.Success -> {
                        Log.d(TAG, "Successful Login")
                        correctLogin(response)
                    }
                    is Result.Failure -> {
                        Log.d(TAG, "${result.error.message}")

                        val builder = AlertDialog.Builder(this@LoginActivity)
                        if (response.statusCode == -1){
                            builder.setTitle("Error")
                            builder.setMessage("Something happened.\nPlease try Again.")
                        }else {
                            builder.setTitle("Authentication Error")
                            builder.setMessage("Incorrect Username or Password.\nPlease try Again.")
                        }
                        val dialog: AlertDialog = builder.create()
                        dialog.show()

                        Log.d(TAG, "Unsuccessful login: ${response.statusCode}")
                    }
                }
            }
    }

    private fun alreadyLoggedUser(uid: String) {
        Log.d(TAG, "Estoy?: ${uid}")
        Fuel.post("${ApplicationContext.getServerURL()}/login")
            .jsonBody(
                "{ \"device\" : \"${uid}\"}"
            )
            .response { request, response, result ->
                when (result) {
                    is Result.Success -> {
                        Log.d(TAG, "alreadyLoggedUser: Connected user in device.")
                        correctLogin(response)
                    }
                    is Result.Failure -> {
                        //Look up code and choose what to do.
                        Log.d(TAG, "alreadyLoggedUser: No connected user in device.")
                        Log.d(TAG, "${result.error.message}")
                    }
                }
            }
    }

    private fun correctLogin(response: Response){
        val body = response.body()
        val json = JSONObject(body.asString("application/json"))
        val token = json["token"] as String
        val userID = json["uid"] as String
        ApplicationContext.setConnectedUser(userID, token)
        Log.d(TAG, "This is the token: $token.")
        Log.d(TAG, "This is the userID: $userID.")

        val intentToHomePage = Intent(this@LoginActivity, HomePageActivity::class.java)
        intentToHomePage.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        toastMessage("Login Successful")
        startActivity(intentToHomePage)
    }

    //Forgotten Password
    private fun openMailDialog(){
        val editAlert = AlertDialog.Builder(this).create()
        val editView = layoutInflater.inflate(R.layout.layout_login_forgotten_password, null)
        editAlert.setView(editView)
        editAlert.setButton(AlertDialog.BUTTON_POSITIVE, "SEND") { _, _ ->
            if (validateEmail(editAlert.txt_login_forgotten_password_email.text.toString())) sendForgottenPasswordTokenToMail(editAlert.txt_login_forgotten_password_email.text.toString())
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

    private fun sendForgottenPasswordTokenToMail(userMail: String){
        val URL = "${ApplicationContext.getServerURL()}/key"
        Fuel.post(URL)
            .jsonBody(
                "{" +
                        " \"email\" : \"${userMail}\"" +
                        "}"
            )
            .responseJson { request, response, result ->
                when (result) {
                    is Result.Success -> {
                        Log.d(TAG, "HTTP Success [getForgottenPasswordToken]")
                        val intentToChangePassword = Intent(this@LoginActivity, ChangePasswordActivity::class.java)
                        intentToChangePassword.putExtra("userMail", userMail)
                        toastMessage("Email sent!")
                        startActivity(intentToChangePassword)
                    }
                    is Result.Failure -> {
                        Log.d(TAG, "Error obtaining User.")
                        Log.d(TAG, "Error Code: ${response.statusCode}")
                        Log.d(TAG, "Error Message: ${result.error}")
                        Log.d(TAG, "request was: $request")
                        toastMessage("There was an error when looking for a user with registered mail: $userMail")
                    }
                }
            }
    }

}
