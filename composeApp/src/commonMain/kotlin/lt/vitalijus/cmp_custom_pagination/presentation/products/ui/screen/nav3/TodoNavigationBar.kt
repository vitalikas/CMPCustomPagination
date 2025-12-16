package lt.vitalijus.cmp_custom_pagination.presentation.products.ui.screen.nav3

import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import org.jetbrains.compose.resources.stringResource

@Composable
fun TodoNavigationBar(
    selectedKey: NavKey,
    onSelectKey: (NavKey) -> Unit,
    modifier: Modifier = Modifier
) {
    BottomAppBar(
        modifier = modifier
    ) {
        TOP_LEVEL_DESTINATIONS.forEach { (routeKey, navItem) ->
            val title = stringResource(navItem.titleRes)
            NavigationBarItem(
                selected = routeKey == selectedKey,
                onClick = {
                    onSelectKey(routeKey)
                },
                icon = {
                    Icon(
                        imageVector = navItem.icon,
                        contentDescription = title
                    )
                },
                label = {
                    Text(text = title)
                }
            )
        }
    }
}
