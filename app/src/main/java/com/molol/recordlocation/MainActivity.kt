package com.molol.recordlocation

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

class MainActivity : ComponentActivity() {
    var statusTxt = mutableStateOf("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecordLocationTheme {
                // A surface container using the 'background' color from the theme

                    Content( statusTxt, ::onStartButton, ::onStopButton)

            }
        }
   //     Handler().postDelayed({
            checkIfServiceIsRunning()
   //     },100)
    }

    fun onStartButton() {
        startService( Intent(this,LocationService::class.java))
        statusTxt.value= "Started"
    }

    fun onStopButton() {
        startService( Intent(this,LocationService::class.java).apply {
            action = ACTION_STOP
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
}

@Composable
fun Content( statusTxt: MutableState<String>, onStart: ()->Unit, onStop: ()->Unit ) {

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

        Content( mutableStateOf("preview"),{} , {})
    }
}