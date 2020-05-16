package com.app.chotuve.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.chotuve.R
import com.app.chotuve.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private val TAG: String = "Sign Up Screen"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnAccept: Button = findViewById(R.id.btnAcceptRegister)
        val btnCancel: Button = findViewById(R.id.btnCancelRegister)

        btnAccept.setOnClickListener(View.OnClickListener {
            Log.d(TAG, "Accept Button Clicked")
            //Account creation logic here
            val intentToLoginPage = Intent(this@RegisterActivity, LoginActivity::class.java)
            toastMessage("Account Created")
            startActivity(intentToLoginPage)
        })

        btnCancel.setOnClickListener(View.OnClickListener {
            Log.d(TAG, "Cancel Button Clicked")
            val intentCancel = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intentCancel)
        })
    }

    private fun toastMessage(message: String) {
        Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_LONG).show()
    }
}