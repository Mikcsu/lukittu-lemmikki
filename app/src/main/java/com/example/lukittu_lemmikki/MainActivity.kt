package com.example.lukittu_lemmikki


import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

interface MapNavigation {
    fun navigateToMap()
}

class MainActivity : ComponentActivity(), MapNavigation {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp(mapNavigation = this)
        }
    }

    override fun navigateToMap() {
        val intent = Intent(this, Map::class.java)
        startActivity(intent)
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