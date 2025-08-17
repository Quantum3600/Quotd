package com.trishit.quotd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.trishit.quotd.ui.FavouriteScreen
import com.trishit.quotd.ui.HomeScreen
import com.trishit.quotd.ui.QuoteViewModel
import com.trishit.quotd.ui.theme.QuotdTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuotdTheme {
                val viewModel: QuoteViewModel = hiltViewModel()
                MainScaffold(viewModel)
            }
        }
    }
}

@Composable
private fun MainScaffold(viewModel: QuoteViewModel) {
    var selectedTab by rememberSaveable { mutableStateOf(BottomTab.Home) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == BottomTab.Home,
                    onClick = { selectedTab = BottomTab.Home },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") }
                )
                NavigationBarItem(
                    selected = selectedTab == BottomTab.Favourites,
                    onClick = { selectedTab = BottomTab.Favourites },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Favourites") },
                    label = { Text("Favourites") }
                )
            }
        }
    ) { innerPadding ->
        TabContent(selectedTab = selectedTab, innerPadding = innerPadding, viewModel = viewModel)
    }
}

private enum class BottomTab { Home, Favourites }

@Composable
private fun TabContent(selectedTab: BottomTab, innerPadding: PaddingValues, viewModel: QuoteViewModel) {
    when (selectedTab) {
        BottomTab.Home -> HomeScreen(viewModel)
        BottomTab.Favourites -> FavouriteScreen(viewModel)
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    QuotdTheme {
        // Preview with a fake layout; not rendering actual data here.
        Text("Quotd")
    }
}