package com.example.lukittu_lemmikki

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService

interface MapNavigation {
    fun navigateToMap()
}

class MainActivity : ComponentActivity(), MapNavigation {

    private lateinit var sensorManager: SensorManager
    private var sensor: Sensor? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp(mapNavigation = this)
        }
        checkSensorPermission()
    }

    override fun navigateToMap() {
        val intent = Intent(this, Map::class.java)
        startActivity(intent)
    }


private fun checkSensorPermission() {
    val bodySensorsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS)
    val activityRecognitionPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)

    if (bodySensorsPermission != PackageManager.PERMISSION_GRANTED || activityRecognitionPermission != PackageManager.PERMISSION_GRANTED) {
        val permissionsToRequest = mutableListOf<String>()
        if (bodySensorsPermission != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.BODY_SENSORS)
        }
        if (activityRecognitionPermission != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACTIVITY_RECOGNITION)
        }

        requestPermissionLauncher.launch(permissionsToRequest.firstOrNull())
    } else {
        initializeSensor()
    }
}


private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        isGranted -> if (isGranted) {
    initializeSensor()
} else {
    Toast.makeText(this, "Permission denied for sensors", Toast.LENGTH_SHORT).show()

}
}
private fun initializeSensor(){
    sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

}
}


@Composable
fun MyApp(mapNavigation: MapNavigation) {
    var currentView by remember { mutableStateOf(1) }

    MaterialTheme {
        when (currentView) {
            1 -> MainActivityView(
                onMapButtonClick = { mapNavigation.navigateToMap() },
                onArButtonClick = { currentView = 3 },
                onWardrobeButtonClick = { currentView = 4}
            )
            2 -> helper.MapView(onButtonClick = { currentView = 1}) // Launch MapView with the callback
            3 -> ArView(onButtonClick = { currentView = 1 })
            4 -> WardrobeView (onButtonClick = { currentView = 1})
        }
    }
}

@Composable
fun MainActivityView(
    onMapButtonClick: () -> Unit,
    onArButtonClick: () -> Unit,
    onWardrobeButtonClick: () -> Unit
)
{
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Main Activity View")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onMapButtonClick) {

            Text(text = "Switch to Map View")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onArButtonClick) {
            Text(text = "Switch to Ar View")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onWardrobeButtonClick) {
            Text(text = "Switch to Wardrobe View")
        }
    }
}