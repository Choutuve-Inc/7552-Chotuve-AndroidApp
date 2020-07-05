package com.app.chotuve.openchats

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.chotuve.R
import com.app.chotuve.context.ApplicationContext
import com.app.chotuve.friendlist.FriendsActivity
import com.app.chotuve.home.HomePageActivity
import com.app.chotuve.utils.TopSpacingItemDecoration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_home_page.*
import kotlinx.android.synthetic.main.activity_open_chats.*

class OpenChatsActivity : AppCompatActivity() {
    private val TAG: String = "Chats Screen"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_chats)

        val adapter = GroupAdapter<GroupieViewHolder>()

        adapter.add(ModelChat())
        adapter.add(ModelChat())
        adapter.add(ModelChat())
        adapter.add(ModelChat())
        adapter.add(ModelChat())
        adapter.add(ModelChat())

        rec_open_chats.adapter = adapter
        rec_open_chats.apply {
            val topSpacingDecoration =
                TopSpacingItemDecoration(8)
            addItemDecoration(topSpacingDecoration)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu_open_chats_page, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId){
            R.id.top_chats_friends -> {
                Log.d(TAG, "New Chat Button Clicked")
                ApplicationContext.LogUserOut()
                val intentToLoginPage = Intent(this@OpenChatsActivity, FriendsActivity::class.java)
                startActivity(intentToLoginPage)
            }
            R.id.top_chats_videos -> {
                Log.d(TAG, "Video Feed Button Clicked")
                ApplicationContext.LogUserOut()
                val intentToHomePage = Intent(this@OpenChatsActivity, HomePageActivity::class.java)
                intentToHomePage.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intentToHomePage)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun toastMessage(message: String) {
        Toast.makeText(this@OpenChatsActivity, message, Toast.LENGTH_LONG).show()
    }
}