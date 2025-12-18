package lt.vitalijus.cmp_custom_pagination.presentation.products.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
 * Reusable sort bar component for sorting product lists.
 * 
 * Features:
 * - Dropdown menu with sort options
 * - Confirmation dialog for loading all items
 * - Info banner when sorting without all items
 * - Material 3 design
 * 
 * @param currentSortOption Currently selected sort option
 * @param allItemsLoaded Whether all items have been loaded
 * @param loadedItemsCount Number of currently loaded items
 * @param onSortOptionChange Callback when sort option changes (called only after confirmation)
 * @param onLoadAllItems Callback to load all items
 * @param modifier Optional modifier for the sort bar
 */
@Composable
fun SortBar(
    currentSortOption: SortOption,
    allItemsLoaded: Boolean,
    loadedItemsCount: Int,
    onSortOptionChange: (SortOption) -> Unit,
    onLoadAllItems: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var showLoadAllDialog by remember { mutableStateOf(false) }
    var pendingSortOption by remember { mutableStateOf<SortOption?>(null) }

    Column(modifier = modifier) {
        // Info banner when sorting without all items
        if (currentSortOption != SortOption.NONE && !allItemsLoaded) {
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
                            imageVector = AppIcons.Notifications,
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

        // Sort selection row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Sort by:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            TextButton(
                onClick = { expanded = true }
            ) {
                Icon(
                    imageVector = AppIcons.Sort,
                    contentDescription = "Sort",
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = currentSortOption.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 8.dp)
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
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
                                expanded = false
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

    // Load all items confirmation dialog
    if (showLoadAllDialog && pendingSortOption != null) {
        AlertDialog(
            onDismissRequest = {
                showLoadAllDialog = false
                pendingSortOption = null
            },
            icon = {
                Icon(
                    imageVector = AppIcons.Sort,
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
                        showLoadAllDialog = false
                        val option = pendingSortOption
                        pendingSortOption = null
                        if (option != null) {
                            onLoadAllItems()
                            onSortOptionChange(option)
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
