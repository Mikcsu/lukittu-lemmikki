package com.example.lukittu_lemmikki

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.core.exceptions.UnavailableException
import com.google.ar.sceneform.ArSceneView

class CustomArView : FrameLayout {
    private var arSession: Session? = null
    private lateinit var arSceneView: ArSceneView

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
                arSession = Session(context)
            } catch (e: UnavailableException) {
                // Handle exception
            }

            arSession?.let {
                it.configure(Config(it).apply {
                    updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
                    depthMode = Config.DepthMode.AUTOMATIC
                })
            }
        }

        // Initialize ARSceneView
        arSceneView = ArSceneView(context).apply {
            setupSession(arSession)
        }

        addView(arSceneView)
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
