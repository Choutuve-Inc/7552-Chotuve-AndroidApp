package com.app.chotuve.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.app.chotuve.R
import com.app.chotuve.register.RegisterActivity
import com.app.chotuve.home.HomePageActivity

class LoginActivity : AppCompatActivity() {

    private val TAG: String = "Login Screen"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnSignIn: Button = findViewById(R.id.btn_login_login)
        val btnRegister: Button = findViewById(R.id.btn_login_register)

        btnSignIn.setOnClickListener(View.OnClickListener {
            Log.d(TAG, "Sign In Button Clicked")
            //Login Auth logic here
            val intentToHomePage = Intent(this@LoginActivity, HomePageActivity::class.java)
            toastMessage("Login Successful")
            startActivity(intentToHomePage)
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

}
