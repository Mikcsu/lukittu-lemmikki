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
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.lukittu_lemmikki.ui.theme.LukittulemmikkiTheme


interface MapNavigation {
    fun navigateToMap()
}

class MainActivity : ComponentActivity(), MapNavigation {

    //List of every permission required to run the program flawlessly
    private val permissions = arrayOf(
        Manifest.permission.BODY_SENSORS,
        Manifest.permission.ACTIVITY_RECOGNITION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.CAMERA
    )

    //Requesting every permission
    private val requestPermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions.all { it.value }) {
            //All permissions are granted
            Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show()
        } else {
            //Not all permissions are granted
            Toast.makeText(this, "Not all permissions granted", Toast.LENGTH_SHORT).show()
        }
    }

    //Declaring the Sensors needed
    private lateinit var sensorManager: SensorManager
    private var sensor: Sensor? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp(mapNavigation = this)
        }

        //Check if all permissions are already granted
        if (permissions.all { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }) {
            //All permissions are granted, you can proceed with your functionality
        } else {
            //Not all permissions are granted, request the necessary permissions
            requestPermissionsLauncher.launch(permissions)
        }
        checkSensorPermission()
    }


    override fun navigateToMap() { //Create an intent to start the Map when transferring between screens
        val intent = Intent(this, Map::class.java)
        startActivity(intent)
    }

//Check for body & activity sensor permissions and initialize if both are granted
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
    //initialize the sensors
private fun initializeSensor(){
    sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
}
}
@Composable
fun MyApp(mapNavigation: MapNavigation) {

    //Settings from settings page and what model to pass to AR
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context) //SharedPreferences
    var currentView by remember { mutableStateOf(1) } //Current view
    var selectedModel by remember { mutableStateOf(preferencesManager.getSelectedModel() ?: "deer") } //Default model
    var darkTheme by remember { mutableStateOf(preferencesManager.getDarkTheme()) } //Default theme
    var isSkinwalkerMode by remember { mutableStateOf(preferencesManager.getSkinwalkerMode()) } //Default Skinwalker mode
    var money = preferencesManager.getMoney() //NOT USED

    LukittulemmikkiTheme(darkTheme = darkTheme) {
        when (currentView) { //Navigation between screens
            1 -> MainActivityView( //MAINMENU
                onMapButtonClick = { mapNavigation.navigateToMap() },//MAPVIEW
                onArButtonClick = { currentView = 3 },//ARSCREEN
                onWardrobeButtonClick = { currentView = 4},//WARDROBE
                onSettingsButtonClick = { currentView = 5 } //SETTINGS
            )
            2 -> helper.MapView(onButtonClick = { currentView = 1 }, darkTheme = darkTheme) //MAPVIEW & Pass darkTheme to MapView
            3 -> ARScreen(selectedModel, onButtonClick = { currentView = 1 }, darkTheme = darkTheme, preferencesManager = preferencesManager) //ARSCREEN
            4 -> WardrobeView ( //WARDROBE
                onModelSelect = { model ->
                    selectedModel = if (isSkinwalkerMode) "sw$model" else model // Update the selected model
                    preferencesManager.saveSelectedModel(selectedModel) // Save the selected model to shared preferences
                },
                onButtonClick = { currentView = 1}, darkTheme = darkTheme, preferencesManager = preferencesManager //Handler back to MAINMENU
            )
            5 -> Settings( //SETTINGS
                darkTheme = darkTheme,
                onDarkThemeChange = { darkTheme = it },
                selectedModel = selectedModel,
                onModelChange = { model ->
                    selectedModel = if (isSkinwalkerMode) "sw$model" else model //Update the selected model & Add skinwalker
                    preferencesManager.saveSelectedModel(selectedModel) //Save the selected model to shared preferences
                },
                isSkinwalkerMode = isSkinwalkerMode, //Switch the model to SkinWalker mode if enabled
                onSkinwalkerModeChange = { isChecked ->
                    isSkinwalkerMode = isChecked
                    selectedModel = if (isChecked) {
                        if (!selectedModel.startsWith("sw")) "sw$selectedModel" else selectedModel
                    } else {
                        if (selectedModel.startsWith("sw")) selectedModel.removePrefix("sw") else selectedModel
                    }
                    preferencesManager.saveSkinwalkerMode(isSkinwalkerMode)
                    preferencesManager.saveSelectedModel(selectedModel) //Save the updated selected model to shared preferences
                },
                onMainButtonClick = { currentView = 1 },
                onSettingsButtonClick = { currentView = 5 }
            ) //Pass the handler to Settings
        }
    }
}

@Composable
fun MainActivityView( //Only buttons for navigation & Image
    onMapButtonClick: () -> Unit,
    onArButtonClick: () -> Unit,
    onWardrobeButtonClick: () -> Unit,
    onSettingsButtonClick: () -> Unit,
) {
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    var money = preferencesManager.getMoney()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image( //BACKGROUND
            painter = painterResource(id = R.drawable.backgroundmain),
            contentDescription = null, // Content description is null as it's a decorative image
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds // Scale the image to fill the bounds of the Box
        )
        Column(
            modifier = Modifier.wrapContentSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //Title
            Text(text = "AR-Lemmikki")
            Text(text = "Money: $money")
            Button( //MAP BUTTON
                onClick = onMapButtonClick,
                modifier = Modifier.size(width = 200.dp, height = 40.dp)
            ) {
                Text(text = "Map")
            }
            Button( //AR BUTTON
                onClick = onArButtonClick,
                modifier = Modifier.size(width = 200.dp, height = 40.dp)
            ) {
                Text(text = "AR")
            }
            Button( // WARDROBE BUTTON
                onClick = onWardrobeButtonClick,
                modifier = Modifier.size(width = 200.dp, height = 40.dp)
            ) {
                Text(text = "Wardrobe")
            }
            Button( // SETTINGS BUTTON
                onClick = onSettingsButtonClick,
                modifier = Modifier.size(width = 200.dp, height = 40.dp)
            ) {
                Text(text = "Settings")
            }
        }
    }
}