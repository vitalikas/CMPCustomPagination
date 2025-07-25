package lt.vitalijus.cmp_custom_pagination

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.serialization.json.Json
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        val viewModel = viewModel(
            initializer = {
                ProductsViewModel(
                    // Note: We're not using Dependency Injection (DI) in this example.
                    // This is intentional, to keep the code simple and focused on the pagination learning process.
                    // In production code, DI (e.g., with Hilt, Koin, etc.) is recommended for better scalability and testability.
                    api = ProductsApi(
                        httpClient = HttpClient(
                            engine = HttpClientEngineFactory().create()
                        ) {
                            install(Logging) {
                                logger = Logger.SIMPLE
                                level = LogLevel.ALL
                            }

                            install(ContentNegotiation) {
                                json(
                                    json = Json { ignoreUnknownKeys = true }
                                )
                            }
                        }
                    )
                )
            }
        )

        val state by viewModel.state.collectAsStateWithLifecycle()

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
        ) { contentPadding ->
            val lazyListState = rememberLazyListState()

            LaunchedEffect(state.products) {
                snapshotFlow {
                    lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                }
                    .distinctUntilChanged()
                    .collect { lastVisibleIndex ->
                        if (lastVisibleIndex == state.products.lastIndex) {
                            viewModel.loadNextProducts()
                        }
                    }
            }

            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = contentPadding
            ) {
                items(state.products) { product ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = product.title,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$ ${product.price}"
                        )
                    }
                }
                if (state.isLoadingMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}
