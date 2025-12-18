package lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.nav3

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.ui.graphics.vector.ImageVector
import cmpcustompagination.composeapp.generated.resources.Res
import cmpcustompagination.composeapp.generated.resources.basket
import cmpcustompagination.composeapp.generated.resources.favorite
import cmpcustompagination.composeapp.generated.resources.orders
import cmpcustompagination.composeapp.generated.resources.products
import cmpcustompagination.composeapp.generated.resources.settings
import org.jetbrains.compose.resources.StringResource

data class BottomNavItem(
    val icon: ImageVector,
    val titleRes: StringResource,
)

val TOP_LEVEL_DESTINATIONS = mapOf(
    Route.Products to BottomNavItem(
        icon = Icons.Outlined.GridView,
        titleRes = Res.string.products
    ),
    Route.Basket to BottomNavItem(
        icon = Icons.Outlined.ShoppingBasket,
        titleRes = Res.string.basket
    ),
    Route.Favorites to BottomNavItem(
        icon = Icons.Outlined.Favorite,
        titleRes = Res.string.favorite
    ),
    Route.Orders to BottomNavItem(
        icon = Icons.Outlined.Checklist,
        titleRes = Res.string.orders
    ),
    Route.Settings to BottomNavItem(
        icon = Icons.Outlined.Settings,
        titleRes = Res.string.settings
    ),
)
