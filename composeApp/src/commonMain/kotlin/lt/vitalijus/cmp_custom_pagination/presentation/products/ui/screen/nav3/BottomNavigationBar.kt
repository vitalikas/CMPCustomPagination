package lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.nav3

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import org.jetbrains.compose.resources.stringResource

@Composable
fun BottomNavigationBar(
    selectedKey: NavKey,
    onSelectKey: (NavKey) -> Unit,
    modifier: Modifier = Modifier,
    basketItemCount: Int = 0,
    favoritesItemCount: Int = 0
) {
    BottomAppBar(
        modifier = modifier,
        containerColor = Color(0xFFC8E6C9) // Light green that matches the color palette
    ) {
        TOP_LEVEL_DESTINATIONS.forEach { (routeKey, navItem) ->
            val title = stringResource(navItem.titleRes)
            val isSelected = routeKey == selectedKey
            val showBadge = (routeKey == Route.Basket && basketItemCount > 0) ||
                    (routeKey == Route.Favorites && favoritesItemCount > 0)
            val badgeCount = when (routeKey) {
                Route.Basket -> basketItemCount
                Route.Favorites -> favoritesItemCount
                else -> 0
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    onSelectKey(routeKey as NavKey)
                },
                icon = {
                    if (showBadge) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape),
                                    containerColor = Color(0xFF1B5E20)
                                ) {
                                    Text(
                                        text = badgeCount.toString(),
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = navItem.icon,
                                contentDescription = title
                            )
                        }
                    } else {
                        Icon(
                            imageVector = navItem.icon,
                            contentDescription = title
                        )
                    }
                },
                label = {
                    Text(text = title)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF4CAF50),
                    selectedTextColor = Color(0xFF4CAF50),
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
