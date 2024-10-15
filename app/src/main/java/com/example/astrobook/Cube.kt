package com.example.astrobook

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class Cube(context: Context) {

    private val vertexShaderCode = """
        uniform mat4 uMVPMatrix;
        attribute vec4 vPosition;
        attribute vec2 aTextureCoord;
        varying vec2 vTextureCoord;
        void main() {
            gl_Position = uMVPMatrix * vPosition;
            vTextureCoord = aTextureCoord;
        }
    """

    private val fragmentShaderCode = """
        precision mediump float;
        varying vec2 vTextureCoord;
        uniform sampler2D uTexture;
        void main() {
            gl_FragColor = texture2D(uTexture, vTextureCoord);
        }
    """

    private val vertexBuffer: FloatBuffer
    private val textureBuffer: FloatBuffer
    private val indexBuffer: ShortBuffer
    private val textureId: Int

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

    private val textureCoords = floatArrayOf(
        0.0f, 0.0f,  // верхний левый перед
        0.0f, 1.0f,  // нижний левый перед
        1.0f, 1.0f,  // нижний правый перед
        1.0f, 0.0f,  // верхний правый перед
        0.0f, 0.0f,  // верхний левый зад
        0.0f, 1.0f,  // нижний левый зад
        1.0f, 1.0f,  // нижний правый зад
        1.0f, 0.0f   // верхний правый зад
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

        textureBuffer = ByteBuffer.allocateDirect(textureCoords.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(textureCoords)
        textureBuffer.position(0)

        indexBuffer = ByteBuffer.allocateDirect(indices.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
            .put(indices)
        indexBuffer.position(0)

        val textures = IntArray(1)
        GLES20.glGenTextures(1, textures, 0)
        textureId = textures[0]

        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.uno)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)

        vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)
    }

    fun draw(mvpMatrix: FloatArray) {
        GLES20.glUseProgram(program)

        val positionHandle = GLES20.glGetAttribLocation(program, "vPosition")
        GLES20.glEnableVertexAttribArray(positionHandle)
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        val textureHandle = GLES20.glGetAttribLocation(program, "aTextureCoord")
        GLES20.glEnableVertexAttribArray(textureHandle)
        GLES20.glVertexAttribPointer(textureHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer)

        val mvpMatrixHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)

        val textureUniformHandle = GLES20.glGetUniformLocation(program, "uTexture")
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
        GLES20.glUniform1i(textureUniformHandle, 0)

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.size, GLES20.GL_UNSIGNED_SHORT, indexBuffer)

        GLES20.glDisableVertexAttribArray(positionHandle)
        GLES20.glDisableVertexAttribArray(textureHandle)
    }

    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }
}