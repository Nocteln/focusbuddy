package com.nocteln.app.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.annotation.StringRes
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
import com.nocteln.app.R
import com.nocteln.app.ui.AppViewModelProvider
import com.nocteln.app.ui.home.EndSessionScreen
import com.nocteln.app.ui.home.HomeScreen
import com.nocteln.app.ui.home.HomeViewModel
import com.nocteln.app.ui.settings.SettingsScreen
import com.nocteln.app.ui.shop.ShopScreen
import com.nocteln.app.ui.stats.StatsScreen
import kotlin.math.min


enum class NavScreens(@StringRes val label: Int, val icon: ImageVector?)
{
    Shop(R.string.nav_shop, Icons.Default.ShoppingCart),
    Home(R.string.nav_home, Icons.Default.Home),
    Settings(R.string.nav_settings, Icons.Default.Settings),
    EndSession(R.string.nav_end_session, null),
    Stats(R.string.nav_stats, null)
}


@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val sessionViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)

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
            SettingsScreen(
                onBack = {navController.navigate(NavScreens.Home.name)}
            )
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
                coinsEarned = 10,
                onBackHome = { navController.popBackStack(NavScreens.Home.name, inclusive = false) },
                onNewSession = {
                    sessionViewModel.startSession()
                    navController.popBackStack(NavScreens.Home.name, inclusive = false)
                }
            )
        }
    }
}