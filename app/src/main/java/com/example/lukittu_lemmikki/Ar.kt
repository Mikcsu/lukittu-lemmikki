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

//Note: this works now but no depth or correct models inplace

@Composable
fun ARScreen(model: String, onButtonClick: () -> Unit, darkTheme: Boolean, preferencesManager: PreferencesManager) {
    val nodes = remember {
        mutableListOf<ArNode>()
    }
    val modelNode = remember {
        mutableStateOf<ArModelNode?>(null)
    }
    val placeModelButton = remember {
        mutableStateOf(false)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        ARScene(
            modifier = Modifier.fillMaxSize(),
            nodes = nodes,
            planeRenderer = true,
            onCreate = { arSceneView ->
                arSceneView.lightEstimationMode = Config.LightEstimationMode.DISABLED
                arSceneView.planeRenderer.isShadowReceiver = false
                modelNode.value = ArModelNode(arSceneView.engine, PlacementMode.INSTANT).apply {
                    loadModelGlbAsync(
                        glbFileLocation = "models/${model}.glb",
                        scaleToUnits = 0.3f
                    ) {
                        // Handle model loading completion or errors here if needed
                    }
                    onAnchorChanged = {
                        placeModelButton.value = !isAnchored
                    }
                    onHitResult = { node, hitResult ->
                        placeModelButton.value = node.isTracking
                    }
                }
                nodes.add(modelNode.value!!)
            },
            onSessionCreate = {
                false.also { planeRenderer.isVisible = it }
            }
        )

        BackButton(onClick = onButtonClick, darkTheme = darkTheme)

    }

    val selectedHat = preferencesManager.getSelectedHat() ?: "no_hat"
    val modelName = if (selectedHat == "no_hat") model else "$selectedHat$model"

    LaunchedEffect(key1 = modelName) {
        modelNode.value?.loadModelGlbAsync(
            glbFileLocation = "models/${modelName}.glb",
            scaleToUnits = 0.3f
        ) {
            // Optionally handle completion or errors
        }
        Log.d("ARLoading", "Loading model: $modelName")
    }
}