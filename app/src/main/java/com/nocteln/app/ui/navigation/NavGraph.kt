package com.nocteln.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nocteln.app.ui.Screens.HomeScreen
import com.nocteln.app.ui.Screens.SettingsScreen
import com.nocteln.app.ui.Screens.ShopScreen


enum class NavScreens(val label: String, val icon: ImageVector)
{
    Shop("Shop", Icons.Default.ShoppingCart),
    Home("Home", Icons.Default.Home),
    Settings("Settings", Icons.Default.Settings),
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NavScreens.Home.name,
        modifier = modifier
    )
    {
        composable (route = NavScreens.Home.name)
        {
            HomeScreen()
        }

        composable (route = NavScreens.Settings.name)
        {
            SettingsScreen()
        }

        composable (route = NavScreens.Shop.name)
        {
            ShopScreen()
        }
    }
}