package com.molol.recordlocation

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

const val  ACTION_STOP =  "${BuildConfig.APPLICATION_ID}.stop"
const val  ACTION_STOP_FOREGROUND =  "${BuildConfig.APPLICATION_ID}.stopForeground"
class LocationService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let {
            if (it.equals(ACTION_STOP_FOREGROUND)) {
                stopForeground(true)
                stopSelf()
            }


        }
        generateForegroundNotificacion()
        //return START_NOT_STICKY
        return START_STICKY
    }

    private var iconNotification: Bitmap? = null
    private var notification: Notification? = null
    var mNotificationManager: NotificationManager? = null
    private val mNotificationId=123

    private fun generateForegroundNotificacion() {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            val intentMainLanding = Intent( this, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(this, 0, intentMainLanding, 0)
            iconNotification = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
            if (mNotificationManager==null) {
                mNotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                assert(mNotificationManager!=null)
                mNotificationManager?.createNotificationChannelGroup( NotificationChannelGroup("chat_group","Chats" )
                )
                val notificationChannel = NotificationChannel("service_channel", "Service Notifications",
                    NotificationManager.IMPORTANCE_MIN)
                notificationChannel.enableLights(false)
                notificationChannel.lockscreenVisibility = Notification.VISIBILITY_SECRET
                mNotificationManager?.createNotificationChannel(notificationChannel)
            }

            val builder = NotificationCompat.Builder(this, "service_channel")
            builder.setContentTitle( resources.getString(R.string.app_name)+" is running")
                .setContentText("touch to open")
                .setSmallIcon(R.drawable.ic_alarm)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setWhen(0)
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)
                .setOngoing(true)
            if (iconNotification!=null) {
                builder.setLargeIcon(Bitmap.createScaledBitmap(iconNotification!!, 128,128,false))
            }
            builder.color = resources.getColor(R.color.purple_200)
            notification = builder.build()

            startForeground(mNotificationId, notification)


         }
    }
}