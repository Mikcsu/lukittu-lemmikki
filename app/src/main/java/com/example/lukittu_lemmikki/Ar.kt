package com.example.lukittu_lemmikki

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.ar.core.Config
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.ArNode
import io.github.sceneview.ar.node.PlacementMode

//Note: this works now but no depth or correct models inplace


@Composable
fun ArView(model: String, onButtonClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onButtonClick,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Return")
            }
            Text(
                text = "AR View",
                modifier = Modifier
                    .weight(2f)
                    .padding(start = 8.dp) // Adjust padding as needed for visual appeal
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Integrate the AR Screen directly here
        ARScreen(model) // Pass the model name to ARScreen

    }
}

@Composable
fun ARScreen(model: String) {
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
                        scaleToUnits = 0.8f
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

    }

    LaunchedEffect(key1 = model) {
        modelNode.value?.loadModelGlbAsync(
            glbFileLocation = "models/${model}.glb",
            scaleToUnits = 0.5f
        ) {
            // Optionally handle completion or errors
        }
        Log.e("ARLoading", "Loading model: $model")
    }
}
