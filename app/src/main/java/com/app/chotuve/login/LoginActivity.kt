package com.app.chotuve.login

import android.app.AlertDialog
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
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.result.Result

class LoginActivity : AppCompatActivity() {

    private val TAG: String = "Login Screen"
    private val serverURL: String = "https://serene-shelf-10674.herokuapp.com/login"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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
        Fuel.post(serverURL)
            .jsonBody(
                "{ \"email\" : \"$email\"," +
                        " \"password\" : \"$password\", " +
                        " \"tipo\" : \"$tipo\" " +
                        "}"
            )
            .response { request, response, result ->

                when (result) {
                    is Result.Success -> {
                        response.statusCode
                        var body = response.body()

                        Log.d(TAG, "Successful Login: ${response.statusCode}")
                        val token = body.asString("application/json")
                        ApplicationContext.setConnectedUser(email, token)

                        val intentToHomePage =
                            Intent(this@LoginActivity, HomePageActivity::class.java)
                        toastMessage("Login Successful")
                        startActivity(intentToHomePage)
                    }
                    is Result.Failure -> {

                        val builder = AlertDialog.Builder(this@LoginActivity)
                        builder.setTitle("Authentication Error")
                        builder.setMessage("Incorrect Username or Password.\nPlease try Again.")
                        val dialog: AlertDialog = builder.create()
                        dialog.show()

                        Log.d(TAG, "Unsuccessful login: ${response.statusCode}")
                    }
                }
            }
    }
}
