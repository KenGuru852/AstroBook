package com.example.astrobook

import android.app.Activity
import android.opengl.GLSurfaceView
import android.os.Bundle


class OpenGLActivity : Activity() {

    private lateinit var glSurfaceView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Создаем GLSurfaceView
        glSurfaceView = GLSurfaceView(this)
        glSurfaceView.setEGLContextClientVersion(2)
        glSurfaceView.setRenderer(OpenGLRenderer(this))

        setContentView(glSurfaceView)
    }

}