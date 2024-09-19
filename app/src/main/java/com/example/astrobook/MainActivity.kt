package com.example.astrobook

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.astrobook.ui.theme.AstroBookTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setContent {
            AstroBookTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DrawNews()
                }
            }
        }
    }
}

@Composable
fun DrawNews() {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.weight(1f)) {
            NewsScreen(
                modifier = Modifier.weight(1f),
                image = painterResource(id = R.drawable.image1),
                text = "Text 1"
            )
            NewsScreen(
                modifier = Modifier.weight(1f),
                image = painterResource(id = R.drawable.image2),
                text = "Text 2"
            )
        }
        Row(modifier = Modifier.weight(1f)) {
            NewsScreen(
                modifier = Modifier.weight(1f),
                image = painterResource(id = R.drawable.image3),
                text = "Text 3"
            )
            NewsScreen(
                modifier = Modifier.weight(1f),
                image = painterResource(id = R.drawable.image4),
                text = "Text 4"
            )
        }
    }
}

@Composable
fun NewsScreen(image: Painter, text: String, modifier: Modifier) {
    Box(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.weight(0.9f)) {
                Image(
                    painter = image,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Box(modifier = Modifier.weight(0.1f), contentAlignment = Alignment.Center) {
                Text(
                    text = text,
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hell $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AstroBookTheme {
        Greeting("Android")
    }
}