package com.molol.recordlocation

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import java.util.concurrent.TimeUnit

//const val  ACTION_STOP =  "${BuildConfig.APPLICATION_ID}.stop"
const val  ACTION_STOP_FOREGROUND =  "${BuildConfig.APPLICATION_ID}.stopForeground"
class LocationService : Service() {

    lateinit var fusedLocationClient : FusedLocationProviderClient
    lateinit var locationRequest : LocationRequest
    //private var currentLocation: Location? = null
    var liveLocation = MutableLiveData<Location>()

    private var iconNotification: Bitmap? = null
    //private var notification: Notification? = null
    var notificationManager: NotificationManager? = null
    private val notificationId=123

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            liveLocation.value = locationResult.lastLocation
//            currentLocation?.let {
//                Log.d("LOCATION", "LOCATION: lat: ${it.latitude}, ${it.longitude}")
//            }

//            val intent = Intent(ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST)
//            intent.putExtra(EXTRA_LOCATION, currentLocation)
//            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

            // Updates notification content if this service is running as a foreground
            // service.
//            if (serviceRunningInForeground) {
//                notificationManager.notify(
//                    notificationId,
//                    generateNotification(currentLocation))
//            }
        }
    }
    private val localBinder = LocalBinder()
    inner class LocalBinder : Binder() {
        internal val service: LocationService
            get() = this@LocationService
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "onBind()")
        return localBinder
    }

    override fun onRebind(intent: Intent) {
        Log.d(TAG, "onRebind()")
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.d(TAG, "onUnbind()")
        return true
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate Service")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(2)
            fastestInterval = TimeUnit.SECONDS.toMillis(1)
            maxWaitTime = TimeUnit.SECONDS.toMillis(15)

            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("LOCATION", "onStart service")
        intent?.action?.let {
            if (it.equals(ACTION_STOP_FOREGROUND)) {
                unSusbscribeFromLocation()
                stopForeground(true)
                stopSelf()
            }
        }
        val notification = generateForegroundNotification()
        startForeground(notificationId, notification)
        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permission == PackageManager.PERMISSION_GRANTED) {
            Log.d("LOCATION", "permission granted")
            try {
                fusedLocationClient.requestLocationUpdates( locationRequest,
                    locationCallback,  null )//Looper.getMainLooper()

            } catch (e: Exception) {
                Log.d("LOCATION","Permision exception: ${e.message}")
            }
        }
        //return START_NOT_STICKY
        return START_STICKY
    }


    fun generateForegroundNotification() :  Notification{
        //if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            val intentMainLanding = Intent( this, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(this, 0, intentMainLanding, 0)
            iconNotification = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
            if (notificationManager==null) {
                notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                assert(notificationManager!=null)
                notificationManager?.createNotificationChannelGroup( NotificationChannelGroup("location_group","Location" )
                )
                val notificationChannel = NotificationChannel("service_channel", "Service Notifications",
                    NotificationManager.IMPORTANCE_MIN)
                notificationChannel.enableLights(false)
                notificationChannel.lockscreenVisibility = Notification.VISIBILITY_SECRET
                notificationManager?.createNotificationChannel(notificationChannel)
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
            return builder.build()

         //}
    }

    fun unSusbscribeFromLocation() {
        val removeTask = fusedLocationClient.removeLocationUpdates(locationCallback)
        removeTask.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                print("Location Callback removed.")
                stopSelf()
            } else {
                print("Failed to remove Location Callback.")
            }
        }
    }

    companion object {
        const val TAG = "__LOCATION_SERVICE__"
    }
}