package lt.vitalijus.cmp_custom_pagination.presentation.products.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
    basketQuantity: Int
) {
    val screens = listOf(Screen.ProductList, Screen.Basket)

    NavigationBar {
        screens.forEach { screen ->
            val isSelected = currentScreen == screen

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigateToScreen(screen) },
                icon = {
                    if (screen is Screen.Basket && basketNotEmpty) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape),
                                    containerColor = Color.Red
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
                            Text(
                                text = screen.iconText,
                                fontSize = 20.sp
                            )
                        }
                    } else {
                        Text(
                            text = screen.iconText,
                            fontSize = 20.sp
                        )
                    }
                },
                label = { Text(text = screen.route) }
            )
        }
    }
}
