package com.example.astrobook

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MoonRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private lateinit var moon: TexturedSphere

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    private val mvMatrix = FloatArray(16)
    private val lightPos = floatArrayOf(0f, 0f, 10f) // Позиция источника света
    private lateinit var lightSphere: TexturedSphere

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_LEQUAL)

        // Инициализация Луны
        moon = TexturedSphere(context, 0.3f, 20, 10)
        moon.loadTexture(R.drawable.moon)

        // Инициализация лампы
        //lightSphere = TexturedSphere(context, 0.5f, 20, 10)
        //lightSphere.loadTexture(R.drawable.lamp) // Загрузка текстуры для лампы
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 1f)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)


        // Отрисовка Луны с освещением по модели Фонга
        moon.draw(mvpMatrix)

        // Отрисовка лампы
        //val lightMvpMatrix = FloatArray(16)
        //Matrix.setIdentityM(lightMvpMatrix, 0)
        //Matrix.translateM(lightMvpMatrix, 0, lightPos[0], lightPos[1], lightPos[2])
        //Matrix.multiplyMM(lightMvpMatrix, 0, mvpMatrix, 0, lightMvpMatrix, 0)
        //lightSphere.draw(lightMvpMatrix)

    }
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height.toFloat()
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 1f, 50f)
    }
}