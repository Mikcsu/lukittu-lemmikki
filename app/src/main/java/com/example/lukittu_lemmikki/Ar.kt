package com.example.lukittu_lemmikki

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ArView(onButtonClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxHeight().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Ar View")
        Button(onClick = onButtonClick) {
            Text(text = "Switch to Main View")
        }
    }
}