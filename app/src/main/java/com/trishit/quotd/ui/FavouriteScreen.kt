package com.trishit.quotd.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun FavouriteScreen(viewModel: QuoteViewModel = hiltViewModel()) {
    LazyColumn(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        items(viewModel.favourites.value?.size ?: 0) { quote ->
            Card(modifier = Modifier.padding(8.dp)) {
                Column {
                    Text(text = viewModel.favourites.value?.get(quote)?.q ?: "Loading...")
                    Spacer(Modifier.height(10.dp))
                    Text(text = viewModel.favourites.value?.get(quote)?.a ?: "Loading...", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}