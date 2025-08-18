package com.trishit.quotd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trishit.quotd.components.BottomTabs
import com.trishit.quotd.ui.FavouriteScreen
import com.trishit.quotd.ui.HomeScreen
import com.trishit.quotd.ui.QuoteViewModel
import com.trishit.quotd.ui.theme.QuotdTheme
import dagger.hilt.android.AndroidEntryPoint

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


enum class MainNavTab { Home, Favourites }
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MainScaffold(viewModel: QuoteViewModel) {
    val selectedTabState = rememberSaveable { mutableStateOf(MainNavTab.Home) }
    val stateHolder = rememberSaveableStateHolder()
    val background = MaterialTheme.colorScheme.primaryContainer

    Box(Modifier.fillMaxSize()) {
        // Content scaffold (no bottomBar)
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(
                        text = when (selectedTabState.value) {
                            MainNavTab.Home -> "Quotd"
                            MainNavTab.Favourites -> "Favourites"
                        },
                        fontFamily = FunnelDisplayFamily,
                        fontWeight = when (selectedTabState.value) {
                            MainNavTab.Home -> FontWeight.ExtraBold
                            MainNavTab.Favourites -> FontWeight.Normal
                        },
                        fontSize = when (selectedTabState.value) {
                            MainNavTab.Home -> 48.sp
                            MainNavTab.Favourites -> MaterialTheme.typography.headlineLarge.fontSize
                        },
                    ) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.semantics { contentDescription = "TopAppBar" }
                )
            }
        ) { innerPadding ->
            val selected = selectedTabState.value
            // Add extra bottom padding so content doesn't get covered by the overlaid bottom bar
            Box(Modifier.padding(innerPadding)) {
                Crossfade(targetState = selected, label = "tab-crossfade") { tab ->
                    stateHolder.SaveableStateProvider(key = tab.name) {
                        when (tab) {
                            MainNavTab.Home -> HomeScreen(viewModel)
                            MainNavTab.Favourites -> FavouriteScreen(viewModel)
                        }
                    }
                }
            }
        }

        // Overlay BottomTabs outside Scaffold so it's not constrained by Scaffold.bottomBar
        BottomTabs(
            selectedTabState = selectedTabState,
            containerColor = background,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, bottom = 24.dp)
        )
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