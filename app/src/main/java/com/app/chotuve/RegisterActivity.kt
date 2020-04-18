package com.app.chotuve

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    var TAG: String = "Sign Up Screen"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnAccept: Button = findViewById(R.id.btnAcceptRegister)
        val btnCancel: Button = findViewById(R.id.btnCancelRegister)

        btnAccept.setOnClickListener(View.OnClickListener {
            Log.d(TAG, "Accept Button Clicked")
            //Account creation logic here
            val intentToLoginPage = Intent(this@RegisterActivity, MainActivity::class.java)
            toastMessage("Account Created")
            startActivity(intentToLoginPage)
        })

        btnCancel.setOnClickListener(View.OnClickListener {
            Log.d(TAG, "Cancel Button Clicked")
            val intentCancel = Intent(this@RegisterActivity, MainActivity::class.java)
            startActivity(intentCancel)
        })
    }

    private fun toastMessage(message: String) {
        Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_LONG).show()
    }
}