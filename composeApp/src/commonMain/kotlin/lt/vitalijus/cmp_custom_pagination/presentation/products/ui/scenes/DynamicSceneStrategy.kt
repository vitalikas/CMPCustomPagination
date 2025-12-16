package lt.vitalijus.cmp_custom_pagination.presentation.products.ui.scenes

import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND

class DynamicSceneStrategy<T : Any>(
    val windowSizeClass: WindowSizeClass
) : SceneStrategy<T> {

    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        if (!windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)) {
            return null
        }

        // Determine which scene type to create based on the list/favorites entry present
        val hasFavorites =
            entries.any { it.metadata.containsKey(FavoritesDetailScene.FAVORITES_KEY) }

        if (hasFavorites) {
            // Use FavoritesDetailScene strategy
            val detailEntry = entries
                .lastOrNull()
                ?.takeIf { navEntry ->
                    navEntry.metadata.containsKey(FavoritesDetailScene.DETAIL_KEY)
                }
                ?: return null

            val favoritesEntry = entries
                .findLast { navEntry ->
                    navEntry.metadata.containsKey(FavoritesDetailScene.FAVORITES_KEY)
                }
                ?: return null

            return FavoritesDetailScene(
                favorites = favoritesEntry,
                detail = detailEntry,
                key = favoritesEntry.contentKey,
                previousEntries = entries.dropLast(1)
            )
        } else {
            // Use ListDetailScene strategy
            val detailEntry = entries
                .lastOrNull()
                ?.takeIf { navEntry ->
                    navEntry.metadata.containsKey(ListDetailScene.DETAIL_KEY)
                }
                ?: return null

            val listEntry = entries
                .findLast { navEntry ->
                    navEntry.metadata.containsKey(ListDetailScene.LIST_KEY)
                }
                ?: return null

            return ListDetailScene(
                list = listEntry,
                detail = detailEntry,
                key = listEntry.contentKey,
                previousEntries = entries.dropLast(1)
            )
        }
    }
}

@Composable
fun <T : Any> rememberDynamicSceneStrategy(): DynamicSceneStrategy<T> {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    return remember(windowSizeClass) {
        DynamicSceneStrategy(windowSizeClass = windowSizeClass)
    }
}
