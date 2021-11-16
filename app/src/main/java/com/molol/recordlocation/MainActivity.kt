package com.molol.recordlocation

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.molol.recordlocation.ui.theme.RecordLocationTheme
import android.R
import android.content.DialogInterface

import com.google.android.material.snackbar.Snackbar

import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.view.View

import androidx.annotation.NonNull

import androidx.core.app.ActivityCompat




class MainActivity : ComponentActivity() {
    var statusTxt = mutableStateOf("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecordLocationTheme {
                // A surface container using the 'background' color from the theme

                    Content( statusTxt, ::onStartButton, ::onStopButton, ::checkIfServiceIsRunning)

            }
        }
        Handler().postDelayed({
            checkIfServiceIsRunning()
        },100)

        print("oncreate")
    }

    fun onStartButton() {
        Log.d("LOCATION", "start button")
        if ( checkPermissions()) {
            startService(Intent(this, LocationService::class.java))
            statusTxt.value = "Started"
        } else {
            statusTxt.value = "Permission request"
            requestPermissions()
        }
    }

    fun onStopButton() {
        startService( Intent(this,LocationService::class.java).apply {
            action = ACTION_STOP_FOREGROUND
        })
        statusTxt.value = "Stoped"
    }

    fun checkIfServiceIsRunning() {
        statusTxt.value = if (isMyServiceRunning(LocationService::class.java))
            "Running" else "Not running"
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        try {
            val manager =
                getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (serviceClass.name == service.service.className) {
                    return true
                }
            }
        } catch (e: Exception) {
            return false
        }
        return false
    }



    //

    private fun checkPermissions(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            print( "Displaying permission rationale to provide additional context.")
//            Snackbar.make(
//                //findViewById(R.id.activity_main),
//                R.string.permission_rationale,
//                Snackbar.LENGTH_INDEFINITE
//            )
//                .setAction(R.string.ok, object : OnClickListener() {
//                    fun onClick(view: View?) {
//                        // Request permission
//                        ActivityCompat.requestPermissions(
//                            this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                            REQUEST_PERMISSIONS_REQUEST_CODE
//                        )
//                    }
//                })
//                .show()
        } else {
            print("Requesting permission")
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(
                this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    val REQUEST_PERMISSIONS_REQUEST_CODE  = 34
    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        print("onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.size <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                print( "User interaction was cancelled.")
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                //mService.requestLocationUpdates()
            } else {
                // Permission denied.
                //setButtonsState(false)
                print( "Open settings Activity.")
//                Snackbar.make(
//                    findViewById(R.id.activity_main),
//                    R.string.permission_denied_explanation,
//                    Snackbar.LENGTH_INDEFINITE
//                )
//                    .setAction(R.string.settings, object : DialogInterface.OnClickListener() {
//                        fun onClick(view: View?) {
//                            // Build intent that displays the App settings screen.
//                            val intent = Intent()
//                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//                            val uri: Uri = Uri.fromParts(
//                                "package",
//                                BuildConfig.APPLICATION_ID, null
//                            )
//                            intent.data = uri
//                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                            startActivity(intent)
//                        }
//                    })
//                    .show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}

@Composable
fun Content( statusTxt: MutableState<String>, onStart: ()->Unit, onStop: ()->Unit , onCheck: ()->Unit) {

    Surface(color = MaterialTheme.colors.background) {
        Scaffold() {
            Column( verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()) {
                Button(onClick = { onStart() }) {
                    Text("Start")

                }
                Spacer(modifier = Modifier.height(40.dp))
                Button(onClick = { onStop() }) {
                    Text("Stop")
                }
                Spacer(modifier = Modifier.height(40.dp))
                Button(onClick = { onCheck() }) {
                    Text("Check")
                }
                Spacer(modifier = Modifier.height(40.dp))
                Text(statusTxt.value)
            }

        }

    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    RecordLocationTheme {

        Content( mutableStateOf("preview"),{} , {}, {})
    }
}