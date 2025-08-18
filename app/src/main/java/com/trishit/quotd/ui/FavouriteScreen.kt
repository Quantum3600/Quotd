package com.trishit.quotd.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun FavouriteScreen(viewModel: QuoteViewModel = hiltViewModel()) {
    val favourites by viewModel.favourites.observeAsState(emptyList())
    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(favourites, key = { it.q + it.a }) { quote ->
            val dismissState = rememberSwipeToDismissBoxState(
                positionalThreshold = { distance -> distance * 0.5f }
            )
            SwipeToDismissBox(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
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
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "delete",
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(start = 16.dp).size(32.dp)
                        )
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "delete",
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(end = 16.dp).size(32.dp)
                        )
                    }
                },
                enableDismissFromStartToEnd = true,
                enableDismissFromEndToStart = true,
                onDismiss = { direction ->
                    when (direction) {
                        SwipeToDismissBoxValue.StartToEnd -> {
                            viewModel.removeFromFavourites(quote)
                        }
                        SwipeToDismissBoxValue.EndToStart -> {
                            viewModel.removeFromFavourites(quote)
                        }
                        SwipeToDismissBoxValue.Settled -> {
                            // Do nothing when settled
                        }
                    }
                },
                content = {
                    Card(modifier = Modifier
                        .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(text = quote.q, style = MaterialTheme.typography.bodyLarge)
                            Spacer(Modifier.height(10.dp))
                            Text(text = quote.a, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            )
        }
    }
}



