package com.example.lukittu_lemmikki

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

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

