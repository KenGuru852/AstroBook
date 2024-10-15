package com.example.astrobook

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class OpenGLRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private lateinit var square: Square
    private lateinit var cube: Cube

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val rotationMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    private var angle: Float = 0f

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

        // Инициализация объектов
        square = Square(context)
        cube = Cube(context)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        // Настройка матрицы проекции
        Matrix.perspectiveM(projectionMatrix, 0, 45f, width.toFloat() / height.toFloat(), 0.1f, 100f)
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // Отрисовка квадрата как фонового изображения
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -5f)
        Matrix.scaleM(modelMatrix, 0, 10f, 10f, 1f)
        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0)
        square.draw(mvpMatrix)

        // Обновление угла вращения
        angle += 1f

        // Отрисовка куба по центру экрана с вращением вокруг своей оси по Z
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, 0f, 0f, -2f)
        Matrix.setRotateM(rotationMatrix, 0, angle, 0f, 0f, 1f) // Вращение вокруг оси Z
        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, rotationMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvpMatrix, 0)
        cube.draw(mvpMatrix)
    }
}