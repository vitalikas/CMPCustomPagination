package lt.vitalijus.cmp_custom_pagination.presentation.products.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Reusable header component with view layout toggle in unified style.
 * 
 * Matches the SortBar design pattern:
 * - Left: Label text
 * - Right: Icon + text button
 * 
 * Used by:
 * - ProductsScreen
 * - FavoritesScreen
 * 
 * @param showLayoutToggle Whether to show the layout toggle button
 * @param isGridLayout Current layout mode (true = grid, false = list)
 * @param onLayoutToggle Callback when layout toggle is clicked
 */
@Composable
fun ScreenHeader(
    showLayoutToggle: Boolean = true,
    isGridLayout: Boolean = true,
    onLayoutToggle: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "View:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (showLayoutToggle) {
            TextButton(onClick = onLayoutToggle) {
                Icon(
                    imageVector = if (isGridLayout) AppIcons.GridView else AppIcons.ViewList,
                    contentDescription = if (isGridLayout) "Grid view" else "List view",
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (isGridLayout) "Grid" else "List",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}


