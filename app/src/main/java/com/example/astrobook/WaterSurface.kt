package com.example.astrobook

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.cos
import kotlin.math.sin

class WaterSurface(context: Context) {

    private val vertexShaderCode = context.resources.openRawResource(R.raw.vertex_shader).bufferedReader().use { it.readText() }
    private val fragmentShaderCode = context.resources.openRawResource(R.raw.fragment_shader).bufferedReader().use { it.readText() }

    private val vertexBuffer: FloatBuffer
    private val textureBuffer: FloatBuffer
    private val mProgram: Int
    private var mPositionHandle: Int = 0
    private var mMVPMatrixHandle: Int = 0
    private var mTimeHandle: Int = 0
    private var mTextureHandle: Int = 0
    private var mTextureCoordHandle: Int = 0
    private var textureId: Int = 0

    init {
        val vertices = createVertices()
        val textureCoords = createTextureCoords()

        vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(vertices)
                position(0)
            }
        }

        textureBuffer = ByteBuffer.allocateDirect(textureCoords.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(textureCoords)
                position(0)
            }
        }

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        mProgram = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }

        // Загрузка текстуры
        textureId = loadTexture(context, R.drawable.water)
    }

    private fun createVertices(): FloatArray {
        val segments = 64 // Количество сегментов для круга
        val vertices = mutableListOf<Float>()

        vertices.add(0f) // Центр changing the Neptune Image (it was rectangular now round), changing the background in the OpenGL View of Neptuneкруга
        vertices.add(0f)
        vertices.add(0f)

        for (i in 0..segments) {
            val angle = 2.0 * Math.PI * i / segments
            val x = cos(angle).toFloat()
            val y = sin(angle).toFloat()
            vertices.add(x)
            vertices.add(y)
            vertices.add(0f)
        }

        return vertices.toFloatArray()
    }

    private fun createTextureCoords(): FloatArray {
        val segments = 64 // Количество сегментов для круга
        val textureCoords = mutableListOf<Float>()

        textureCoords.add(0.5f) // Центр круга
        textureCoords.add(0.5f)

        for (i in 0..segments) {
            val angle = 2.0 * Math.PI * i / segments
            val u = 0.5f + 0.5f * cos(angle).toFloat()
            val v = 0.5f + 0.5f * sin(angle).toFloat()
            textureCoords.add(u)
            textureCoords.add(v)
        }

        return textureCoords.toFloatArray()
    }

    fun draw(mvpMatrix: FloatArray, time: Float) {
        GLES20.glUseProgram(mProgram)

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition").also {
            GLES20.glEnableVertexAttribArray(it)
            GLES20.glVertexAttribPointer(it, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer)
        }

        mTextureCoordHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord").also {
            GLES20.glEnableVertexAttribArray(it)
            GLES20.glVertexAttribPointer(it, 2, GLES20.GL_FLOAT, false, 8, textureBuffer)
        }

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix").also {
            GLES20.glUniformMatrix4fv(it, 1, false, mvpMatrix, 0)
        }

        mTimeHandle = GLES20.glGetUniformLocation(mProgram, "uTime").also {
            GLES20.glUniform1f(it, time)
        }

        mTextureHandle = GLES20.glGetUniformLocation(mProgram, "uTexture").also {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
            GLES20.glUniform1i(it, 0)
        }

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 66) // 1 центральная вершина + 64 сегмента + 1 дополнительная вершина для замыкания

        GLES20.glDisableVertexAttribArray(mPositionHandle)
        GLES20.glDisableVertexAttribArray(mTextureCoordHandle)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }

    private fun loadTexture(context: Context, resourceId: Int): Int {
        val textureHandle = IntArray(1)
        GLES20.glGenTextures(1, textureHandle, 0)

        if (textureHandle[0] != 0) {
            val options = android.graphics.BitmapFactory.Options().apply {
                inScaled = false
            }

            val bitmap = android.graphics.BitmapFactory.decodeResource(context.resources, resourceId, options)

            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])

            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

            bitmap.recycle()
        }

        if (textureHandle[0] == 0) {
            throw RuntimeException("Error loading texture.")
        }

        return textureHandle[0]
    }
}