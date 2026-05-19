package com.axiom.aicoach.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.axiom.aicoach.ui.theme.AxiomTheme

data class BottomNavItem(
    val label: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

val bottomNavItems = listOf(
    BottomNavItem("Home", Screen.Dashboard.route, Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem("Train", Screen.WorkoutPlan.route, Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter),
    BottomNavItem("Fuel", Screen.NutritionDashboard.route, Icons.Filled.Restaurant, Icons.Outlined.Restaurant),
    BottomNavItem("Progress", Screen.ProgressOverview.route, Icons.Filled.TrendingUp, Icons.Outlined.TrendingUp),
    BottomNavItem("Coach", Screen.CoachChat.route, Icons.Filled.ChatBubble, Icons.Outlined.ChatBubbleOutline),
)

val bottomNavRoutes = bottomNavItems.map { it.route }

@Composable
fun AxiomBottomNavBar(navController: NavController) {
    val colors = AxiomTheme.colors
    val navBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStack?.destination?.route

    NavigationBar(
        containerColor = colors.surface,
        modifier = Modifier.navigationBarsPadding(),
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route
            val iconColor by animateColorAsState(
                targetValue = if (selected) colors.primary else colors.textMuted,
                animationSpec = tween(180),
                label = "nav_icon_color"
            )
            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Screen.Dashboard.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label,
                        tint = iconColor,
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                        color = iconColor,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = colors.primaryLight,
                ),
            )
        }
    }
}
