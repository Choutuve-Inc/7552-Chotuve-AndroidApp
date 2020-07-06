package com.app.chotuve.context

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import java.text.SimpleDateFormat
import java.util.*

object ApplicationContext {
    //Media: https://arcane-thicket-79100.herokuapp.com
    //App: https://choutuve-app-server.herokuapp.com
    //Auth: https://serene-shelf-10674.herokuapp.com
    private var username: String = ""
    private var token: String = ""

    fun getDeviceID(): String? { //cqq9RCxlQE6ZMYfpxfKIt7:APA91bGc4usxBrRC_t4Ad5j5rT_iGruMQUc7_cClgr-TY2ClS6m978Lfkvrkqq-HyMS_h8XFEn6xeG4atfdjUai4_gV5p-YAKyo4V1aTLDbKE_AHO0PqsbSdgXD7GhQXuHP3tRmj7ZpG
        return FirebaseInstanceId.getInstance().getToken()
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