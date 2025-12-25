package com.trishit.quotd.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HeartBroken
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trishit.quotd.FunnelDisplayFamily
import com.trishit.quotd.data.QuoteResponse

@Composable
fun FavouriteScreen(state: QuoteState, onEvent: (QuoteEvent) -> Unit) {
    val favourites = state.favourites
    val context = LocalContext.current
    Box(Modifier.fillMaxSize()) {
        if (favourites.isNotEmpty()) {
            LazyColumn(
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 8.dp, bottom = 78.dp, end = 8.dp)
            ) {
                items(favourites, key = { it.q + it.a }) { quote ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        positionalThreshold = { distance -> distance * 0.75f }
                    )
                    SwipeToDismissBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        state = dismissState,
                        backgroundContent = {
                            val color = MaterialTheme.colorScheme.errorContainer
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(color)
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.HeartBroken,
                                    contentDescription = "delete",
                                    tint = when (dismissState.targetValue) {
                                        SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.onErrorContainer
                                        SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier
                                        .padding(start = 16.dp)
                                        .size(32.dp)
                                )
                                Icon(
                                    imageVector = Icons.Filled.HeartBroken,
                                    contentDescription = "delete",
                                    tint = when (dismissState.targetValue) {
                                        SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.errorContainer
                                        SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.onErrorContainer
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier
                                        .padding(end = 16.dp)
                                        .size(32.dp)
                                )
                            }
                        },
                        enableDismissFromStartToEnd = true,
                        enableDismissFromEndToStart = true,
                        onDismiss = { direction ->
                            when (direction) {
                                SwipeToDismissBoxValue.StartToEnd -> {
                                    onEvent(QuoteEvent.RemoveFromFavourites(quote))
                                }

                                SwipeToDismissBoxValue.EndToStart -> {
                                    onEvent(QuoteEvent.RemoveFromFavourites(quote))
                                }

                                SwipeToDismissBoxValue.Settled -> {}
                            }
                        },
                        content = {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .clickable(true, onClick = {
                                            val sendIntent = Intent().apply {
                                                action = Intent.ACTION_SEND
                                                putExtra(Intent.EXTRA_TEXT, "$quote.q - $quote.a")
                                                type = "text/plain"
                                            }
                                            val shareIntent = Intent.createChooser(sendIntent, null)
                                            context.startActivity(shareIntent)
                                        })
                                ) {
                                    Text(text = quote.q, style = MaterialTheme.typography.bodyLarge)
                                    Spacer(Modifier.height(10.dp))
                                    Text(
                                        text = quote.a,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.End,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Nothing to show here!",
                    fontFamily = FunnelDisplayFamily,
                    fontSize = 30.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        }
        Text(
            text = "Made with ❤️ in India",
            fontFamily = FunnelDisplayFamily,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.3f),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp, 40.dp)
        )
    }
}
@Preview
@Composable 
fun FavouritePreview() {
    FavouriteScreen(
        state = QuoteState(
            favourites = listOf(
                QuoteResponse(
                    q = "The only thing we have to fear is fear itself.",
                    a = "Franklin D. Roosevelt"
                )
            )
        ),
        onEvent = {}
    )
}

