package com.example.astrobook

import android.app.Activity
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class PlanetInfoActivity : Activity() {

    private lateinit var glSurfaceView: GLSurfaceView
    private lateinit var renderer: PlanetRenderer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planet_info)

        val planetName = intent.getStringExtra("PLANET_NAME")
        val planetInfo = intent.getStringExtra("PLANET_INFO")

        findViewById<TextView>(R.id.planetNameTextView).text = planetName
        findViewById<TextView>(R.id.planetInfoTextView).text = planetInfo

        glSurfaceView = findViewById(R.id.planetGLSurfaceView)
        glSurfaceView.setEGLContextClientVersion(2)
        renderer = PlanetRenderer(this, planetName ?: "Неизвестная планета")
        glSurfaceView.setRenderer(renderer)

        findViewById<Button>(R.id.backButton).setOnClickListener {
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }
}