package com.trishit.quotd

import android.content.res.Configuration
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.trishit.quotd.components.BottomTabs
import com.trishit.quotd.data.QuoteResponse
import com.trishit.quotd.ui.FavouriteScreen
import com.trishit.quotd.ui.HomeScreen
import com.trishit.quotd.ui.QuoteEvent
import com.trishit.quotd.ui.QuoteState
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
                val state by viewModel.state.collectAsState()
                MainScaffold(
                    state = state,
                    onEvent = viewModel::onEvent
                )
            }
        }
    }
}


enum class MainNavTab { Home, Favourites }
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MainScaffold(
    state: QuoteState,
    onEvent: (QuoteEvent) -> Unit
) {
    val selectedTabState = rememberSaveable { mutableStateOf(MainNavTab.Home) }
    val stateHolder = rememberSaveableStateHolder()
    val background = MaterialTheme.colorScheme.primaryContainer

    Box(Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(
                        text = when (selectedTabState.value) {
                            MainNavTab.Home -> "Quotd"
                            MainNavTab.Favourites -> "Favourites"
                        },
                        fontFamily = FunnelDisplayFamily, // This will be fixed later
                        fontWeight = when (selectedTabState.value) {
                            MainNavTab.Home -> FontWeight.ExtraBold
                            MainNavTab.Favourites -> FontWeight.Normal
                        },
                        fontSize = when (selectedTabState.value) {
                            MainNavTab.Home -> 48.sp
                            MainNavTab.Favourites -> MaterialTheme.typography.headlineLarge.fontSize
                        },
                    ) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
                    modifier = Modifier.semantics { contentDescription = "TopAppBar" }
                )
            }
        ) { innerPadding ->
            val selected = selectedTabState.value
            Box(Modifier.padding(innerPadding)) {
                Crossfade(targetState = selected, label = "tab-crossfade") { tab ->
                    stateHolder.SaveableStateProvider(key = tab.name) {
                        when (tab) {
                            MainNavTab.Home -> HomeScreen(state, onEvent)
                            MainNavTab.Favourites -> FavouriteScreen(state, onEvent)
                        }
                    }
                }
            }
        }

        BottomTabs(
            selectedTabState = selectedTabState,
            containerColor = background,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 32.dp)
        )
    }

}
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AppPreview() {
    MainScaffold(
        state = QuoteState(
            quote = QuoteResponse(
                q = "The only thing we have to fear is fear itself.",
                a = "Franklin D. Roosevelt"
            )
        ),
        onEvent = {}
    )
}
