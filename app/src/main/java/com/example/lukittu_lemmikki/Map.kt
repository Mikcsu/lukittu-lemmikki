package com.example.lukittu_lemmikki

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp



@Composable
fun MapView(onButtonClick: () -> Unit) {
    val context = LocalContext.current
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    val sessionSteps = remember { mutableStateOf(0) }
    val totalSteps = remember { mutableStateOf(0) }

    val sensorEventListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor == stepSensor) {
                    totalSteps.value = event.values[0].toInt()
                    sessionSteps.value ++
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Not used
            }
        }
    }

    DisposableEffect(Unit) {
        sensorManager.registerListener(sensorEventListener, stepSensor, SensorManager.SENSOR_DELAY_UI)
        onDispose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Map View")
        Button(onClick = {
            sessionSteps.value = 0
            sensorManager.registerListener(sensorEventListener, stepSensor, SensorManager.SENSOR_DELAY_NORMAL)
            Toast.makeText(context, "Step counter started", Toast.LENGTH_SHORT).show()
        }) {
            Text(text = "Start step counter")
        }
        Button(onClick = {
            sensorManager.unregisterListener(sensorEventListener)
            Toast.makeText(context, "Step counter stopped", Toast.LENGTH_SHORT).show()
        }) {
            Text(text = "Stop Step Counter")
        }
        Text(text="Steps this session: ${sessionSteps.value}")
        Text(text="Total Steps: ${totalSteps.value}")

        Button(onClick = onButtonClick) {
            Text(text = "Switch to Main View")
        }

    }
}
