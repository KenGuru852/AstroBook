package com.example.astrobook

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class OpenGLRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private lateinit var square: Square
    private lateinit var sunCircle: TexturedSphere

    // Объекты планет
    private lateinit var mars: TexturedSphere
    private lateinit var saturn: TexturedSphere
    private lateinit var jupiter: TexturedSphere
    private lateinit var neptune: TexturedSphere
    private lateinit var uranus: TexturedSphere
    private lateinit var mercury: TexturedSphere
    private lateinit var venus: TexturedSphere
    private lateinit var earth: TexturedSphere
    private lateinit var moon: TexturedSphere

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)
    private val tempMatrix = FloatArray(16) // Для промежуточных расчетов

    private var angleX: Float = 0f
    private var sunRotationAngle: Float = 0f // Угол вращения Солнца

    private val planetRotations = floatArrayOf(1.0f, 0.9f, 0.8f, 0.5f, 0.4f, 0.3f, 0.2f, 0.1f) // Скорости вращения планет
    private val planetDistances = floatArrayOf(
        3.5f,   // Меркурий - самая близкая
        4.5f,   // Венера
        5.5f,   // Земля
        6.5f,   // Марс
        8.5f,   // Юпитер
        10.5f,  // Сатурн
        12.5f,  // Уран
        14.5f   // Нептун - самая дальняя
    )

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_LEQUAL)

        // Инициализация фона и объектов
        square = Square(context)
        sunCircle = TexturedSphere(context, 1.0f, 80, 40)

        // Инициализация планет с радиусами и текстурами
        mars = TexturedSphere(context, 0.5f, 40, 20)
        mars.loadTexture(R.drawable.kmars)

        saturn = TexturedSphere(context, 0.8f, 60, 30)
        saturn.loadTexture(R.drawable._ksaturn)

        jupiter = TexturedSphere(context, 1.2f, 80, 40)
        jupiter.loadTexture(R.drawable.kjupiter)

        neptune = TexturedSphere(context, 0.6f, 60, 30)
        neptune.loadTexture(R.drawable._kneptune)

        uranus = TexturedSphere(context, 0.7f, 60, 30)
        uranus.loadTexture(R.drawable.kuranus)

        // Инициализация новых планет
        mercury = TexturedSphere(context, 0.3f, 30, 15)
        mercury.loadTexture(R.drawable._k_mercury)

        venus = TexturedSphere(context, 0.4f, 35, 18)
        venus.loadTexture(R.drawable._k_venus_surface)

        earth = TexturedSphere(context, 0.45f, 40, 20)
        earth.loadTexture(R.drawable.earth)

        // Инициализация Луны
        moon = TexturedSphere(context, 0.1f, 20, 10)  // Луна маленькая
        moon.loadTexture(R.drawable.moon)  // Замените на свою текстуру Луны
    }

    override fun onDrawFrame(gl: GL10?) {
        // Очистка буфера кадра и глубинного буфера
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // Настройка камеры
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 10f, 0f, 0f, 0f, 0f, 1f, 0f)

        // Увеличение угла вращения
        angleX += 1f
        sunRotationAngle += 0.1f // Увеличение угла вращения Солнца

        // Модельно-видовая-пространственная матрица
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        square.draw(projectionMatrix, viewMatrix)

        drawPlanet(mercury, planetRotations[0], planetDistances[0], angleX, -4.5f, 1.5f)
        drawPlanet(venus, -planetRotations[1], planetDistances[1], angleX, -5.0f, 1.5f)

        // Матрица Земли
        val earthMatrix = FloatArray(16)
        Matrix.setIdentityM(earthMatrix, 0)

        // Земля вращается вокруг своей оси и находится на расстоянии от Солнца
        Matrix.translateM(earthMatrix, 0, 0f, 0f, -5.5f)
        Matrix.rotateM(earthMatrix, 0, angleX * planetRotations[2], 0f, 1f, 0f)
        Matrix.translateM(earthMatrix, 0, planetDistances[2], 0f, 0f)

        // Применение проекционной и видовой матриц для Земли
        val earthFinalMatrix = FloatArray(16)
        Matrix.multiplyMM(earthFinalMatrix, 0, viewMatrix, 0, earthMatrix, 0)
        Matrix.multiplyMM(earthFinalMatrix, 0, projectionMatrix, 0, earthFinalMatrix, 0)

        // Отрисовка Земли
        drawPlanet(earth, planetRotations[2], planetDistances[2], angleX, -5.5f, 1.5f)
        // Отрисовка Луны
        drawMoonPerpendicularToEcliptic(earthMatrix, angleX)

        // Остальные планеты
        drawPlanet(mars, planetRotations[3], planetDistances[3], angleX, -6.0f, 2.5f)
        drawPlanet(saturn, planetRotations[4], planetDistances[4], angleX, -6.5f, 2.5f)
        drawPlanet(jupiter, planetRotations[5], planetDistances[5], angleX, -7.0f, 2.5f)
        drawPlanet(neptune, planetRotations[6], planetDistances[6], angleX, -7.5f, 2.5f)
        drawPlanet(uranus, -planetRotations[7], planetDistances[7], angleX, -8.0f, 2.5f)

        // Отрисовка Солнца
        val sunMatrix = FloatArray(16)
        Matrix.setIdentityM(sunMatrix, 0)
        Matrix.translateM(sunMatrix, 0, 0f, 0f, -5.0f) // Солнце ближе к камере
        Matrix.rotateM(sunMatrix, 0, sunRotationAngle, 0f, 1f, 0f) // Вращение Солнца вокруг своей оси
        Matrix.scaleM(sunMatrix, 0, 3.0f, 3.0f, 3.0f) // Увеличение размера Солнца
        Matrix.multiplyMM(sunMatrix, 0, viewMatrix, 0, sunMatrix, 0)
        Matrix.multiplyMM(sunMatrix, 0, projectionMatrix, 0, sunMatrix, 0)
        sunCircle.draw(sunMatrix)
    }

    private fun drawMoonPerpendicularToEcliptic(earthMatrix: FloatArray, angle: Float) {
        // Матрица для Луны
        val moonMatrix = FloatArray(16)
        Matrix.setIdentityM(moonMatrix, 0)

        // Вращение Луны перпендикулярно плоскости эклиптики (вокруг оси X, вместо оси Y)
        Matrix.rotateM(moonMatrix, 0, angle * 2f, 1f, 0f, 0f)  // Вращение по оси X

        // Перемещение Луны на орбиту (расстояние от Земли)
        Matrix.translateM(moonMatrix, 0, 0f, 0f, 1.5f)  // Луна на расстоянии 1.5f от Земли по оси Z

        // Умножение на матрицу Земли (чтобы Луна вращалась вокруг Земли)
        Matrix.multiplyMM(moonMatrix, 0, earthMatrix, 0, moonMatrix, 0)

        // Собственное вращение Луны вокруг её оси
        Matrix.rotateM(moonMatrix, 0, angle, 0f, 1f, 0f)

        // Умножение на видовую и проекционную матрицы
        Matrix.multiplyMM(moonMatrix, 0, viewMatrix, 0, moonMatrix, 0)
        Matrix.multiplyMM(moonMatrix, 0, projectionMatrix, 0, moonMatrix, 0)

        Matrix.scaleM(moonMatrix, 0, 1.5f, 1.5f, 1.5f) // Увеличение Луны

        // Отрисовка Луны
        moon.draw(moonMatrix)
    }

    private fun drawPlanet(planet: TexturedSphere, rotationSpeed: Float, distanceFromSun: Float, angle: Float, depthZ: Float, scale: Float) {
        // Матрица для каждой планеты
        val planetMatrix = FloatArray(16)
        Matrix.setIdentityM(planetMatrix, 0)

        // Перемещение на расстояние от Солнца
        Matrix.translateM(planetMatrix, 0, 0f, 0f, depthZ) // Разные глубины для планет

        // Вращение планеты вокруг Солнца
        Matrix.rotateM(planetMatrix, 0, angle * rotationSpeed, 0f, 1f, 0f)

        // Перемещение планеты на нужное расстояние от Солнца
        Matrix.translateM(planetMatrix, 0, distanceFromSun, 0f, 0f)

        // Масштабирование планеты (увеличение размера)
        Matrix.scaleM(planetMatrix, 0, scale, scale, scale)

        // Умножение на видовую матрицу
        Matrix.multiplyMM(planetMatrix, 0, viewMatrix, 0, planetMatrix, 0)

        // Умножение на проекционную матрицу
        Matrix.multiplyMM(planetMatrix, 0, projectionMatrix, 0, planetMatrix, 0)

        // Отрисовка планеты
        planet.draw(planetMatrix)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height.toFloat()

        // Проекционная матрица
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, 1f, 50f)
    }
}