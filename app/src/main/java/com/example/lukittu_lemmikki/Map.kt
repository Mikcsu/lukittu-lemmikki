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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.lukittu_lemmikki.ui.theme.LukittulemmikkiTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

val helper = Map()

class Map : ComponentActivity() {

    //Declare the sensors
    private lateinit var sensorManager: SensorManager
    private lateinit var sensorEventListener: SensorEventListener
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var locationRequired: Boolean = false

    @Composable
    fun MapView(onButtonClick: () -> Unit, darkTheme: Boolean) {

    }

    //Permissions for Location access
    private val permission = arrayOf(
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )


    override fun onResume() {//Resume the location updates and unregister the stepsensor if initialization is not null
        super.onResume()
        if (locationRequired) {
            startLocationUpdates()
        }
        if (::sensorEventListener.isInitialized) {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }

    override fun onPause() { //Stop location updates
        super.onPause()
        locationCallback?.let {
            fusedLocationClient?.removeLocationUpdates(it)
        }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() { //Start Location updates
        locationCallback?.let {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 100 //Prioritize high accuracy with 100ms intervals
            )
                .setWaitForAccurateLocation(false) //Start locationupdates with coarser location
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
        //Initialize the map onCreate
        MapsInitializer.initialize(this, MapsInitializer.Renderer.LATEST){
        }
        //Declare the location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            var currentLocation by remember { //Current location need Latitude and Longitude
                mutableStateOf(LatLng(0.toDouble(),0.toDouble()))
            }
            var cameraPositionState = rememberCameraPositionState("0.0, 0.0, 2.0")

            locationCallback = object: LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0)
                    for (location in p0.locations) { //get the latitude and longitude
                        currentLocation = LatLng(location.latitude, location.longitude)
                    }
                }
            }
            LukittulemmikkiTheme {
                //A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) { //Location Screen needs context, the current LatLng, Cameraposition and callback to Mainmenu
                    LocationScreen(this@Map, currentLocation, cameraPositionState, onButtonClick = {
                        val intent = Intent(this@Map, MainActivity::class.java)
                        startActivity(intent)
                    })
                }
            }
        }
    }

    @SuppressLint("UnrememberedMutableState")
    @Composable
    fun LocationScreen(context: Context, currentLocation: LatLng, cameraPositionState: CameraPositionState, onButtonClick: () -> Unit) {

        val preferencesManager = PreferencesManager(context) //Preferencesmanager
        val contexti = LocalContext.current //Context for some reason had to be declared differently here
        val sensorManager = contexti.getSystemService(Context.SENSOR_SERVICE) as SensorManager //Sensors
        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) //Sensors
        val sessionSteps = remember { mutableStateOf(0) } //Sessionsteps to be reset all the time
        var totalSteps = preferencesManager.getSteps() //Totals steps from preferencesManager

        //Sensor functionality
        val sensorEventListener = remember {
            object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) { //Everytime the state of a sensor changes
                    if (event.sensor == stepSensor) { //If the sensor changing is step sensor
                        //Old accurate version, but stores every value
                        //val stepCount = event.values[0].toInt()
                        totalSteps += 1 //Add to steps
                        sessionSteps.value += 1
                        preferencesManager.saveSteps(totalSteps) //Save the steps
                    }
                }
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                    //Not used but has to exist
                }
            }
        }

        DisposableEffect(Unit) {
            onDispose { //When composable is shutdown, the sensor gets unregistered(doesn't count steps anymore)
                sensorManager.unregisterListener(sensorEventListener)
            }
        }

        //Check for all required permissions
        val launchMultiplePermissions = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()) {
                permissionMaps ->
            val areGranted = permissionMaps.values.reduce {acc, next -> acc && next}
            if (areGranted) {
                locationRequired = true
                startLocationUpdates()
                Toast.makeText(context, "Permission Granted!", Toast.LENGTH_SHORT).show()
            } else
            {
                Toast.makeText(context, "Permission Denied!", Toast.LENGTH_SHORT).show()
            }
        }

        if (permission.all { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED })
        { //get location if everything is granted
            startLocationUpdates()
        } else
        { //Ask for permissions again if not granted
            launchMultiplePermissions.launch(permission)
        }
        Box(modifier = Modifier.fillMaxSize()) {
            GoogleMap( //Google map function to display the map
                modifier = Modifier.fillMaxSize(),
                uiSettings = MapUiSettings(zoomControlsEnabled = false, compassEnabled = false), //Disable zoom controls and the compass
                cameraPositionState = rememberCameraPositionState{ //At first show Oulu location, with a zoom of 11f
                    position = CameraPosition.fromLatLngZoom(LatLng(65.0142,25.4719),11f)
                },
                ) {
                Marker( //Marker to show users current location
                    state = MarkerState(
                        position = currentLocation
                    ),
                    title = "You",
                    snippet = "you're here!!!"
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
            ){
                BackButton (onClick = { //Backbutton funtion to mainMenu, also disables stepcounter
                    onButtonClick()
                    sensorManager.unregisterListener(sensorEventListener)
                    Toast.makeText(contexti, "Step counter stopped", Toast.LENGTH_SHORT).show()
                }, darkTheme = false)
            }
            Column(
                modifier = Modifier.fillMaxSize()
                    .height(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally

            ){
                Row (
    horizontalArrangement = Arrangement.spacedBy(4.dp)
){
    Button(onClick = {  //Start the step sensor and count for SessionSteps
        sessionSteps.value = -1 //Restarts sessionsteps back to 0, (-1 because it is a sensor event which adds +1)
        sensorManager.registerListener(
            sensorEventListener,
            stepSensor,
            SensorManager.SENSOR_DELAY_GAME //Sensor delay suitable for games
        )
        Toast.makeText(contexti, "Step counter started", Toast.LENGTH_SHORT).show()
    }) {
        Text(text = "Start Activity")
    }
    Button(onClick = { //Stop step sensor
        sensorManager.unregisterListener(sensorEventListener)
        Toast.makeText(contexti, "Step counter stopped", Toast.LENGTH_SHORT).show()
    }) {
        Text(text = "Stop Activity")
    }
}
                totalSteps = preferencesManager.getSteps() //Get total steps
                Box( //Pop out the texts a little more against the background
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray)
                        .padding(2.dp)
                ) {//Displaying sessionsteps
                    Text(text = "Activity steps: ${sessionSteps.value}")
                }
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray)
                        .padding(2.dp)

                ) {//Displaying totalsteps
                    Text(text = "Total Steps: ${totalSteps}")
                }
                //Player progress vvv
                var money = preferencesManager.getMoney()
                var level = preferencesManager.getLevel()
                var totalStepsAtLevelStart = preferencesManager.getTotalStepsAtLevelStart()
                val progress = derivedStateOf {
                    //How many steps player has when they get level, always counting from "0"
                    val stepsInCurrentLevel = totalSteps - totalStepsAtLevelStart
                    // Calculate progress based on steps. Adjust the calculation as needed.
                    stepsInCurrentLevel.toFloat() / 50//Counts 1 steps as 2 for progress
                }
                LaunchedEffect(progress.value) { //when progress.value is changed, it updates to UI
                    if (progress.value >= 1.0f) { //when players progress reaches 100
                        Toast.makeText(contexti, "Level up! + 1000$ earned", Toast.LENGTH_SHORT).show()
                        level++ //Level up
                        money += 1000 //Gain currency
                        totalStepsAtLevelStart = totalSteps //Update the totalStepsAtLevelStart
                        preferencesManager.saveTotalStepsAtLevelStart(totalStepsAtLevelStart)
                        preferencesManager.saveMoney(money)//Update money
                    }
                    preferencesManager.saveProgress(progress.value) //Save progress
                    preferencesManager.saveLevel(level) //Save level
                }
                MyProgressBar(progress.value, level) //Progressbar function from Stats.kt
            }
        }
    }
}
