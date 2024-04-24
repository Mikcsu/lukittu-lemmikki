package com.example.lukittu_lemmikki

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.google.ar.core.Config
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.ArNode
import io.github.sceneview.ar.node.PlacementMode

//This works but no depth inplace

@Composable
fun ARScreen(model: String, onButtonClick: () -> Unit, darkTheme: Boolean, preferencesManager: PreferencesManager) {
    val nodes = remember { mutableListOf<ArNode>() }
    val modelNode = remember { mutableStateOf<ArModelNode?>(null) }
    val placeModelButton = remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        ARScene(
            modifier = Modifier.fillMaxSize(),
            nodes = nodes,
            planeRenderer = true,
            onCreate = { arSceneView -> //On creation instantly placing the model.glb
                arSceneView.lightEstimationMode = Config.LightEstimationMode.DISABLED //Turn off lighting
                arSceneView.planeRenderer.isShadowReceiver = false //Turn off shadows
                modelNode.value = ArModelNode(arSceneView.engine, PlacementMode.INSTANT).apply { //
                    loadModelGlbAsync( //Call the model loading
                        //What model to load and the scale of the model, (1f was huge)
                        glbFileLocation = "models/${model}.glb",
                        scaleToUnits = 0.3f
                    ) {
                        //Handle model loading completion or errors here if needed
                    }
                    onAnchorChanged = {//model can be dragged by finger which changes anchor
                        placeModelButton.value = !isAnchored
                    }
                    onHitResult = { node, hitResult -> //Finger press = On Hit
                        placeModelButton.value = node.isTracking
                    }
                }
                nodes.add(modelNode.value!!)
            },
            onSessionCreate = { //hides the AR scenes "GRID"
                false.also { planeRenderer.isVisible = it }
            }
        )
        //Backbutton to mainmenu
        BackButton(onClick = onButtonClick, darkTheme = darkTheme)
    }

    //Handle the model loading when the modelname changes
    //Add the respective hat in front of the modelname if hat is selected
    val selectedHat = preferencesManager.getSelectedHat() ?: "no_hat"
    val modelName = if (selectedHat == "no_hat") model else "$selectedHat$model"

    LaunchedEffect(key1 = modelName) { //Listener for modelname
        modelNode.value?.loadModelGlbAsync( //Call for the model loading
            //What model to load and the scale of the model, (1f was huge)
            glbFileLocation = "models/${modelName}.glb",
            scaleToUnits = 0.3f
        ) {
            //Optionally handle completion or errors
        }
        Log.d("ARLoading", "Loading model: $modelName")
    }
}