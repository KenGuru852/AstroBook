// PlanetInfoActivity.kt
package com.example.astrobook

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PlanetInfoActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Получаем индекс планеты из Intent
        val planetIndex = intent.getIntExtra("planetIndex", 0)

        // Создаем LinearLayout
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setPadding(16, 16, 16, 16)
        }

        // Создаем ImageView для изображения планеты
        val planetImage = ImageView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            adjustViewBounds = true
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        // Создаем TextView для описания планеты
        val planetDescription = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setPadding(0, 16, 0, 0)
            textSize = 16f
        }

        // Устанавливаем изображение и описание в зависимости от индекса планеты
        when (planetIndex) {
            0 -> {
                planetImage.setImageResource(R.drawable._k_mercury)
                planetDescription.text = getString(R.string.mercury_description)
            }
            1 -> {
                planetImage.setImageResource(R.drawable._k_venus_surface)
                planetDescription.text = getString(R.string.venus_description)
            }
            2 -> {
                planetImage.setImageResource(R.drawable.earth)
                planetDescription.text = getString(R.string.earth_description)
            }
            3 -> {
                planetImage.setImageResource(R.drawable.kmars)
                planetDescription.text = getString(R.string.mars_description)
            }
            4 -> {
                planetImage.setImageResource(R.drawable.kjupiter)
                planetDescription.text = getString(R.string.jupiter_description)
            }
            5 -> {
                planetImage.setImageResource(R.drawable._ksaturn)
                planetDescription.text = getString(R.string.saturn_description)
            }
            6 -> {
                planetImage.setImageResource(R.drawable.kuranus)
                planetDescription.text = getString(R.string.uranus_description)
            }
            7 -> {
                planetImage.setImageResource(R.drawable._kneptune)
                planetDescription.text = getString(R.string.neptune_description)
            }
        }

        // Добавляем ImageView и TextView в LinearLayout
        layout.addView(planetImage)
        layout.addView(planetDescription)

        // Устанавливаем LinearLayout как основной контент
        setContentView(layout)
    }
}