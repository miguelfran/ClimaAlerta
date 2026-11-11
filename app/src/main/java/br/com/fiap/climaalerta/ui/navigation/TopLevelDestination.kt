package br.com.fiap.climaalerta.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.TravelExplore
import androidx.compose.ui.graphics.vector.ImageVector
import br.com.fiap.climaalerta.R

enum class TopLevelDestination(
    val route: Any,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @param:StringRes val labelRes: Int
) {
    HOME(HomeRoute, Icons.Filled.Dashboard, Icons.Outlined.Dashboard, R.string.nav_home),
    SEARCH(SearchRoute, Icons.Filled.TravelExplore, Icons.Outlined.TravelExplore, R.string.nav_search),
    ALERTS(AlertsRoute, Icons.Filled.NotificationsActive, Icons.Outlined.NotificationsActive, R.string.nav_alerts),
    GUIDE(GuideRoute, Icons.Filled.HealthAndSafety, Icons.Outlined.HealthAndSafety, R.string.nav_guide)
}
