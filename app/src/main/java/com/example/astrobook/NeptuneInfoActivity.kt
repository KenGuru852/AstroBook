package com.example.astrobook

import android.app.Activity
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginTop

class NeptuneInfoActivity : Activity() {

    private lateinit var glSurfaceView: GLSurfaceView
    private lateinit var waterSurfaceRenderer: WaterSurfaceRenderer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Создаем LinearLayout
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setPadding(16, 16, 16, 16)
            setBackgroundColor(ContextCompat.getColor(this@NeptuneInfoActivity, R.color.space_background))
        }

        // Создаем GLSurfaceView для анимации водной глади
        glSurfaceView = GLSurfaceView(this)
        glSurfaceView.setEGLContextClientVersion(2)
        waterSurfaceRenderer = WaterSurfaceRenderer(this)
        glSurfaceView.setRenderer(waterSurfaceRenderer)

        // Устанавливаем параметры для GLSurfaceView
        val glSurfaceViewParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        glSurfaceViewParams.weight = 0.5f // Это позволяет GLSurfaceView занимать 50% высоты экрана
        glSurfaceView.layoutParams = glSurfaceViewParams

        // Создаем TextView для описания Нептуна
        val neptuneDescription = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setPadding(0, 0, 0, 300) // Увеличиваем верхний отступ на 15px
            textSize = 18f
            text = getString(R.string.neptune_description)
            setTextColor(ContextCompat.getColor(this@NeptuneInfoActivity, R.color.space_text))

        }

        // Добавляем GLSurfaceView и TextView в LinearLayout
        layout.addView(glSurfaceView)
        layout.addView(neptuneDescription)

        // Устанавливаем LinearLayout как основной контент
        setContentView(layout)
    }
}