package com.example.astrobook

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.astrobook.ui.theme.AstroBookTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setContent {
            AstroBookTheme {
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

data class NewsItem(val imageResId: Int, val text: String, val likes: MutableState<Int>)

@Composable
fun DrawNews(newsList: List<NewsItem>) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.weight(1f)) {
            NewsScreen(
                modifier = Modifier.weight(1f),
                image = painterResource(id = newsList[0].imageResId),
                text = newsList[0].text,
                likes = newsList[0].likes.value,
                onLikeClick = { newsList[0].likes.value++ }
            )
            NewsScreen(
                modifier = Modifier.weight(1f),
                image = painterResource(id = newsList[1].imageResId),
                text = newsList[1].text,
                likes = newsList[1].likes.value,
                onLikeClick = { newsList[1].likes.value++ }
            )
        }
        Row(modifier = Modifier.weight(1f)) {
            NewsScreen(
                modifier = Modifier.weight(1f),
                image = painterResource(id = newsList[2].imageResId),
                text = newsList[2].text,
                likes = newsList[2].likes.value,
                onLikeClick = { newsList[2].likes.value++ }
            )
            NewsScreen(
                modifier = Modifier.weight(1f),
                image = painterResource(id = newsList[3].imageResId),
                text = newsList[3].text,
                likes = newsList[3].likes.value,
                onLikeClick = { newsList[3].likes.value++ }
            )
        }
    }
}

@Composable
fun NewsScreen(image: Painter, text: String, likes: Int, onLikeClick: () -> Unit, modifier: Modifier) {
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
                    text = "$text - $likes",
                    modifier = Modifier.clickable { onLikeClick() }
                )
            }
        }
    }
}

@Composable
fun AllNews() {
    val newsList = remember {
        mutableStateListOf(
            NewsItem(R.drawable.image1, "Likes", mutableStateOf(0)),
            NewsItem(R.drawable.image2, "Likes", mutableStateOf(0)),
            NewsItem(R.drawable.image3, "Likes", mutableStateOf(0)),
            NewsItem(R.drawable.image4, "Likes", mutableStateOf(0)),
            NewsItem(R.drawable.image5, "Likes", mutableStateOf(0)),
            NewsItem(R.drawable.image6, "Likes", mutableStateOf(0)),
            NewsItem(R.drawable.image7, "Likes", mutableStateOf(0)),
            NewsItem(R.drawable.image8, "Likes", mutableStateOf(0)),
            NewsItem(R.drawable.image9, "Likes", mutableStateOf(0)),
            NewsItem(R.drawable.image10, "Likes", mutableStateOf(0))
        )
    }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            while (true) {
                delay(5000)
                val randomIndex = Random.nextInt(0, 4)
                val randomNewsIndex = Random.nextInt(4, newsList.size)
                val temp = newsList[randomIndex]
                newsList[randomIndex] = newsList[randomNewsIndex]
                newsList[randomNewsIndex] = temp
            }
        }
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