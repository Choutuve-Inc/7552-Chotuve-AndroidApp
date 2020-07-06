package com.app.chotuve.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.chotuve.R
import com.app.chotuve.login.LoginActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class FCMReceiver : FirebaseMessagingService()  {
    private val TAG = "FCM Notif Reciever"
    lateinit var notifManager: NotificationManager
    lateinit var notifChannel: NotificationChannel
    private var channelId = "CHATUVE_MESSAGE"
    private var channelDescription = "Notification on message recieved on Chatuve"
    lateinit var builder: Notification.Builder
    private var notifID = 0

    override fun onNewToken(token: String) {
        Log.d(TAG, "The token refreshed: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {

        if (message.data.isNotEmpty()){
            val title = message.data.get("title")
            val messageText = message.data.get("body")
            Log.d(TAG,"Recieved: $title - $messageText\nFrom:${message.from}")

            val intent = Intent(this, LoginActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            notifManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notifChannel = NotificationChannel(channelId,channelDescription,NotificationManager.IMPORTANCE_HIGH)
                notifChannel.enableLights(true)
                notifChannel.lightColor = Color.RED
                notifChannel.enableVibration(false)
                notifManager.createNotificationChannel(notifChannel)

                builder = Notification.Builder(this, channelId)
                    .setContentTitle(title)
                    .setContentText(messageText)
                    .setBadgeIconType(R.drawable.ic_launcher_round)
                    .setSmallIcon(R.drawable.ic_launcher_round)
                    .setLargeIcon(BitmapFactory.decodeResource(this.resources,R.drawable.ic_launcher))
                    .setContentIntent(pendingIntent)
            }else{
                builder = Notification.Builder(this)
                    .setContentTitle(title)
                    .setContentText(messageText)
                    .setSmallIcon(R.drawable.ic_launcher_round)
                    .setLargeIcon(BitmapFactory.decodeResource(this.resources,R.drawable.ic_launcher))
                    .setContentIntent(pendingIntent)
            }

            notifManager.notify(notifID,builder.build())
            notifID += 1
        }
    }
}