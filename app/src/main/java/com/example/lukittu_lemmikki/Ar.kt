package com.example.lukittu_lemmikki

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.lukittu_lemmikki.CustomArView
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
@Composable
fun ArView(onButtonClick: () -> Unit, onBackClick: () -> Unit) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxHeight().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = onBackClick, modifier = Modifier.padding(end = 8.dp)) {
                Text("<- Back")
            }
            Text(text = "AR View")
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Embed CustomArView within your Compose UI
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { CustomArView(context).apply {
                // Initialize or set up your CustomArView as needed
            }}
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onButtonClick) {
            Text(text = "Switch to Main View")
        }
    }
}
