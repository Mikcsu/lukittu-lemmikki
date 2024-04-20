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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardrobeView(
    onModelSelect: (String) -> Unit,
    onButtonClick: () -> Unit,
    preferencesManager: PreferencesManager,
    darkTheme: Boolean
) {
    var showDialog by remember { mutableStateOf(false) }
    var dialogType by remember { mutableStateOf("none") }
    val selectedModel = remember { mutableStateOf(preferencesManager.getSelectedModel() ?: "deer") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Wardrobe") },
                navigationIcon = {
                    BackButton(onClick = onButtonClick, darkTheme = darkTheme)
                },
                actions = {
                    IconButton(onClick = {
                        showDialog = true
                        dialogType = "model"
                    }) {
                        val wardrobeBoxDrawable: Painter = if (darkTheme) {
                            painterResource(id = R.drawable.wardrobe_box_superior)
                        } else {
                            painterResource(id = R.drawable.wardrobe_box)
                        }
                        Image(
                            painter = wardrobeBoxDrawable,
                            contentDescription = null,
                            Modifier.size(40.dp)
                        )
                    }
                }
            )
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
                PetView(selectedModel.value, { hat ->
                    selectedModel.value = hat
                    preferencesManager.saveSelectedModel(hat) // Save the selected model with hat
                }, preferencesManager)
            }
        },
        floatingActionButton = {
            if (showDialog && dialogType == "model") {
                ModelDisplay(onModelSelect = { model ->
                    selectedModel.value = model
                    showDialog = false
                    dialogType = "none"
                }, onDismissRequest = {
                    showDialog = false
                    dialogType = "none"
                })
            }
        }
    )
}



@Composable
fun PetView(selectedModel: String, onSelectHat: (String) -> Unit, preferencesManager: PreferencesManager) {
    val modelWithHat = remember { mutableStateOf(selectedModel) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = {
                modelWithHat.value = selectedModel
                onSelectHat(selectedModel)

            }) { Text("No Hat") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                modelWithHat.value = "tophat$selectedModel"
                onSelectHat(selectedModel)
            }) { Text("Top Hat") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                modelWithHat.value = "propeller$selectedModel"
                onSelectHat(selectedModel)
            }) { Text("Propeller") }
        }
        Spacer(modifier = Modifier.height(16.dp)) // Adds space between the buttons and the image
        if (selectedModel.isNotEmpty()) {
            val modelId = when {
                modelWithHat.value.startsWith("tophat") -> getModelId("tophat$selectedModel")
                modelWithHat.value.startsWith("propeller") -> getModelId("propeller$selectedModel")
                else -> getModelId(selectedModel)
            }
            val modelImage = painterResource(id = modelId)
            Image(
                painter = modelImage,
                contentDescription = "Selected Pet Model with Hat",
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.CenterHorizontally) // Ensures the image is centered within the column
            )
        }
    }
}

fun getModelId(modelName: String): Int {
    return when (modelName) {
        "tophatgekko" -> R.drawable.tophatgekko
        "propellergekko" -> R.drawable.propellergekko
        "gekko" -> R.drawable.gekko
        "fish" -> R.drawable.fish
        "tophatfish" -> R.drawable.tophatfish
        "propellerfish" -> R.drawable.propellerfish
        "bird" -> R.drawable.bird
        "propellerbird" -> R.drawable.propellerbird
        "tophatbird" -> R.drawable.tophatbird
        "hamster" -> R.drawable.hamster
        "snake" -> R.drawable.snake
        "octopus" -> R.drawable.octopus
        "monkey" -> R.drawable.monkey
        "deer" -> R.drawable.deer

        // Add other model cases as needed
        else -> R.drawable.assaultpet
    }
}








// Assume you have a Composable function `ModelDisplay` similar to `ClothesDisplay`
// that you'll implement to handle model selection logic.
@Composable
fun ModelDisplay(onModelSelect: (String) -> Unit, onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Model Selection") },
        text = { ModelList(onModelSelect) },
        confirmButton = {
            Button(onClick = onDismissRequest) { Text("Close") }
        }
    )
}
data class Model(val id: Int, val name: String)

@Composable
fun ModelList(onModelSelect: (String) -> Unit) {
    // List of all model images
    val modelList = listOf(
        Model(R.drawable.gekko, "gekko"),
        Model(R.drawable.deer, "deer"),
        Model(R.drawable.fish, "fish"),
        Model(R.drawable.hamster, "hamster"),
        Model(R.drawable.monkey, "monkey"),
        Model(R.drawable.octopus, "octopus"),
        Model(R.drawable.snake, "snake"),
        Model(R.drawable.bird, "bird")
        // Add more images as needed
    )

    val modelRows = modelList.chunked(2)

    LazyColumn {
        items(modelRows) { rowModels ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                for (model in rowModels) {
                    Image(
                        painter = painterResource(id = model.id),
                        contentDescription = "Model item",
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp)
                            .clickable {
                                onModelSelect(model.name) // Pass the model name when clicked
                                Log.d("ModelList", "Model ${model.name} clicked") // Log to console
                            }
                    )
                    // Add spacer if there's more than one image in the row
                    if (rowModels.size > 1) {
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
                // If there's only one image in the row, fill the remaining space
                if (rowModels.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

