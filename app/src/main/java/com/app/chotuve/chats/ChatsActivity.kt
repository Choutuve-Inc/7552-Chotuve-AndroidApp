package com.app.chotuve.chats

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.app.chotuve.R
import com.app.chotuve.context.ApplicationContext
import com.app.chotuve.friendlist.FriendsActivity
import com.app.chotuve.login.LoginActivity
import com.app.chotuve.upload.UploadActivity

class ChatsActivity : AppCompatActivity() {
    private val TAG: String = "Chats Screen"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu_home_page, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId){
            R.id.top_chats_friends -> {
                Log.d(TAG, "New Chat Button Clicked")
                ApplicationContext.LogUserOut()
                val intentToLoginPage = Intent(this@ChatsActivity, FriendsActivity::class.java)
                intentToLoginPage.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intentToLoginPage)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun toastMessage(message: String) {
        Toast.makeText(this@ChatsActivity, message, Toast.LENGTH_LONG).show()
    }
}