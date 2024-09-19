package com.example.astrobook

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.astrobook.ui.theme.AstroBookTheme
import kotlin.random.Random


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
                    AllNews()
                }
            }
        }
    }
}

data class NewsItem(val imageResId: Int, val text: String, var likes: Int)



@Composable
fun DrawNews(newsList: List<NewsItem>) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.weight(1f)) {
            NewsScreen(
                modifier = Modifier.weight(1f),
                image = painterResource(id = newsList[0].imageResId),
                text = "Text 1"
            )
            NewsScreen(
                modifier = Modifier.weight(1f),
                image = painterResource(id = newsList[1].imageResId),
                text = "Text 2"
            )
        }
        Row(modifier = Modifier.weight(1f)) {
            NewsScreen(
                modifier = Modifier.weight(1f),
                image = painterResource(id = newsList[2].imageResId),
                text = "Text 3"
            )
            NewsScreen(
                modifier = Modifier.weight(1f),
                image = painterResource(id = newsList[3].imageResId),
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
fun AllNews() {
    val newsList = remember {
        mutableStateListOf(
            NewsItem(R.drawable.image1, "LIKES: ", 0),
            NewsItem(R.drawable.image2, "LIKES: ", 0),
            NewsItem(R.drawable.image3, "LIKES: ", 0),
            NewsItem(R.drawable.image4, "LIKES: ", 0),
            NewsItem(R.drawable.image5, "LIKES: ", 0),
            NewsItem(R.drawable.image6, "LIKES: ", 0),
            NewsItem(R.drawable.image7, "LIKES: ", 0),
            NewsItem(R.drawable.image8, "LIKES: ", 0),
            NewsItem(R.drawable.image9, "LIKES: ", 0),
            NewsItem(R.drawable.image10, "LIKES: ", 0)
        )
    }
    val handler = Handler(Looper.getMainLooper())
    val runnable = remember {
        object : Runnable {
            override fun run() {
                val randomIndex = Random.nextInt(0, 4)
                val randomNewsIndex = Random.nextInt(4, newsList.size)
                val temp = newsList[randomIndex]
                newsList[randomIndex] = newsList[randomNewsIndex]
                newsList[randomNewsIndex] = temp
                handler.postDelayed(this, 5000)
            }
        }
    }

    LaunchedEffect(Unit) {
        handler.post(runnable)
    }

    DrawNews(newsList)
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