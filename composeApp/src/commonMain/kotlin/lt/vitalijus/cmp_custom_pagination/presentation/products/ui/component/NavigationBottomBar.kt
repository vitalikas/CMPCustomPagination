package lt.vitalijus.cmp_custom_pagination.presentation.products.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
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
import lt.vitalijus.cmp_custom_pagination.presentation.products.Screen

@Composable
fun NavigationBottomBar(
    onNavigateToScreen: (Screen) -> Unit,
    currentScreen: Screen?,
    basketNotEmpty: Boolean,
    basketQuantity: Int,
    favoritesCount: Int = 0,
    ordersCount: Int = 0
) {
    val screens = listOf(Screen.ProductList, Screen.Basket, Screen.Favorites, Screen.Orders)

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.secondaryContainer
    ) {
        screens.forEach { screen ->
            val isSelected = currentScreen == screen

            val iconVector = when (screen) {
                Screen.ProductList -> AppIcons.GridView
                Screen.Basket -> AppIcons.ShoppingCart
                Screen.Favorites -> AppIcons.FavoriteBorder
                Screen.Orders -> AppIcons.ShoppingCart
                else -> AppIcons.GridView
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigateToScreen(screen) },
                icon = {
                    // Show badge for Basket if items present
                    if (screen == Screen.Basket && basketNotEmpty) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape),
                                    containerColor = Color(0xFF1B5E20)
                                ) {
                                    Text(
                                        text = basketQuantity.toString(),
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = iconVector,
                                contentDescription = screen.route,
                                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    // Show badge for Favorites if items present
                    else if (screen == Screen.Favorites && favoritesCount > 0) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape),
                                    containerColor = Color(0xFF1B5E20)
                                ) {
                                    Text(
                                        text = favoritesCount.toString(),
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        ) {
                            Icon(
                                imageVector = iconVector,
                                contentDescription = screen.route,
                                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Icon(
                            imageVector = iconVector,
                            contentDescription = screen.route,
                            tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                label = {
                    Text(
                        text = screen.route,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
