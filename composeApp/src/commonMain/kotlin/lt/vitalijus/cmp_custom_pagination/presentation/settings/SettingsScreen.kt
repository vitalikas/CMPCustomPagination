package lt.vitalijus.cmp_custom_pagination.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import lt.vitalijus.cmp_custom_pagination.domain.model.UserSettings
import lt.vitalijus.cmp_custom_pagination.domain.model.ViewLayoutPreference
import lt.vitalijus.cmp_custom_pagination.presentation.products.ui.component.AppIcons

@Composable
fun SettingsScreen(
    settings: UserSettings,
    onViewLayoutChange: (ViewLayoutPreference) -> Unit,
    onNotificationsChange: (Boolean) -> Unit,
    onAnalyticsChange: (Boolean) -> Unit,
    onResetToDefaults: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
            // View Layout Preference
            SettingsItem(
                title = "Default View Layout",
                subtitle = "Choose how product lists are displayed",
                icon = if (settings.viewLayoutPreference == ViewLayoutPreference.GRID) {
                    AppIcons.GridView
                } else {
                    AppIcons.ViewList
                }
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Grid Button
                    FilterChip(
                        selected = settings.viewLayoutPreference == ViewLayoutPreference.GRID,
                        onClick = { onViewLayoutChange(ViewLayoutPreference.GRID) },
                        label = { Text("Grid") },
                        leadingIcon = {
                            Icon(
                                imageVector = AppIcons.GridView,
                                contentDescription = "Grid view",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                    
                    // List Button
                    FilterChip(
                        selected = settings.viewLayoutPreference == ViewLayoutPreference.LIST,
                        onClick = { onViewLayoutChange(ViewLayoutPreference.LIST) },
                        label = { Text("List") },
                        leadingIcon = {
                            Icon(
                                imageVector = AppIcons.ViewList,
                                contentDescription = "List view",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
            }
            
            // Push Notifications
            SettingsItem(
                title = "Push Notifications",
                subtitle = "Receive notifications about orders and offers",
                icon = AppIcons.Notifications
            ) {
                Switch(
                    checked = settings.enableNotifications,
                    onCheckedChange = onNotificationsChange
                )
            }
            
            // Analytics
            SettingsItem(
                title = "Analytics",
                subtitle = "Help improve the app by sharing usage data",
                icon = AppIcons.Analytics
            ) {
                Switch(
                    checked = settings.enableAnalytics,
                    onCheckedChange = onAnalyticsChange
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Reset to Defaults
            OutlinedButton(
                onClick = onResetToDefaults,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = AppIcons.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset to Defaults")
            }
            
        // Version Info
        Text(
            text = "Version 1.0.0",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun SettingsItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    action: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            action()
        }
    }
}
