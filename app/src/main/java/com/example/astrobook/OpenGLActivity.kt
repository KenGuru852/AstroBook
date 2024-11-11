package com.example.astrobook

import android.app.Activity
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout

class OpenGLActivity : Activity() {

    private lateinit var glSurfaceView: GLSurfaceView
    private lateinit var renderer: OpenGLRenderer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Создаем GLSurfaceView
        glSurfaceView = GLSurfaceView(this)
        glSurfaceView.setEGLContextClientVersion(2)
        renderer = OpenGLRenderer(this)
        glSurfaceView.setRenderer(renderer)

        // Создаем контейнер для GLSurfaceView и кнопок
        val layout = FrameLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Добавляем GLSurfaceView в контейнер
        layout.addView(glSurfaceView)

        // Создаем кнопки
        val buttonLeft = Button(this).apply {
            text = "Назад"
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.BOTTOM or Gravity.START
                setMargins(16, 0, 0, 16)
            }
            setOnClickListener {
                renderer.selectPreviousPlanet()
            }
            setBackgroundResource(R.drawable.button_background)
            setTextColor(resources.getColor(android.R.color.white))
            textSize = 16f
        }

        val buttonRight = Button(this).apply {
            text = "Вперёд"
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.BOTTOM or Gravity.END
                setMargins(0, 0, 16, 16)
            }
            setOnClickListener {
                renderer.selectNextPlanet()
            }
            setBackgroundResource(R.drawable.button_background)
            setTextColor(resources.getColor(android.R.color.white))
            textSize = 16f
        }

        val buttonInfo = Button(this).apply {
            text = "Информация"
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                setMargins(0, 0, 0, 16)
            }
            setOnClickListener {
                renderer.showPlanetInfo()
            }
            setBackgroundResource(R.drawable.button_background)
            setTextColor(resources.getColor(android.R.color.white))
            textSize = 12f
        }

        // Добавляем кнопки в контейнер
        layout.addView(buttonLeft)
        layout.addView(buttonRight)
        layout.addView(buttonInfo)

        // Устанавливаем контейнер как основной контент
        setContentView(layout)
    }
}