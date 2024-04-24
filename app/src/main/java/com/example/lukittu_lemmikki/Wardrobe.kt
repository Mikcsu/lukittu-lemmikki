package com.example.lukittu_lemmikki

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WardrobeView(
    onModelSelect: (String) -> Unit, //What model has been selected
    onButtonClick: () -> Unit, //Back to mainmenu
    preferencesManager: PreferencesManager, //Pass PreferencesManager
    darkTheme: Boolean
) {
    var showDialog by remember { mutableStateOf(false) } //Show dialog by true
    var dialogType by remember { mutableStateOf("none") } //For different types: "none", "clothes", "model"
    val context = LocalContext.current //Context this
    val preferencesManager = PreferencesManager(context) //Pass preferencesManager in this context
    var money = preferencesManager.getMoney() //For money text and buy logic

    val selectedModel = remember { mutableStateOf(preferencesManager.getSelectedModel() ?: "deer") } //Selected model, if null -> Deer
    var selectedHat by remember { mutableStateOf(preferencesManager.getSelectedHat() ?: "no_hat") } //Not used

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(//Title
                title = { Text("Wardrobe") },
                navigationIcon = { //Back to mainmenu button
                    BackButton(onClick = onButtonClick, darkTheme = darkTheme)
                },
                actions = {
                    Column {
                        IconButton(onClick = { //Open the dialog for models, buy the Deer default model
                            preferencesManager.saveModelBought(2131165297, true)
                            showDialog = true
                            dialogType = "model"
                        }) {
                            val wardrobeBoxDrawable: Painter = if (darkTheme) {
                                painterResource(id = R.drawable.wardrobe_box_superior) //Use the dark theme image
                            } else {
                                painterResource(id = R.drawable.wardrobe_box) //Use the light theme image
                            }
                            Image(
                                painter = wardrobeBoxDrawable,
                                contentDescription = null,
                                Modifier.size(40.dp)
                            )
                        }
                        Text("Money: $money") //Show money
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
                    //Here you might update the state of hats if needed
                }
            }
        }
    )

    if (showDialog && dialogType == "model") { //Renders the content to the selected dialog
        ModelDisplay(onModelSelect = { model ->
            onModelSelect(model)
            preferencesManager.saveSelectedModel(model) //Save the selected model when it is selected
            selectedModel.value = model
            showDialog = false
            dialogType = "none"
        }, onSelectHat = { hat ->
            //Handle hat selection here if needed
        },onDismissRequest = {
            showDialog = false
            dialogType = "none"
        })
    }
}

@Composable //The mainscreen of Wardrobe, displaying animal image and their hat buttons
fun PetView(selectedModel: String, onSelectHat: (String) -> Unit) {

    val preferencesManager = PreferencesManager(LocalContext.current)
    var selectedHat by remember { mutableStateOf(preferencesManager.getSelectedHat() ?: "no_hat")}
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth().padding(5.dp)
    ) {
        Row(horizontalArrangement = Arrangement.Center,verticalAlignment = Alignment.CenterVertically,modifier = Modifier.fillMaxWidth()) {
            Button(onClick = { //Remove the hat button
                onSelectHat("no_hat")
                selectedHat = "no_hat"
                preferencesManager.saveSelectedHat("no_hat") //Save the selected model when no hat is selected
            }, enabled = selectedHat != "no_hat") { Text("No Hat") }//Disables the button when the current is selected

            Spacer(modifier = Modifier.width(2.dp))

            Button(onClick = { //Propeller hat button
                onSelectHat("propeller")
                selectedHat = "propeller"
                preferencesManager.saveSelectedHat("propeller") //Save the selected model with propeller hat
            }, enabled = selectedHat != "propeller") { Text("Propeller") }//Disables the button when the current hat is selected

            Spacer(modifier = Modifier.width(2.dp))

            Button(onClick = { //Tophat button
                onSelectHat("tophat")
                selectedHat = "tophat"
                preferencesManager.saveSelectedHat("tophat") // Save the selected model with top hat
            }, enabled = selectedHat != "tophat") { Text("Tophat") } //Disables the button when the current hat is selected
        }
        Spacer(modifier = Modifier.height(16.dp)) //Adds space between the buttons and the image
        var modelId = R.drawable.assaultpet //Default image if no png exists to the animal with hat
        if (selectedModel.isNotEmpty()) {
            modelId = when { //Models and their respective images to their hats, not all models have hats
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
                else -> R.drawable.assaultpet//Default image when no hat exists
            }
        }
        val modelImage = painterResource(id = modelId)
        Image( //Paint the image for the animal with or without hats, or default image
            painter = modelImage,
            contentDescription = "Selected Pet Model",
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.CenterHorizontally) //Ensures the image is centered within the column
        )
    }
}


@Composable //Render the alerDialog
fun ModelDisplay(onModelSelect: (String) -> Unit, onDismissRequest: () -> Unit, onSelectHat: (String) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Model Selection") }, //Title
        text = { ModelList(onModelSelect, onSelectHat) }, //Models
        confirmButton = {
            Button(onClick = onDismissRequest) { Text("Close") }
        }
    )
}

@Composable //Model list
fun ModelList(onModelSelect: (String) -> Unit, onSelectedHat: (String) -> Unit) {
    val context = LocalContext.current
    val preferencesManager = PreferencesManager(context)
    var money by remember { mutableStateOf(preferencesManager.getMoney()) }

    //Use remember to make sure the list recomposes when the bought property changes
    val models = remember { getModels() }
    val modelRows = models.chunked(2)

    //Display the selectable models images in columns
    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
        items(modelRows) { rowModels ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                for (model in rowModels) {
                    Column {
                        IconButton( //Animal model image as a pressable button
                            onClick = {
                                if (!preferencesManager.isModelBought(model.id)) { //If user owns the animal it can be bought
                                    if (money >= model.cost) { //If user has required amount of money
                                        val updatedMoney = money - model.cost //Deduct money
                                        preferencesManager.saveMoney(updatedMoney) //Update money in PreferencesManager
                                        money = updatedMoney //Update the local money state
                                        onModelSelect(model.name)
                                        preferencesManager.saveModelBought(model.id, true) //Save into preferencesManager the model bought
                                        Toast.makeText(context, "${model.name.capitalize()} Bought & Selected!", Toast.LENGTH_SHORT).show() //Let the user know
                                    } else {
                                        Toast.makeText(context, "Insufficient funds", Toast.LENGTH_SHORT).show() //Let the user know
                                    }
                                } else { //If the user already owns the model, it will only be passed as a selected model
                                    onModelSelect(model.name)
                                    Toast.makeText(context, "${model.name.capitalize()} Selected!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = true //The animal image buttons are always enabled
                        ) {
                            Image(  //What model is displayed in the respective spot
                                painter = painterResource(id = model.id),
                                contentDescription = "Model item",
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(4.dp)
                            )
                        }
                        Text(if (preferencesManager.isModelBought(model.id)) "Owned" else "${model.cost}$") //Display price or owned depending on the models state
                    }
                    //Add spacer if there's more than one image in the row
                    if (rowModels.size > 1) {
                        Spacer(modifier = Modifier.width(80.dp))
                    }
                }
                //If there's only one image in the row, fill the remaining space
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
