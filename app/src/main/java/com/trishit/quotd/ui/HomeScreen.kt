package com.trishit.quotd.ui

import android.content.Intent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(viewModel: QuoteViewModel = hiltViewModel()) {
    val quote by viewModel.quote.observeAsState()
    val favourites by viewModel.favourites.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState()
    val offsetY = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()
    val currentQuote = rememberUpdatedState(quote)
    val isLiked = remember(quote, favourites) {
        val q = quote
        q != null && favourites.any { it.q == q.q && it.a == q.a }
    }
    val swipePx = with(LocalDensity.current) { 60.dp.toPx()}

    LaunchedEffect(quote) {
        if (quote != null) {
            // Reset animations when a new quote is loaded
            offsetY.snapTo(300f)
            alpha.snapTo(0f)
            launch {
                offsetY.animateTo(0f, animationSpec = tween(400))

            }
            launch {
                alpha.animateTo(1f, animationSpec = tween(400))
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(40.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .graphicsLayer {
                    translationY = offsetY.value
                    this.alpha = alpha.value
                }
                .pointerInput(Unit) {
                    // Reset animations on tap
                    coroutineScope {
                        launch {
                            detectTapGestures(
                                onDoubleTap = {
                                    currentQuote.let {
                                        val q = it.value ?: return@let
                                        viewModel.toggleFavourite(q)
                                    }
                                }
                            )
                        }
                        launch {
                            detectDragGestures(
                                onDragEnd = {
                                    if (offsetY.value < -swipePx) {
                                        coroutineScope.launch {
                                            offsetY.animateTo(-1000f, animationSpec = tween(500))
                                            alpha.animateTo(0f, animationSpec = tween(500))
                                            viewModel.fetchRandomQuote()
                                        }
                                    } else {
                                        coroutineScope.launch {
                                            offsetY.animateTo(0f, animationSpec = tween(500))
                                            alpha.animateTo(1f, animationSpec = tween(500))
                                        }
                                    }
                                }
                            ) { change, dragAmount ->
                                if (abs(dragAmount.y) > abs(dragAmount.x)) {
                                    change.consume()
                                    coroutineScope.launch {
                                        offsetY.snapTo(offsetY.value + dragAmount.y)
                                    }
                                }
                            }
                        }
                    }
                }
                .align(Alignment.Center),
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isLoading && quote == null -> {
                            ContainedLoadingIndicator()
                        }

                        errorMessage != null -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = errorMessage ?: "Error",
                                    color = MaterialTheme.colorScheme.error,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(12.dp))
                                Button(onClick = { viewModel.fetchRandomQuote() }) {
                                    Text("Retry")
                                }
                            }
                        }
                        quote != null -> {
                            val it = quote!!
                            val context = LocalContext.current
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column {
                                    Text(
                                        text = "\"${it.q}\"",
                                        style = MaterialTheme.typography.headlineMediumEmphasized,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "- ${it.a}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        textAlign = TextAlign.Right,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(end = 8.dp)
                                    )
                                    Spacer(Modifier.height(20.dp))
                                }
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(vertical = 32.dp, horizontal = 8.dp),
                                ) {
                                    IconButton(
                                        modifier = Modifier.size(52.dp),
                                        onClick = {
                                            viewModel.toggleFavourite(it)
                                        }
                                    ) {
                                        Icon(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(8.dp),
                                            imageVector =
                                                if (isLiked) Icons.Filled.Favorite
                                                else Icons.Filled.FavoriteBorder,
                                            contentDescription = "Like Quote",
                                            tint = if (isLiked) Color.Red else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    IconButton(
                                        modifier = Modifier.size(52.dp),
                                        onClick = {
                                            val sendIntent = Intent().apply {
                                                action = Intent.ACTION_SEND
                                                putExtra(Intent.EXTRA_TEXT, "\"${it.q}\" - ${it.a}")
                                                type = "text/plain"
                                            }
                                            val shareIntent = Intent.createChooser(sendIntent, null)
                                            context.startActivity(shareIntent)
                                        }
                                    ) {
                                        Icon(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(8.dp),
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Share Quote"
                                        )
                                    }
                                }
                            }
                        }

                        else -> {
                            // Initial state (no data yet, not loading) - show loader
                            ContainedLoadingIndicator()
                        }
                    }
                }
            }
        )

        // Overlay loader visible when loading and the card has been faded out (after drag)
        if (isLoading && alpha.value <= 0.01f) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ContainedLoadingIndicator()
            }
        }
    }
}
