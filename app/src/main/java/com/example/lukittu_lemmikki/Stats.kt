package com.example.lukittu_lemmikki

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MyProgressBar(progress: Float, level: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.weight(1f))
        //Texts and Progress bar at the bottom of the screen
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
                    .padding(4.dp)
            ) {
                Text(
                    text = "Level: $level",
                    fontSize = 20.sp,
                    color = Color.Black
                )
            }
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
                    .padding(4.dp)
            ) {
                Text(
                    text = "Progress: ${(progress * 100).toInt()}%",
                    fontSize = 20.sp,
                    color = Color.Black
                )
            }
            LinearProgressIndicator( //Progressbar function
                progress = progress,
                color = Color.Red,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }
    }
}

