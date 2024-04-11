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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.lukittu_lemmikki.ui.theme.LukittulemmikkiTheme


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
    var selectedModel by remember { mutableStateOf("deer") } // Default model

    LukittulemmikkiTheme {
        when (currentView) {
            1 -> MainActivityView(
                onMapButtonClick = { mapNavigation.navigateToMap() },
                onArButtonClick = { currentView = 3 },
                onWardrobeButtonClick = { currentView = 4}
            )
            2 -> helper.MapView(onButtonClick = { currentView = 1}) // Launch MapView with the callback
            3 -> ArView(selectedModel, onButtonClick = { currentView = 1 }) // Pass the selected model to ArView
            4 -> WardrobeView (
                onModelSelect = { model ->
                    selectedModel = model // Update the selected model
                    //currentView = 3 // Switch to AR view
                },
                onButtonClick = { currentView = 1}
            )
        }
    }
}

@Composable
fun MainActivityView(
    onMapButtonClick: () -> Unit,
    onArButtonClick: () -> Unit,
    onWardrobeButtonClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.assaultpet),
            contentDescription = null, // Content description is null as it's a decorative image
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds // Scale the image to fill the bounds of the Box
        )
        Column(
            modifier = Modifier.wrapContentSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onMapButtonClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Map")
            }
            Button(
                onClick = onArButtonClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "AR")
            }
            Button(
                onClick = onWardrobeButtonClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Wardrobe")
            }
        }
    }
}
