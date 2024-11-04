package com.example.astrobook

import android.content.Context
import android.content.Intent
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.widget.Toast
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
        3.2f,   // Меркурий - самая близкая
        3.7f,   // Венера
        4.4f,   // Земля
        5.3f,   // Марс
        6.7f,   // Юпитер
        9.5f,   // Сатурн
        11.0f,  // Уран
        12.5f   // Нептун - самая дальняя
    )

    private val planetScales = floatArrayOf(
        0.5f,   // Меркурий
        0.6f,   // Венера
        0.65f,  // Земля
        0.7f,   // Марс
        1.1f,   // Юпитер
        1.0f,   // Сатурн
        0.8f,   // Уран
        0.9f    // Нептун
    )

    private var selectedPlanetIndex = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_LEQUAL)

        // Инициализация фона и объектов
        square = Square(context)
        sunCircle = TexturedSphere(context, 1.0f, 80, 40) // Уменьшение размера Солнца

        // Инициализация планет с радиусами и текстурами
        mars = TexturedSphere(context, 0.7f, 40, 20)
        mars.loadTexture(R.drawable.kmars)

        saturn = TexturedSphere(context, 1.0f, 60, 30)
        saturn.loadTexture(R.drawable._ksaturn)

        jupiter = TexturedSphere(context, 1.1f, 80, 40)
        jupiter.loadTexture(R.drawable.kjupiter)

        neptune = TexturedSphere(context, 0.8f, 60, 30)
        neptune.loadTexture(R.drawable._kneptune)

        uranus = TexturedSphere(context, 0.9f, 60, 30)
        uranus.loadTexture(R.drawable.kuranus)

        // Инициализация новых планет
        mercury = TexturedSphere(context, 0.5f, 30, 15)
        mercury.loadTexture(R.drawable._k_mercury)

        venus = TexturedSphere(context, 0.6f, 35, 18)
        venus.loadTexture(R.drawable._k_venus_surface)

        earth = TexturedSphere(context, 0.65f, 40, 20)
        earth.loadTexture(R.drawable.earth)

        // Инициализация Луны
        moon = TexturedSphere(context, 0.4f, 20, 10)  // Луна маленькая
        moon.loadTexture(R.drawable.moon)  // Замените на свою текстуру Луны
    }

    override fun onDrawFrame(gl: GL10?) {
        // Clear the frame and depth buffer
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // Set up the camera
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f)

        // Model-view-projection matrix
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        // Draw the background without depth testing
        GLES20.glDisable(GLES20.GL_DEPTH_TEST)
        square.draw(mvpMatrix)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)

        // Настройка камеры
        Matrix.setLookAtM(viewMatrix, 0, 0f, 20f, 20f, 0f, 0f, 0f, 0f, 1f, 0f)

        // Увеличение угла вращения
        angleX += 1f
        sunRotationAngle += 0.2f // Увеличение угла вращения Солнца

        // Отрисовка Меркурия и куба, если он выбран
        drawPlanetWithCube(mercury, planetRotations[0], planetDistances[0], angleX, 0f, planetScales[0], 0)

        // Отрисовка Венеры и куба, если он выбран
        drawPlanetWithCube(venus, -planetRotations[1], planetDistances[1], angleX, 0f, planetScales[1], 1)

        // Матрица Земли
        val earthMatrix = FloatArray(16)
        Matrix.setIdentityM(earthMatrix, 0)

        // Земля вращается вокруг своей оси и находится на расстоянии от Солнца
        Matrix.translateM(earthMatrix, 0, 0f, 0f, 0f)
        Matrix.rotateM(earthMatrix, 0, angleX * planetRotations[2], 0f, 1f, 0f)
        Matrix.translateM(earthMatrix, 0, planetDistances[2], 0f, 0f)

        // Применение проекционной и видовой матриц для Земли
        val earthFinalMatrix = FloatArray(16)
        Matrix.multiplyMM(earthFinalMatrix, 0, viewMatrix, 0, earthMatrix, 0)
        Matrix.multiplyMM(earthFinalMatrix, 0, projectionMatrix, 0, earthFinalMatrix, 0)

        // Отрисовка Земли и куба, если он выбран
        drawPlanetWithCube(earth, planetRotations[2], planetDistances[2], angleX, 0f, planetScales[2], 2)

        // Отрисовка Луны
        drawMoonPerpendicularToEcliptic(earthMatrix, angleX)

        // Остальные планеты
        drawPlanetWithCube(mars, planetRotations[3], planetDistances[3], angleX, 0f, planetScales[3], 3)
        drawPlanetWithCube(saturn, planetRotations[4], planetDistances[5], angleX, 0f, planetScales[4], 5)
        drawPlanetWithCube(jupiter, planetRotations[5], planetDistances[4], angleX, 0f, planetScales[5], 4)
        drawPlanetWithCube(uranus, -planetRotations[6], planetDistances[6], angleX, 0f, planetScales[6], 6)
        drawPlanetWithCube(neptune, planetRotations[7], planetDistances[7], angleX, 0f, planetScales[7], 7)

        // Отрисовка Солнца
        val sunMatrix = FloatArray(16)
        Matrix.setIdentityM(sunMatrix, 0)
        Matrix.translateM(sunMatrix, 0, 0f, 0f, 0f) // Солнце в центре
        Matrix.rotateM(sunMatrix, 0, sunRotationAngle, 0f, 1f, 0f) // Вращение Солнца вокруг своей оси
        Matrix.scaleM(sunMatrix, 0, 2.5f, 2.5f, 2.5f) // Уменьшение размера Солнца
        Matrix.multiplyMM(sunMatrix, 0, viewMatrix, 0, sunMatrix, 0)
        Matrix.multiplyMM(sunMatrix, 0, projectionMatrix, 0, sunMatrix, 0)
        sunCircle.draw(sunMatrix)
    }

    private fun drawPlanetWithCube(planet: TexturedSphere, rotationSpeed: Float, distanceFromSun: Float, angle: Float, depthZ: Float, scale: Float, planetIndex: Int) {
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

        // Если эта планета выбрана, отрисовываем куб
        if (planetIndex == selectedPlanetIndex) {
            val cubeMatrix = FloatArray(16)
            Matrix.setIdentityM(cubeMatrix, 0)

            // Перемещение куба на расстояние от планеты
            Matrix.translateM(cubeMatrix, 0, 0f, 0f, 0f) // Расстояние от планеты

            // Умножение на матрицу планеты
            Matrix.multiplyMM(cubeMatrix, 0, planetMatrix, 0, cubeMatrix, 0)

            // Масштабирование куба
            Matrix.scaleM(cubeMatrix, 0, 1f, 1f, 1f) // Уменьшение размера куба

            Matrix.rotateM(cubeMatrix, 0, angle * 1f, 0f, 1f, 1f)

            // Отрисовка куба
            val cube = Cube(context)
            cube.draw(cubeMatrix)
        }
    }

    fun selectNextPlanet() {
        selectedPlanetIndex = (selectedPlanetIndex + 1) % 9
    }

    fun selectPreviousPlanet() {
        selectedPlanetIndex = (selectedPlanetIndex - 1 + 9) % 9
    }

    fun showPlanetInfo() {
        val planetName = when (selectedPlanetIndex) {
            0 -> "Меркурий"
            1 -> "Венера"
            2 -> "Земля"
            3 -> "Марс"
            4 -> "Юпитер"
            5 -> "Сатурн"
            6 -> "Уран"
            7 -> "Нептун"
            8 -> "Луна"
            else -> "Неизвестная планета"
        }

        if (selectedPlanetIndex == 8) { // Предположим, что Луна имеет индекс 8
            val intent = Intent(context, MoonActivity::class.java)
            context.startActivity(intent)
        } else {
            val intent = Intent(context, PlanetInfoActivity::class.java).apply {
                putExtra("planetIndex", selectedPlanetIndex)
            }
            context.startActivity(intent)
        }
    }

    private fun drawMoonPerpendicularToEcliptic(earthMatrix: FloatArray, angle: Float) {
        // Матрица для Луны
        val moonMatrix = FloatArray(16)
        Matrix.setIdentityM(moonMatrix, 0)

        // Вращение Луны перпендикулярно плоскости эклиптики (вокруг оси X, вместо оси Y)
        Matrix.rotateM(moonMatrix, 0, angle * 2f, 1f, 0f, 0f)  // Вращение по оси X

        // Перемещение Луны на орбиту (расстояние от Земли)
        Matrix.translateM(moonMatrix, 0, 0f, 0f, 0.5f)  // Луна на расстоянии 1.5f от Земли по оси Z

        // Умножение на матрицу Земли (чтобы Луна вращалась вокруг Земли)
        Matrix.multiplyMM(moonMatrix, 0, earthMatrix, 0, moonMatrix, 0)

        // Собственное вращение Луны вокруг её оси
        Matrix.rotateM(moonMatrix, 0, angle, 0f, 1f, 0f)

        // Умножение на видовую и проекционную матрицы
        Matrix.multiplyMM(moonMatrix, 0, viewMatrix, 0, moonMatrix, 0)
        Matrix.multiplyMM(moonMatrix, 0, projectionMatrix, 0, moonMatrix, 0)

        // Масштабирование Луны
        Matrix.scaleM(moonMatrix, 0, 0.4f, 0.4f, 0.4f)

        // Отрисовка Луны
        moon.draw(moonMatrix)

        // Если Луна выбрана, отрисовываем куб
        if (selectedPlanetIndex == 8) {
            val cubeMatrix = FloatArray(16)
            Matrix.setIdentityM(cubeMatrix, 0)

            // Перемещение куба на расстояние от Луны
            Matrix.translateM(cubeMatrix, 0, 0f, 0f, 0f) // Расстояние от Луны

            // Умножение на матрицу Луны
            Matrix.multiplyMM(cubeMatrix, 0, moonMatrix, 0, cubeMatrix, 0)

            // Масштабирование куба
            Matrix.scaleM(cubeMatrix, 0, 1f, 1f, 1f) // Уменьшение размера куба

            // Отрисовка куба
            val cube = Cube(context)
            cube.draw(cubeMatrix)
        }
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