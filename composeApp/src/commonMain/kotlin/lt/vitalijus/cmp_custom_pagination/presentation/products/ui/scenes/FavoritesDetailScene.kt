package lt.vitalijus.cmp_custom_pagination.presentation.products.ui.scenes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.Scene

class FavoritesDetailScene<T : Any>(
    val favorites: NavEntry<T>,
    val detail: NavEntry<T>?,
    override val key: Any,
    override val previousEntries: List<NavEntry<T>>
) : Scene<T> {

    override val entries: List<NavEntry<T>>
        get() = listOfNotNull(favorites, detail)

    override val content: @Composable (() -> Unit)
        get() = {
            Row(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .weight(4f)
                ) {
                    favorites.Content()
                }
                if (detail != null) {
                    Column(
                        modifier = Modifier
                            .weight(6f)
                    ) {
                        detail.Content()
                    }
                }
            }
        }

    companion object {
        const val FAVORITES_KEY = "FavoritesDetailScene-Favorites"
        const val DETAIL_KEY = "FavoritesDetailScene-Detail"

        fun listPane() = mapOf(FAVORITES_KEY to true)
        fun detailPane() = mapOf(DETAIL_KEY to true)
    }
}
