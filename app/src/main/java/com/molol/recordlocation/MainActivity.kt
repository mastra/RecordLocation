package com.molol.recordlocation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.molol.recordlocation.ui.theme.RecordLocationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecordLocationTheme {
                // A surface container using the 'background' color from the theme

                    Content(  ::onStartButton, ::onStopButton)

            }
        }
    }
    fun onStartButton() {

    }

    fun onStopButton() {

    }
}

@Composable
fun Content( onStart: ()->Unit, onStop: ()->Unit ) {
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
                Text("hola")
            }

        }

    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    RecordLocationTheme {

        Content( {} , {})
    }
}