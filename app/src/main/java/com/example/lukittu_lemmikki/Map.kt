package com.example.lukittu_lemmikki

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.lukittu_lemmikki.ui.theme.LukittulemmikkiTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


val helper = Map()

class Map : ComponentActivity() {


    @Composable
    fun MapView(onButtonClick: () -> Unit) {

    }


    private val permission = arrayOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var locationRequired: Boolean = false

    override fun onResume() {
        super.onResume()
        if (locationRequired) {
            startLocationUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        locationCallback?.let {
            fusedLocationClient?.removeLocationUpdates(it)
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        locationCallback?.let {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 100
            )
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(3000)
                .setMaxUpdateDelayMillis(100)
                .build()

            fusedLocationClient?.requestLocationUpdates(
                locationRequest,
                it,
                Looper.getMainLooper()
            )
        }

    }


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapsInitializer.initialize(this, MapsInitializer.Renderer.LATEST){

        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {


            var currentLocation by remember {
                mutableStateOf(LatLng(0.toDouble(),0.toDouble()))
            }


            var cameraPositionState = rememberCameraPositionState("0.0, 0.0, 2.0")



            locationCallback = object: LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0)
                    for (location in p0.locations) {
                        currentLocation = LatLng(location.latitude, location.longitude)

                    }
                }
            }

            LukittulemmikkiTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LocationScreen(this@Map, currentLocation, cameraPositionState, onButtonClick = {
                        val intent = Intent(this@Map, MainActivity::class.java)
                        startActivity(intent)
                    })
                }
            }
        }
    }


    @Composable
    fun LocationScreen(context: Context, currentLocation: LatLng, cameraPositionState: CameraPositionState, onButtonClick: () -> Unit) {

        val contexti = LocalContext.current
        val sensorManager = contexti.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        val sessionSteps = remember { mutableStateOf(0) }
        val totalSteps = remember { mutableStateOf(0) }



        val sensorEventListener = remember {
            object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    if (event.sensor == stepSensor) {

                        val stepCount = event.values[0].toInt()
                        Log.d("StepCounter", "Step Count: $stepCount")

                        totalSteps.value = stepCount
                        sessionSteps.value += 1

                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                    // Not used
                }
            }
        }

        DisposableEffect(Unit) {
            sensorManager.registerListener(
                sensorEventListener,
                stepSensor,
                SensorManager.SENSOR_DELAY_UI
            )
            onDispose {
                sensorManager.unregisterListener(sensorEventListener)
            }
        }


        val launchMultiplePermissions = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()) {
                permissionMaps ->
            val areGranted = permissionMaps.values.reduce {acc, next -> acc && next}
            if (areGranted) {
                locationRequired = true
                startLocationUpdates()
                Toast.makeText(context, "Permission Granted!", Toast.LENGTH_SHORT).show()

            }
            else
            {
                Toast.makeText(context, "Permission Denied!", Toast.LENGTH_SHORT).show()
            }
        }

        if (permission.all {
                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
            })

        {
            // get location
            startLocationUpdates()
        }
        else
        {
            launchMultiplePermissions.launch(permission)
        }



        Box(modifier = Modifier.fillMaxSize()) {

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = rememberCameraPositionState{
                    position = CameraPosition.fromLatLngZoom(LatLng(65.0142,25.4719),11f)
                }
            ) {
                Marker(
                    state = MarkerState(
                        position = currentLocation
                    ),
                    title = "You",
                    snippet = "you're here!!!"
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth()
            ){
                BackButton (onClick = onButtonClick)
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){


                Text(text = "${currentLocation.latitude}/${currentLocation.longitude}")

                Button(onClick = {
                    sessionSteps.value = 0
                    sensorManager.registerListener(sensorEventListener, stepSensor, SensorManager.SENSOR_DELAY_NORMAL)
                    Toast.makeText(contexti, "Step counter started", Toast.LENGTH_SHORT).show()
                }) {
                    Text(text = "Start step counter")
                }
                Button(onClick = {
                    sensorManager.unregisterListener(sensorEventListener)
                    Toast.makeText(contexti, "Step counter stopped", Toast.LENGTH_SHORT).show()
                }) {
                    Text(text = "Stop Step Counter")
                }

                Text(text="Steps this session: ${sessionSteps.value}")
                Text(text="Total Steps: ${totalSteps.value}")

                var progress by remember { mutableStateOf(0.0f) }
                var level by remember { mutableStateOf(1) }

                MyProgressBar(progress, level)

                LaunchedEffect(key1 = totalSteps.value) {
                    progress += 0.01f

                    if (progress >= 1.0f) {
                        progress = 0.0f
                        level++
                    }
                }
            }
        }
    }
}
