package com.example.lukittu_lemmikki

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Anchor
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Config
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.core.Session
import com.google.ar.core.exceptions.UnavailableException
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode

class CustomArView : FrameLayout {

    private var arSession: Session? = null
    private val arSceneView by lazy {
        ArSceneView(context).also {
            // Ensure the AR session has been initialized before setting up the AR scene view.
            it.setupSession(arSession)
        }
    }

    // Constructors that call init() method to initialize the AR session and view.
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    // init() method that initializes the AR session.
    private fun init() {
        if (ArCoreApk.getInstance().checkAvailability(context).isSupported) {
            try {
                arSession = Session(context)
                arSession?.configure(Config(arSession).apply {
                    updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
                    depthMode = Config.DepthMode.AUTOMATIC
                })
            } catch (e: UnavailableException) {
                Log.e("CustomArView", "ARCore not available", e)
                // TODO: Handle the case where ARCore is not available, perhaps by notifying the user.
            }
        } else {
            // TODO: Handle the case where ARCore is not supported on this device.
        }
    }

    // Lifecycle methods that check if the AR scene view is initialized before calling its methods.
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // Resume the AR scene view if the session has been initialized.
        arSession?.let {
            arSceneView.resume()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // Pause the AR scene view if the session has been initialized.
        arSession?.let {
            arSceneView.pause()
        }
    }

    fun onDestroy() {
        arSession?.let {
            it.close()
            arSceneView.destroy() // It's safe to call because it implies arSession was initialized
        }
    }
    // Add an AR Fragment member if using Sceneform UX package
    private val arFragment = ArFragment()

// You will need to add the ArFragment to your layout or dynamically if not using XML layouts.
// Make sure to handle this in your Activity or where you manage your layout.
// e.g., in your Activity onCreate: if (savedInstanceState == null) { addArFragment() }



    // Call this method after the AR session is established and the view is ready.
    private fun setupTapListener() {
        arFragment.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane, motionEvent: MotionEvent ->
            // Create the Anchor.
            val anchor = hitResult.createAnchor()
            placeModel(anchor)
        }
    }

    private fun placeModel(anchor: Anchor) {
        // Load and place the model using ModelRenderable
        ModelRenderable.builder()
            .setSource(context, Uri.parse("/assets/models/burger.glb")) // Replace with your model's filename
            .build()
            .thenAccept { renderable ->
                addNodeToScene(anchor, renderable)
            }
            .exceptionally { throwable ->
                Log.e("CustomArView", "Error loading model", throwable)
                null
            }
    }

    private fun addNodeToScene(anchor: Anchor, renderable: ModelRenderable) {
        // Create the AnchorNode with the anchor that was created
        val anchorNode = AnchorNode(anchor).apply {
            setParent(arSceneView.scene)
        }

        // Create the node for the renderable and add it to the anchor.
        TransformableNode(arFragment.transformationSystem).apply {
            this.renderable = renderable
            setParent(anchorNode)
        }
    }
}



