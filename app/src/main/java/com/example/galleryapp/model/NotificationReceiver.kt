package com.example.galleryapp.model

import android.app.Activity
import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.NotificationManagerCompat.from
import java.util.Date.from

private const val TAG = "NotificationReceiver"

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG, "reseived result: ${resultCode}")
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        val requestCode = intent!!.getIntExtra(PollWorker.REQUEST_CODE, 0)
        val notification: Notification =
            intent.getParcelableExtra(PollWorker.NOTIFICATION)!!

        val notificationManager = NotificationManagerCompat.from(context!!)
        notificationManager.notify(requestCode, notification)
    }
}