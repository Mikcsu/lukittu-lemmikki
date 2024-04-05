package com.example.lukittu_lemmikki

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MyProgressBar(progress: Float, level: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Level: $level",
            fontSize = 20.sp,
            color = Color.Black
        )

        Text(
            text = "Progress: ${(progress * 100).toInt()}%",
            fontSize = 20.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))
        LinearProgressIndicator(
            progress = progress,
            color = Color.Blue,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
