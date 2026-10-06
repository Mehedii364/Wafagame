package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.localization.WafaStrings
import com.example.core.navigation.Screen
import com.example.core.settings.AppLanguage
import com.example.ui.theme.*

data class NavItem(
    val route: String,
    val titleFn: (AppLanguage) -> String,
    val icon: ImageVector,
    val tag: String
)

@Composable
fun WafaBottomNav(
    currentRoute: String,
    language: AppLanguage,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        NavItem(Screen.Home.route, { WafaStrings.home(it) }, Icons.Default.Home, "nav_home"),
        NavItem(Screen.Games.route, { WafaStrings.games(it) }, Icons.Default.SportsEsports, "nav_games"),
        NavItem(Screen.Missions.route, { WafaStrings.missions(it) }, Icons.Default.Assignment, "nav_missions"),
        NavItem(Screen.Profile.route, { WafaStrings.profile(it) }, Icons.Default.Person, "nav_profile"),
        NavItem(Screen.Settings.route, { WafaStrings.settings(it) }, Icons.Default.Settings, "nav_settings")
    )

    NavigationBar(
        modifier = Modifier.testTag("wafa_bottom_nav"),
        containerColor = DarkSurface,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        onNavigate(item.route)
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.titleFn(language),
                        tint = if (isSelected) NeonEmerald else TextMuted
                    )
                },
                label = {
                    Text(
                        text = item.titleFn(language),
                        fontSize = 11.sp,
                        color = if (isSelected) NeonEmerald else TextMuted
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = DarkSurfaceVariant
                ),
                modifier = Modifier.testTag(item.tag)
            )
        }
    }
}
