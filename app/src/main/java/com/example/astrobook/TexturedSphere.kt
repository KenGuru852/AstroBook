package com.example.astrobook

import android.content.Context
import android.opengl.GLES20
import android.graphics.BitmapFactory
import android.opengl.GLUtils
import android.util.Log
import com.example.astrobook.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.*

class TexturedSphere(
    private val context: Context,
    private val radius: Float,
    private val segments: Int,
    private val rings: Int
) {
    // Новое свойство для хранения угла вращения
    var angle: Float = 0f

    private val vertexShaderCode = """
        attribute vec4 vPosition;
        attribute vec2 aTexCoord;
        varying vec2 vTexCoord;
        uniform mat4 uMVPMatrix;

        void main() {
            gl_Position = uMVPMatrix * vPosition;
            vTexCoord = aTexCoord;
        }
    """.trimIndent()

    private val fragmentShaderCode = """
        precision mediump float;
        varying vec2 vTexCoord;
        uniform sampler2D uTexture;

        void main() {
            gl_FragColor = texture2D(uTexture, vTexCoord);
        }
    """.trimIndent()

    private var program: Int

    init {
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = GLES20.glCreateProgram().also {
            GLES20.glAttachShader(it, vertexShader)
            GLES20.glAttachShader(it, fragmentShader)
            GLES20.glLinkProgram(it)

            val linkStatus = IntArray(1)
            GLES20.glGetProgramiv(it, GLES20.GL_LINK_STATUS, linkStatus, 0)
            if (linkStatus[0] == 0) {
                Log.e("OpenGL", "Program linking failed: ${GLES20.glGetProgramInfoLog(it)}")
                GLES20.glDeleteProgram(it)
            }
        }
    }

    private val vertexBuffer: FloatBuffer
    private val indexBuffer: ByteBuffer
    private val textureBuffer: FloatBuffer
    private val textureHandle = IntArray(1)
    private val indices: ShortArray

    init {
        val vertices = generateSphereVertices(radius, segments, rings)
        val texCoords = generateSphereTexCoords(segments, rings)

        val bb = ByteBuffer.allocateDirect(vertices.size * 4)
        bb.order(ByteOrder.nativeOrder())
        vertexBuffer = bb.asFloatBuffer().apply {
            put(vertices)
            position(0)
        }

        indices = generateSphereIndices(segments, rings)
        indexBuffer = ByteBuffer.allocateDirect(indices.size * 2)
        indexBuffer.order(ByteOrder.nativeOrder())
        indexBuffer.asShortBuffer().apply {
            put(indices)
            position(0)
        }

        val tb = ByteBuffer.allocateDirect(texCoords.size * 4)
        tb.order(ByteOrder.nativeOrder())
        textureBuffer = tb.asFloatBuffer().apply {
            put(texCoords)
            position(0)
        }

        loadTexture(R.drawable.ksun)
    }

    private fun generateSphereIndices(segments: Int, rings: Int): ShortArray {
        val indices = mutableListOf<Short>()
        for (i in 0 until rings) {
            for (j in 0 until segments) {
                val first = (i * (segments + 1) + j).toShort()
                val second = (first + segments + 1).toShort()

                indices.add(first)
                indices.add(second)
                indices.add((first + 1).toShort())

                indices.add(second)
                indices.add((second + 1).toShort())
                indices.add((first + 1).toShort())
            }
        }
        return indices.toShortArray()
    }

    public fun loadTexture(resourceId: Int) {
        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId)
        GLES20.glGenTextures(1, textureHandle, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()

        val error = GLES20.glGetError()
        if (error != GLES20.GL_NO_ERROR) {
            Log.e("OpenGL", "Error loading texture: $error")
        }
    }

    fun draw(mvpMatrix: FloatArray) {
        GLES20.glUseProgram(program)

        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        val texCoordHandle = GLES20.glGetAttribLocation(program, "aTexCoord")
        val mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        val textureUniformHandle = GLES20.glGetUniformLocation(program, "uTexture")

        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer)

        GLES20.glEnableVertexAttribArray(texCoordHandle)
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 8, textureBuffer)

        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])
        GLES20.glUniform1i(textureUniformHandle, 0)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.size, GLES20.GL_UNSIGNED_SHORT, indexBuffer)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(texCoordHandle)
    }

    private fun generateSphereVertices(radius: Float, segments: Int, rings: Int): FloatArray {
        val vertices = mutableListOf<Float>()
        for (i in 0..rings) {
            val theta = i * Math.PI / rings
            val sinTheta = sin(theta).toFloat()
            val cosTheta = cos(theta).toFloat()

            for (j in 0..segments) {
                val phi = j * 2 * Math.PI / segments
                val sinPhi = sin(phi).toFloat()
                val cosPhi = cos(phi).toFloat()

                val x = cosPhi * sinTheta
                val y = cosTheta
                val z = sinPhi * sinTheta

                vertices.add(x * radius)
                vertices.add(y * radius)
                vertices.add(z * radius)
            }
        }
        return vertices.toFloatArray()
    }

    private fun generateSphereTexCoords(segments: Int, rings: Int): FloatArray {
        val texCoords = mutableListOf<Float>()
        for (i in 0..rings) {
            for (j in 0..segments) {
                val u = j / segments.toFloat()
                val v = i / rings.toFloat()
                texCoords.add(u)
                texCoords.add(v)
            }
        }
        return texCoords.toFloatArray()
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)

        val compileStatus = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)
        if (compileStatus[0] == 0) {
            Log.e("OpenGL", "Shader compilation failed: ${GLES20.glGetShaderInfoLog(shader)}")
            GLES20.glDeleteShader(shader)
        }

        return shader
    }
}