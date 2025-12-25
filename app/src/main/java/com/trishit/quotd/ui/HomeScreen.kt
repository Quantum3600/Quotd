package com.trishit.quotd.ui

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trishit.quotd.FunnelDisplayFamily
import com.trishit.quotd.MuseoModernoFamily
import com.trishit.quotd.components.HeartPopup
import com.trishit.quotd.data.QuoteResponse

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(state: QuoteState, onEvent: (QuoteEvent) -> Unit) {
    val pagerState = rememberPagerState(initialPage = 1, pageCount = { 3 })

    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage == 0) {
            onEvent(QuoteEvent.FetchPreviousQuote)
            pagerState.scrollToPage(1)
        } else if (pagerState.currentPage == 2) {
            onEvent(QuoteEvent.FetchNextQuote)
            pagerState.scrollToPage(1)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (state.isLoading && state.quote == null) {
            ContainedLoadingIndicator()
        } else if (state.errorMessage != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = state.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))
                Button(onClick = { onEvent(QuoteEvent.ForceRefreshAndGetQuote) }) {
                    Text("Retry")
                }
            }
        } else {
            VerticalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                when (page) {
                    0 -> if (state.canNavigateToPrevious) {
                        QuoteCard(
                            quote = state.quote,
                            isLiked = state.favourites.contains(state.quote),
                            onEvent = onEvent
                        )
                    }
                    1 -> state.quote?.let {
                        QuoteCard(
                            quote = it,
                            isLiked = state.favourites.contains(it),
                            onEvent = onEvent
                        )
                    }
                    2 -> QuoteCard(
                        quote = state.quote,
                        isLiked = state.favourites.contains(state.quote),
                        onEvent = onEvent
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun QuoteCard(
    quote: QuoteResponse?,
    isLiked: Boolean,
    onEvent: (QuoteEvent) -> Unit
) {
    var showHeartPopup by remember { mutableStateOf(false) }
    var heartPosition by remember { mutableStateOf(Offset.Zero) }
    var isCentered by remember { mutableStateOf(false) }
    val annotatedLinkString = buildAnnotatedString {
        append("provided by ")
        pushLink(LinkAnnotation.Url("https://zenquotes.io/"))
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                textDecoration = TextDecoration.None
            )
        ) {
            append("zenquotes.io")
        }
        pop()
    }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Card(
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerHigh),
            shape = RoundedCornerShape(40.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .pointerInput(quote, isLiked) {
                    detectTapGestures(
                        onDoubleTap = { tapOffset ->
                            quote?.let {
                                heartPosition = tapOffset
                                isCentered = false
                                showHeartPopup = true
                                if (!isLiked) {
                                    onEvent(QuoteEvent.ToggleFavourite(it))
                                }
                            }
                        }
                    )
                }
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (quote != null) {
                    Spacer(Modifier.height(48.dp))
                    Column {
                        Text(
                            text = "\"${quote.q}\"",
                            style = MaterialTheme.typography.headlineMediumEmphasized,
                            textAlign = TextAlign.Center,
                            fontFamily = FunnelDisplayFamily,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "- ${quote.a}",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Right,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 8.dp)
                        )
                        Spacer(Modifier.height(48.dp))
                        Text(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth(),
                            text = annotatedLinkString,
                            fontSize = 12.sp,
                            fontFamily = MuseoModernoFamily,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(vertical = 32.dp, horizontal = 8.dp),
                    ) {
                        IconButton(
                            modifier = Modifier.size(52.dp),
                            onClick = {
                                if (!isLiked) {
                                    isCentered = true
                                    showHeartPopup = true
                                }
                                onEvent(QuoteEvent.ToggleFavourite(quote))
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
                                    putExtra(Intent.EXTRA_TEXT, "\"${quote.q}\" - ${quote.a}")
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
                                imageVector = Icons.Default.IosShare,
                                contentDescription = "Share Quote"
                            )
                        }
                    }
                }
            }
        }

        // Heart popup now outside the Card, on top of everything
        HeartPopup(
            isVisible = showHeartPopup,
            isCentered = isCentered,
            offsetX = with(LocalDensity.current) { heartPosition.x.toDp() },
            offsetY = with(LocalDensity.current) { heartPosition.y.toDp() },
            onAnimationEnd = {
                showHeartPopup = false
            }
        )
    }
}
@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        state = QuoteState(
            quote = QuoteResponse(
                q = "The only thing we have to fear is fear itself.",
                a = "Franklin D. Roosevelt"
            )
        ),
        onEvent = {}
    )
}
