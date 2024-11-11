package com.example.astrobook

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class PlanetRenderer(private val context: Context, private val planetName: String) : GLSurfaceView.Renderer {

    private lateinit var planet: TexturedSphere
    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    private var angle: Float = 0f

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_LEQUAL)

        // Инициализация планеты
        planet = when (planetName) {
            "Меркурий" -> TexturedSphere(context, 0.3f, 30, 15).apply { loadTexture(R.drawable._k_mercury) }
            "Венера" -> TexturedSphere(context, 0.4f, 35, 18).apply { loadTexture(R.drawable._k_venus_surface) }
            "Земля" -> TexturedSphere(context, 0.45f, 40, 20).apply { loadTexture(R.drawable.earth) }
            "Марс" -> TexturedSphere(context, 0.5f, 40, 20).apply { loadTexture(R.drawable.kmars) }
            "Юпитер" -> TexturedSphere(context, 1.2f, 80, 40).apply { loadTexture(R.drawable.kjupiter) }
            "Сатурн" -> TexturedSphere(context, 0.8f, 60, 30).apply { loadTexture(R.drawable._ksaturn) }
            "Уран" -> TexturedSphere(context, 0.7f, 60, 30).apply { loadTexture(R.drawable.kuranus) }
            "Нептун" -> TexturedSphere(context, 0.6f, 60, 30).apply { loadTexture(R.drawable._kneptune) }
            "Луна" -> TexturedSphere(context, 0.1f, 20, 10).apply { loadTexture(R.drawable.moon) }
            else -> TexturedSphere(context, 0.3f, 30, 15).apply { loadTexture(R.drawable._k_mercury) }
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        val neptuneScale = 0.6f
        Matrix.scaleM(mvpMatrix, 0, neptuneScale, neptuneScale, neptuneScale)

        planet.draw(mvpMatrix)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height.toFloat()
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 1f, 50f)
    }
}