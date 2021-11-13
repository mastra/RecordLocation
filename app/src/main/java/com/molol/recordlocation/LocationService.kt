package com.molol.recordlocation

import android.app.Service
import android.content.Intent
import android.os.IBinder

const val  ACTION_STOP =  "${BuildConfig.APPLICATION_ID}.stop"

class LocationService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action?.let {
            if (it.equals(ACTION_STOP)) {
                stopSelf()
            }
        }

        return START_NOT_STICKY
    }
}