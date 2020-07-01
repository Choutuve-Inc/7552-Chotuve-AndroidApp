package com.app.chotuve.context

import com.google.firebase.iid.FirebaseInstanceId
import java.text.SimpleDateFormat
import java.util.*

object ApplicationContext {
    private var username: String = ""
    private var token: String = ""

    fun getDeviceID(): String {
        return FirebaseInstanceId.getInstance().id
    }

    fun getCurrentDate(): String {
        var date = Date()
        val formatter = SimpleDateFormat("YYYY-MM-dd")
        return formatter.format(date)
    }

    fun setConnectedUser(email: String, token: String) {
        this.username = email
        this.token = token
    }

    fun getConnectedUsername():String {
        return this.username
    }

    fun getConnectedToken(): String {
        return this.token
    }

    fun LogUserOut(){
        this.username = ""
        this.token = ""
    }
}