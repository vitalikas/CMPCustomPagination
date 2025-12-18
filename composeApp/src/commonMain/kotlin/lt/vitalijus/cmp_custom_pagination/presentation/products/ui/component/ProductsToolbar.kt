package lt.vitalijus.cmp_custom_pagination.presentation.products.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import lt.vitalijus.cmp_custom_pagination.domain.model.SortOption

/**
 * Unified toolbar combining View mode and Sort controls in a single row.
 * 
 * Features:
 * - View selector (Grid/List) on the left
 * - Sort selector on the right
 * - Confirmation dialog for loading all items
 * - Info banner when sorting without all items
 * - Consistent Material 3 design
 * 
 * @param isGridLayout Current layout mode (true = grid, false = list)
 * @param onLayoutToggle Callback when layout is toggled
 * @param currentSortOption Currently selected sort option
 * @param allItemsLoaded Whether all items have been loaded
 * @param loadedItemsCount Number of currently loaded items
 * @param onSortOptionChange Callback when sort option changes
 * @param onLoadAllItems Callback to load all items
 * @param showLayoutToggle Whether to show the layout toggle (default true)
 * @param modifier Optional modifier
 */
@Composable
fun ProductsToolbar(
    isGridLayout: Boolean,
    onLayoutToggle: () -> Unit,
    currentSortOption: SortOption,
    allItemsLoaded: Boolean,
    loadedItemsCount: Int,
    onSortOptionChange: (SortOption) -> Unit,
    onLoadAllItems: () -> Unit,
    showLayoutToggle: Boolean = true,
    modifier: Modifier = Modifier
) {
    var sortMenuExpanded by remember { mutableStateOf(false) }
    var showLoadAllDialog by remember { mutableStateOf(false) }
    var pendingSortOption by remember { mutableStateOf<SortOption?>(null) }

    Column(modifier = modifier) {
        // Info banner when sorting without all items (animated)
        AnimatedVisibility(
            visible = currentSortOption != SortOption.NONE && !allItemsLoaded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Sorting $loadedItemsCount items",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    TextButton(onClick = onLoadAllItems) {
                        Text(
                            text = "Load All",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }

        // Unified toolbar row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // View selector on the left
            if (showLayoutToggle) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "View:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(onClick = onLayoutToggle) {
                        Icon(
                            imageVector = if (isGridLayout) Icons.Default.Apps else Icons.Default.List,
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

            // Sort selector on the right
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Sort:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(
                    onClick = { sortMenuExpanded = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = "Sort",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = when (currentSortOption) {
                            SortOption.NONE -> "Default"
                            SortOption.PRICE_LOW_TO_HIGH -> "Price ↑"
                            SortOption.PRICE_HIGH_TO_LOW -> "Price ↓"
                            SortOption.RATING -> "Rating"
                            SortOption.NAME -> "Name"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 8.dp)
                    )

                    DropdownMenu(
                        expanded = sortMenuExpanded,
                        onDismissRequest = { sortMenuExpanded = false }
                    ) {
                        SortOption.entries.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = option.displayName,
                                        color = if (option == currentSortOption) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        },
                                        style = if (option == currentSortOption) {
                                            MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                            )
                                        } else {
                                            MaterialTheme.typography.bodyMedium
                                        }
                                    )
                                },
                                onClick = {
                                    sortMenuExpanded = false
                                    // If not NONE and not all items loaded, show dialog
                                    if (option != SortOption.NONE && !allItemsLoaded) {
                                        pendingSortOption = option
                                        showLoadAllDialog = true
                                    } else {
                                        onSortOptionChange(option)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Load all items confirmation dialog
    if (showLoadAllDialog && pendingSortOption != null) {
        AlertDialog(
            onDismissRequest = {
                showLoadAllDialog = false
                pendingSortOption = null
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.SwapVert,
                    contentDescription = null
                )
            },
            title = {
                Text("Enable Sorting?")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Sorting works best when all items are loaded.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "This will quickly load all product data. Images will load as you scroll for optimal performance.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "\nContinue?",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val option = pendingSortOption
                        showLoadAllDialog = false
                        pendingSortOption = null
                        if (option != null) {
                            // Apply sort first, THEN trigger load all
                            // This way the sort is already set when loading completes
                            onSortOptionChange(option)
                            onLoadAllItems()
                        }
                    }
                ) {
                    Text("Load All & Sort")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showLoadAllDialog = false
                        pendingSortOption = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
