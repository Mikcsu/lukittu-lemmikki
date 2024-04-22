package com.example.lukittu_lemmikki

import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardrobeView(
    onModelSelect: (String) -> Unit,
    onButtonClick: () -> Unit,
    preferencesManager: PreferencesManager, // Pass PreferencesManager
    darkTheme: Boolean
) {
    var showDialog by remember { mutableStateOf(false) }
    var dialogType by remember { mutableStateOf("none") } // "none", "clothes", "model"
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    var money = preferencesManager.getMoney()

    val selectedModel = remember { mutableStateOf(preferencesManager.getSelectedModel() ?: "deer") }
    var selectedHat by remember { mutableStateOf(preferencesManager.getSelectedHat() ?: "no_hat") }


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Wardrobe") },
                navigationIcon = {
                    BackButton(onClick = onButtonClick, darkTheme = darkTheme)
                },
                actions = {
                    Column {
                        IconButton(onClick = {
                            showDialog = true
                            dialogType = "model"
                        }) {
                            val wardrobeBoxDrawable: Painter = if (darkTheme) {
                                painterResource(id = R.drawable.wardrobe_box_superior) // Use the dark theme image
                            } else {
                                painterResource(id = R.drawable.wardrobe_box) // Use the light theme image
                            }
                            Image(
                                painter = wardrobeBoxDrawable,
                                contentDescription = null,
                                Modifier.size(40.dp)
                            )
                        }
                        Text("Money: $money")
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
                PetView(selectedModel.value) { hat ->
                    // Here you might update the state of hats if needed
                }
            }
        }
    )

    if (showDialog && dialogType == "model") {
        ModelDisplay(onModelSelect = { model ->
            onModelSelect(model)
            preferencesManager.saveSelectedModel(model) // Save the selected model when it is selected
            selectedModel.value = model
            showDialog = false
            dialogType = "none"
        }, onSelectHat = { hat ->
            // Handle hat selection here if needed

        },onDismissRequest = {
            showDialog = false
            dialogType = "none"
        })
    }
}

@Composable
fun PetView(selectedModel: String, onSelectHat: (String) -> Unit) {


    val preferencesManager = PreferencesManager(LocalContext.current)
    var selectedHat by remember { mutableStateOf(preferencesManager.getSelectedHat() ?: "no_hat")}
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Button(onClick = {
                onSelectHat("no_hat")
                selectedHat = "no_hat"
                preferencesManager.saveSelectedHat("no_hat") // Save the selected model when no hat is selected
            }, enabled = selectedHat != "no_hat") { Text("No Hat") }

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                onSelectHat("propeller")
                selectedHat = "propeller"
                preferencesManager.saveSelectedHat("propeller") // Save the selected model with propeller hat
                Log.d("Hat", "Propeller Hat selected. Model: ${preferencesManager.getSelectedModel()}") // Log to console
            }, enabled = selectedHat != "propeller") { Text("Propeller Hat") }

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                onSelectHat("tophat")
                selectedHat = "tophat"
                preferencesManager.saveSelectedHat("tophat") // Save the selected model with top hat
            }, enabled = selectedHat != "tophat") { Text("Top Hat") }
        }
        Spacer(modifier = Modifier.height(16.dp)) // Adds space between the buttons and the image
        var modelId = R.drawable.assaultpet // Default value
        if (selectedModel.isNotEmpty()) {
            modelId = when {
                selectedModel == "gekko" && selectedHat == "no_hat" -> R.drawable.gekko
                selectedModel == "gekko" && selectedHat == "propeller" -> R.drawable.propellergekko
                selectedModel == "gekko" && selectedHat == "tophat" -> R.drawable.tophatgekko
                selectedModel == "fish" && selectedHat == "no_hat" -> R.drawable.fish
                selectedModel == "fish" && selectedHat == "propeller" -> R.drawable.propellerfish
                selectedModel == "fish" && selectedHat == "tophat" -> R.drawable.tophatfish
                selectedModel == "bird" && selectedHat == "no_hat" -> R.drawable.bird
                selectedModel == "bird" && selectedHat == "propeller" -> R.drawable.propellerbird
                selectedModel == "bird" && selectedHat == "tophat" -> R.drawable.tophatbird
                selectedModel == "deer" && selectedHat == "no_hat" -> R.drawable.deer
                selectedModel == "snake" && selectedHat == "no_hat" -> R.drawable.snake
                selectedModel == "octopus" && selectedHat == "no_hat" -> R.drawable.octopus
                selectedModel == "hamster" && selectedHat == "no_hat" -> R.drawable.hamster
                selectedModel == "monkey" && selectedHat == "no_hat" -> R.drawable.monkey
                // Add more cases as needed
                else -> R.drawable.assaultpet// Default case
            }
        }
        val modelImage = painterResource(id = modelId)
        Image(
            painter = modelImage,
            contentDescription = "Selected Pet Model",
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.CenterHorizontally) // Ensures the image is centered within the column
        )
    }
}




// Assume you have a Composable function `ModelDisplay` similar to `ClothesDisplay`
// that you'll implement to handle model selection logic.
@Composable
fun ModelDisplay(onModelSelect: (String) -> Unit, onDismissRequest: () -> Unit, onSelectHat: (String) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Model Selection") },
        text = { ModelList(onModelSelect, onSelectHat) },
        confirmButton = {
            Button(onClick = onDismissRequest) { Text("Close") }
        }
    )
}

@Composable
fun ModelList(onModelSelect: (String) -> Unit, onSelectedHat: (String) -> Unit) {
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    var money by remember { mutableStateOf(preferencesManager.getMoney()) }

    // Use remember to make sure the list recomposes when the bought property changes
    val models = remember { getModels() }
    val modelRows = models.chunked(2)

    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
        items(modelRows) { rowModels ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                for (model in rowModels) {
                    Column {
                        IconButton(
                            onClick = {
                                if (!preferencesManager.isModelBought(model.id)) {
                                    if (money >= model.cost) {
                                        val updatedMoney = money - model.cost
                                        preferencesManager.saveMoney(updatedMoney) // Update money in PreferencesManager
                                        money = updatedMoney // Update the local money state
                                        onModelSelect(model.name)
                                        preferencesManager.saveModelBought(model.id, true)
                                        Toast.makeText(context, "${model.name.capitalize()} Bought & Selected!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Insufficient funds", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    onModelSelect(model.name)
                                    Toast.makeText(context, "${model.name.capitalize()} Selected!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = true
                        ) {
                            Image(
                                painter = painterResource(id = model.id),
                                contentDescription = "Model item",
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(4.dp)
                            )
                        }
                        Text(if (preferencesManager.isModelBought(model.id)) "Owned" else "${model.cost}$")
                    }
                    // Add spacer if there's more than one image in the row
                    if (rowModels.size > 1) {
                        Spacer(modifier = Modifier.width(80.dp))
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



fun getModels(): List<Model>{
    return listOf(
        Model(R.drawable.gekko, "gekko", 100),
        Model(R.drawable.deer, "deer", 100),
        Model(R.drawable.fish, "fish", 100),
        Model(R.drawable.hamster, "hamster", 200),
        Model(R.drawable.monkey, "monkey", 500),
        Model(R.drawable.octopus, "octopus", 500),
        Model(R.drawable.snake, "snake", 500),
        Model(R.drawable.bird, "bird", 1000)
    )
}
