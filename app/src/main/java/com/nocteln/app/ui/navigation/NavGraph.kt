package com.nocteln.app.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nocteln.app.ui.home.HomeScreen
import com.nocteln.app.ui.settings.SettingsScreen
import com.nocteln.app.ui.shop.ShopScreen


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
        modifier = modifier,
        enterTransition = {
            fadeIn(tween(210, delayMillis = 90)) + scaleIn(tween(210, delayMillis = 90), initialScale = 0.92f)
        },
        exitTransition = { fadeOut(tween(90)) }
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