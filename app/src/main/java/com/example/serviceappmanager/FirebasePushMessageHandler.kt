package com.example.serviceappmanager

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebasePushMessageHandler : FirebaseMessagingService() {
    private val CHANNEL_ID = "manager-notification"
    private val managerNotifDb = Firebase.firestore.collection("manager-notifications")

    override fun onNewToken(token: String) {
        Log.d("fcm", "New Token: $token")
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("fcm", "Message received")
        val toApp = remoteMessage.data["toApp"]
        val serviceId = remoteMessage.data["serviceId"]
        val status = remoteMessage.data["status"]


        if (toApp != "manager")
            return
        Log.d("fcm", "Received notification with status: $status, toApp: $toApp")

        val content = "Status of service with id $serviceId is changed to $status"
        showNotification(content)
    }

    private fun showNotification(content: String) {
        createNotificationChannel()

        val data = mapOf("description" to content)
        managerNotifDb.add(data)

        val goToNotifications = Intent(this, MainActivity2::class.java)
        goToNotifications.putExtra("is-notification", "true")

        val notifIntent = PendingIntent.getActivity(
            this,
            0,
            goToNotifications,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("Service status changed")
            .setContentIntent(notifIntent)
            .setContentText(content)
            .setAutoCancel(true)
            .build()

        val notificationId = System.currentTimeMillis();

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    this@FirebasePushMessageHandler,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("fcm", "Permission for sending notification denied")
                return
            } else {
                Log.d("fcm", "Sending Notification to user")
                notify(notificationId.toInt(), notification)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Manager App Notifier"
            val descriptionText = "Alerts the manager when an engineer changes status of a call"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}