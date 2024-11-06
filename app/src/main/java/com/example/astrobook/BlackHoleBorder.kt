package com.example.astrobook

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import kotlin.math.cos
import kotlin.math.sin

class BlackHoleBorder(context: Context) {

    private val vertexShaderCode = """
        attribute vec4 vPosition;
        uniform mat4 uMVPMatrix;
        varying vec3 vColor;

        void main() {
            gl_Position = uMVPMatrix * vPosition;
            vColor = vec3(1.0, 1.0, 1.0); // Белый цвет для границы
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float;
        varying vec3 vColor;

        void main() {
            gl_FragColor = vec4(vColor, 1.0);
        }
    """.trimIndent()

    private val program: Int
    private val vertexBuffer: FloatBuffer
    private val indexBuffer: ShortBuffer
    private val vertices: FloatArray
    private val indices: ShortArray

    init {
        val segments = 32 // Количество сегментов для круга
        vertices = generateCircleVertices(segments)
        indices = generateCircleIndices(segments)

        val bb = ByteBuffer.allocateDirect(vertices.size * 4)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer().apply {
            put(vertices)
            position(0)
        }

        val ib = ByteBuffer.allocateDirect(indices.size * 2)
        ib.order(ByteOrder.nativeOrder())
        indexBuffer = ib.asShortBuffer().apply {
            put(indices)
            position(0)
        }

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)
        }
    }

    private fun generateCircleVertices(segments: Int): FloatArray {
        val vertices = mutableListOf<Float>()
        for (i in 0..segments) {
            val angle = 2.0 * Math.PI * i / segments
            vertices.add(cos(angle).toFloat())
            vertices.add(sin(angle).toFloat())
            vertices.add(0.0f)
        }

        return vertices.toFloatArray()
    }

    private fun generateCircleIndices(segments: Int): ShortArray {
        val indices = mutableListOf<Short>()
        for (i in 0 until segments) {
            indices.add(i.toShort())
            indices.add((i + 1).toShort())
        }
        indices.add(segments.toShort())
        indices.add(0)

        return indices.toShortArray()
    }

    fun draw(mvpMatrix: FloatArray) {
        GLES20.glUseProgram(program)

        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        val mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")

        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer)

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        GLES20.glDrawElements(GLES20.GL_LINE_LOOP, indices.size, GLES20.GL_UNSIGNED_SHORT, indexBuffer)

        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }
}