package com.example.lukittu_lemmikki

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.InstantPlacementPoint
import com.google.ar.core.Session
import com.google.ar.core.exceptions.UnavailableException
import com.google.ar.sceneform.ArSceneView

class CustomArView : FrameLayout {
    private var arSession: Session? = null
    private lateinit var arSceneView: ArSceneView
    private var placementIsDone = false

    private var tapX = 0f
    private var tapY = 0f
    private var didUserTap = false

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
        // Initialize AR session
        if (ArCoreApk.getInstance().checkAvailability(context).isSupported) {
            try {
                arSession = Session(context).also { session ->
                    // Configure the session
                    val config = Config(session).apply {
                        updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
                        depthMode = Config.DepthMode.AUTOMATIC
                        instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP // Enable Instant Placement
                    }
                    session.configure(config)
                }
            } catch (e: UnavailableException) {
                // Handle exception
            }
        }

        // Initialize ARSceneView
        arSceneView = ArSceneView(context).apply {
            setupSession(arSession)
        }

        addView(arSceneView)

        // Update loop
        arSceneView.scene.addOnUpdateListener {
            if (didUserTap && !placementIsDone) {
                val frame = arSession?.update()
                frame?.let { onDrawFrame(it) }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP && !placementIsDone) {
            tapX = event.x
            tapY = event.y
            didUserTap = true
        }
        return true
    }

    private fun onDrawFrame(frame: Frame) {
        // Perform hit test and place object
        val hits = frame.hitTest(tapX, tapY)
        for (hit in hits) {
            val trackable = hit.trackable
            if (trackable is InstantPlacementPoint && !placementIsDone) {
                val anchor = trackable.createAnchor(trackable.pose)
                // Here you would attach your AR object to the anchor
                placementIsDone = true
                didUserTap = false // Reset tap detection
                // To disable Instant Placement, adjust your session configuration as needed
                break
            }
        }
    }



    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        arSceneView.resume()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        arSceneView.pause()
    }

    fun onDestroy() {
        arSession?.close()
        arSceneView.destroy()
    }
}
