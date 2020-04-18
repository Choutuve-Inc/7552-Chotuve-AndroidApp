package com.app.chotuve

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class HomePageActivity  : AppCompatActivity() {

    var TAG: String = "Home Screen"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        val btnLogOut: Button = findViewById(R.id.btnLogOut)

        btnLogOut.setOnClickListener(View.OnClickListener {
            Log.d(TAG, "Log Out Button Clicked")
            //Account creation logic here
            val intentToLoginPage = Intent(this@HomePageActivity, MainActivity::class.java)
            toastMessage("Correctly Logged Out")
            startActivity(intentToLoginPage)
        })
    }

    private fun toastMessage(message: String) {
        Toast.makeText(this@HomePageActivity, message, Toast.LENGTH_LONG).show()
    }
}