package lt.vitalijus.cmp_custom_pagination.presentation.products.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import lt.vitalijus.cmp_custom_pagination.domain.model.SortOption
import kotlin.time.Duration.Companion.seconds

/**
 * Unified toolbar combining View mode and Sort controls in a single row.
 *
 * Features:
 * - View selector (Grid/List) on the left
 * - Sort selector on the right
 * - Manual refresh button and last sync timestamp
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
 * @param onManualRefresh Callback for manual refresh
 * @param lastSyncTimestamp Last sync timestamp in milliseconds (optional)
 * @param showSyncInfo Whether to show sync info and refresh button (default false)
 * @param isRefreshing Whether refresh is in progress (for rotating icon animation)
 * @param syncIntervalMs Auto-refresh interval in milliseconds (null = manual only)
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
    onManualRefresh: () -> Unit = {},
    lastSyncTimestamp: Long? = null,
    showSyncInfo: Boolean = false,
    isRefreshing: Boolean = false,
    syncIntervalMs: Long? = null,
    showLayoutToggle: Boolean = true,
    modifier: Modifier = Modifier
) {
    var sortMenuExpanded by remember { mutableStateOf(false) }
    var showLoadAllDialog by remember { mutableStateOf(false) }
    var pendingSortOption by remember { mutableStateOf<SortOption?>(null) }

    // Reactive timestamp updates - refreshes every 10 seconds
    var currentTime by remember { mutableStateOf(lt.vitalijus.cmp_custom_pagination.core.utils.currentTimeMillis()) }

    LaunchedEffect(showSyncInfo, lastSyncTimestamp) {
        if (showSyncInfo && lastSyncTimestamp != null) {
            println("⏱️ ProductsToolbar: Starting timestamp update timer")
            while (true) {
                currentTime = lt.vitalijus.cmp_custom_pagination.core.utils.currentTimeMillis()
                val elapsed = currentTime - lastSyncTimestamp
                val minutes = elapsed / 60000
                val seconds = (elapsed % 60000) / 1000
                println("⏱️ Toolbar timer tick: currentTime=$currentTime, elapsed=${minutes}m ${seconds}s")
                delay(10.seconds) // Update every 10 seconds
            }
        } else {
            println("⏱️ ProductsToolbar: Timer NOT started (showSyncInfo=$showSyncInfo, lastSyncTimestamp=$lastSyncTimestamp)")
        }
    }

    Column(modifier = modifier) {
        // Sync info row (Last sync timestamp + Refresh button)
        if (showSyncInfo) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Use currentTime to trigger recomposition when time updates
                val formattedTime = remember(lastSyncTimestamp, currentTime) {
                    val result = lt.vitalijus.cmp_custom_pagination.core.utils.formatRelativeTime(
                        lastSyncTimestamp
                    )
                    println("⏱️ Recalculating formattedTime: lastSync=$lastSyncTimestamp, currentTime=$currentTime -> '$result'")
                    result
                }

                // Calculate time until next auto-refresh
                val nextRefreshInfo = remember(lastSyncTimestamp, currentTime, syncIntervalMs) {
                    println("🔍 Countdown calc: syncIntervalMs=$syncIntervalMs, lastSync=$lastSyncTimestamp, currentTime=$currentTime")

                    // Show countdown if:
                    // 1. We have a sync interval set
                    // 2. It's not manual only (Long.MAX_VALUE)
                    // 3. It's not real-time (0L)
                    // 4. We have a last sync timestamp
                    if (syncIntervalMs != null &&
                        syncIntervalMs > 0L &&
                        syncIntervalMs < Long.MAX_VALUE &&
                        lastSyncTimestamp != null
                    ) {

                        val elapsed = currentTime - lastSyncTimestamp
                        val remaining = (syncIntervalMs - elapsed).coerceAtLeast(0)
                        val minutes = remaining / 60000
                        val seconds = (remaining % 60000) / 1000

                        val result = when {
                            remaining == 0L -> "refreshing now"
                            minutes > 0 -> "next in ${minutes}m ${seconds}s"
                            else -> "next in ${seconds}s"
                        }
                        println("🔍 Countdown result: '$result' (remaining=${remaining}ms)")
                        result
                    } else {
                        println("🔍 Countdown: null (syncIntervalMs=$syncIntervalMs, lastSync=$lastSyncTimestamp)")
                        null // Manual only, real-time, or no interval set
                    }
                }

                Column {
                    Text(
                        text = "Last sync: $formattedTime",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Show countdown to next auto-refresh if applicable
                    if (nextRefreshInfo != null) {
                        Text(
                            text = "Auto-refresh: $nextRefreshInfo",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    }
                }

                // Rotating refresh icon animation
                val infiniteTransition = rememberInfiniteTransition(label = "refresh_rotation")
                val rotationAngle by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 1000,
                            easing = LinearEasing
                        ),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "refresh_rotation_angle"
                )

                TextButton(
                    onClick = onManualRefresh,
                    enabled = !isRefreshing // Disable button while refreshing
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = if (isRefreshing) "Refreshing..." else "Refresh",
                        tint = if (isRefreshing) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier
                            .size(18.dp)
                            .then(
                                if (isRefreshing) {
                                    Modifier.rotate(rotationAngle)
                                } else {
                                    Modifier
                                }
                            )
                    )
                    Text(
                        text = if (isRefreshing) "Refreshing..." else "Refresh",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isRefreshing) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }

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
