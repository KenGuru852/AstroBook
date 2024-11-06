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
import kotlin.random.Random

class BlackHole(context: Context) {

    private val vertexShaderCode = """
        attribute vec4 vPosition;
        uniform mat4 uMVPMatrix;
        varying vec3 vColor;

        void main() {
            gl_Position = uMVPMatrix * vPosition;
            vColor = vec3(0.0, 0.0, 0.0); // Черный цвет
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

    private var positionX: Float = 0f
    private var positionY: Float = 0f
    private var positionZ: Float = -20f

    private val screenBounds = floatArrayOf(-14f, 16f, -16f, 15f) // Границы экрана

    private var angle: Float = 0f
    private val orbitRadiusX: Float = 15f // Большая полуось эллипса по X
    private val orbitRadiusY: Float = 15f  // Малая полуось эллипса по Y
    private val orbitRadiusZ: Float = 3f   // Радиус по оси Z

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
        vertices.add(0.0f) // Центр круга
        vertices.add(0.0f)
        vertices.add(0.0f)

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
        for (i in 1..segments) {
            indices.add(0)
            indices.add(i.toShort())
            indices.add((i + 1).toShort())
        }
        indices.add(0)
        indices.add(segments.toShort())
        indices.add(1)

        return indices.toShortArray()
    }

    fun updatePosition() {
        angle += 0.002f // Увеличиваем угол для эллиптической траектории

        // Обновляем позицию по эллиптической траектории
        positionX = orbitRadiusX * cos(angle)
        positionY = orbitRadiusY * sin(angle)
        positionZ = orbitRadiusZ * sin(angle * 2) // Добавляем компоненту по оси Z


    }

    fun draw(mvpMatrix: FloatArray) {
        GLES20.glUseProgram(program)

        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        val mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")

        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer)

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        GLES20.glDrawElements(GLES20.GL_TRIANGLE_FAN, indices.size, GLES20.GL_UNSIGNED_SHORT, indexBuffer)

        GLES20.glDisableVertexAttribArray(positionHandle)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        return GLES20.glCreateShader(type).also { shader ->
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
        }
    }

    fun getPositionX(): Float {
        return positionX
    }

    fun getPositionY(): Float {
        return positionY
    }

    fun getPositionZ(): Float {
        return positionZ
    }

    fun setPositionX(x: Float) {
        positionX = x
    }

    fun setPositionY(y: Float) {
        positionY = y
    }

    fun setPositionZ(z: Float) {
        positionZ = z
    }
}