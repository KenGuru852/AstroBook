package com.example.astrobook

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class WaterSurfaceRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private lateinit var waterSurface: WaterSurface

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    private var time: Float = 0f

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // Устанавливаем цвет очистки буфера цвета
        GLES20.glClearColor(0.0745f, 0.0392f, 0.2706f, 1f) // Установите нужный цвет фона
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_LEQUAL)

        // Инициализация водной поверхности
        waterSurface = WaterSurface(context)
    }

    override fun onDrawFrame(gl: GL10?) {
        // Очищаем буфер цвета и буфер глубины
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 1.7f, 0f, 0f, 0f, 0f, 2f, 0f)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        // Анимация водной поверхности
        time += 0.03f
        waterSurface.draw(mvpMatrix, time)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height.toFloat()
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 1f, 50f)
    }
}