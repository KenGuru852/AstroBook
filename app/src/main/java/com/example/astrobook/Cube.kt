package com.example.astrobook

import android.content.Context
import android.opengl.GLES20
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class Cube(context: Context) {

    private val vertexShaderCode = """
        uniform mat4 uMVPMatrix;
        attribute vec4 vPosition;
        void main() {
            gl_Position = uMVPMatrix * vPosition;
        }
    """

    private val fragmentShaderCode = """
        precision mediump float;
        uniform vec4 uColor;
        void main() {
            gl_FragColor = uColor;
        }
    """

    private val vertexBuffer: FloatBuffer
    private val indexBuffer: ShortBuffer

    private val vertexCoords = floatArrayOf(
        -1.0f,  1.0f, -1.0f,  // верхний левый перед
        -1.0f, -1.0f, -1.0f,  // нижний левый перед
        1.0f, -1.0f, -1.0f,  // нижний правый перед
        1.0f,  1.0f, -1.0f,  // верхний правый перед
        -1.0f,  1.0f,  1.0f,  // верхний левый зад
        -1.0f, -1.0f,  1.0f,  // нижний левый зад
        1.0f, -1.0f,  1.0f,  // нижний правый зад
        1.0f,  1.0f,  1.0f   // верхний правый зад
    )

    private val indices = shortArrayOf(
        0, 1, 2, 0, 2, 3,  // передняя грань
        4, 5, 6, 4, 6, 7,  // задняя грань
        0, 1, 5, 0, 5, 4,  // левая грань
        2, 3, 7, 2, 7, 6,  // правая грань
        0, 3, 7, 0, 7, 4,  // верхняя грань
        1, 2, 6, 1, 6, 5   // нижняя грань
    )

    private val vertexShader: Int
    private val fragmentShader: Int
    private val program: Int

    init {
        vertexBuffer = ByteBuffer.allocateDirect(vertexCoords.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(vertexCoords)
        vertexBuffer.position(0)

        indexBuffer = ByteBuffer.allocateDirect(indices.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
            .put(indices)
        indexBuffer.position(0)

        vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
    }

    fun draw(mvpMatrix: FloatArray) {
        GLES20.glUseProgram(program)

        // Включаем смешивание цветов
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        val mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        val colorHandle = GLES20.glGetUniformLocation(program, "uColor")
        GLES20.glUniform4f(colorHandle, 1.0f, 1.0f, 1.0f, 0.5f) // Белый цвет с прозрачностью 0.5

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.size, GLES20.GL_UNSIGNED_SHORT, indexBuffer)

        GLES20.glDisableVertexAttribArray(positionHandle)

        // Отключаем смешивание цветов
        GLES20.glDisable(GLES20.GL_BLEND)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }
}