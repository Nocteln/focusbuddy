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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.nocteln.app.ui.home.EndSessionScreen
import com.nocteln.app.ui.home.HomeScreen
import com.nocteln.app.ui.home.HomeViewModel
import com.nocteln.app.ui.settings.SettingsScreen
import com.nocteln.app.ui.shop.ShopScreen
import com.nocteln.app.ui.stats.StatsScreen
import kotlin.math.min


enum class NavScreens(val label: String, val icon: ImageVector?)
{
    Shop("Shop", Icons.Default.ShoppingCart),
    Home("Home", Icons.Default.Home),
    Settings("Settings", Icons.Default.Settings),
    EndSession("End Session", null),
    Stats("Statistics", null)
}


@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val sessionViewModel: HomeViewModel = viewModel()

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
            HomeScreen(
               viewModel = sessionViewModel,
                onSessionEnded = {
                    navController.navigate(NavScreens.EndSession.name)
                    sessionViewModel.resetSession()
                },
                onOpenSettings = { navController.navigate(NavScreens.Settings.name)},
                onOpenShop = { navController.navigate(NavScreens.Shop.name)},
                onOpenStats = { navController.navigate(NavScreens.Stats.name)}
            )
        }

        composable (route = NavScreens.Settings.name)
        {
            SettingsScreen()
        }

        composable (route = NavScreens.Shop.name)
        {
            ShopScreen()
        }

        composable (route = NavScreens.Stats.name) {
            StatsScreen()
        }

        composable (route = NavScreens.EndSession.name)
        {
            EndSessionScreen(
                viewModel = sessionViewModel,
                focusSeeds = 10,
                healthSeeds = 5,
                streakDays = 5,
                onHarvest = { navController.popBackStack(NavScreens.Home.name, inclusive = false) },
                onNewSession = {
                    sessionViewModel.startSession()
                    navController.popBackStack(NavScreens.Home.name, inclusive = false)
                }
            )
        }
    }
}