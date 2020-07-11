package com.app.chotuve.login

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.app.chotuve.R
import com.app.chotuve.context.ApplicationContext
import com.app.chotuve.register.RegisterActivity
import com.app.chotuve.home.HomePageActivity
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import com.google.firebase.iid.FirebaseInstanceId
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private val TAG: String = "Login Screen"
    private val serverURL: String = "https://serene-shelf-10674.herokuapp.com/login"
    //TODO private val serverURL: String = "https://choutuve-app-server.herokuapp.com/login"

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



    }

    private fun toastMessage(message: String) {
        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_LONG).show()
    }

    private fun loginRequest(email: String, password: String) {
        val tipo: String = "mailPass"
        val deviceId = ApplicationContext.getDeviceID()
        Log.d(TAG,"device: $deviceId")
        Fuel.post(serverURL)
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
        Fuel.post(serverURL)
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
        toastMessage("Login Successful")
        startActivity(intentToHomePage)
    }

}
