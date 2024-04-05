package com.example.lukittu_lemmikki

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardrobeView(onButtonClick: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) } // State to control dialog visibility

    Scaffold(
        topBar = {
            SmallTopAppBar(title = { Text("Wardrobe") })
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AndroidView(
                    factory = { context ->
                        // Your AR view setup
                        CustomArView(context).apply {
                            // Configuration of your AR view
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
                Text(text = "Wardrobe Items will be listed here")
                Button(onClick = { showDialog = true }) {
                    Text("Show Clothes Details")
                }
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = onButtonClick) {
                    Text("Back to Main")
                }
            }
        }
    )

    // Clothes dialog
    if (showDialog) {
        ClothesDisplay(onDismissRequest = { showDialog = false })
    }
}
@Composable
fun ClothesList() {
    // List of all images
    val imageList = listOf(
        R.drawable.projectshirt1,
        R.drawable.projectshirt2,
        R.drawable.projectshirt3,
        R.drawable.projectshirt4,
        R.drawable.projectdress,
        R.drawable.projectdress2,
        R.drawable.projectdress3,
        R.drawable.projectdress4,
        R.drawable.projectshirt5
        // Add more images as needed
    )

    val imageRows = imageList.chunked(2)

    LazyColumn {
        items(imageRows) { rowImages ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                for (image in rowImages) {
                    Image(
                        painter = painterResource(id = image),
                        contentDescription = "Clothing item",
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                            .clickable {
                                Log.d("ClothesList", "Item $image clicked") // Log to console
                            }
                    )
                    // Add spacer if there's more than one image in the row
                    if (rowImages.size > 1) {
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
                // If there's only one image in the row, fill the remaining space
                if (rowImages.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}



@Composable
fun ClothesDisplay(onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Clothes Details") },
        text = { Text("Details of the clothes will be shown here.") },
        confirmButton = {
            Button(onClick = onDismissRequest) { Text("Close") }
            ClothesList()
        }
    )
}
