package br.com.fiap.climaalerta.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import br.com.fiap.climaalerta.ui.screens.alerts.AlertsScreen
import br.com.fiap.climaalerta.ui.screens.detail.CityDetailScreen
import br.com.fiap.climaalerta.ui.screens.guide.GuideScreen
import br.com.fiap.climaalerta.ui.screens.home.HomeScreen
import br.com.fiap.climaalerta.ui.screens.onboarding.OnboardingScreen
import br.com.fiap.climaalerta.ui.screens.search.SearchScreen
import br.com.fiap.climaalerta.ui.screens.splash.SplashDestination
import br.com.fiap.climaalerta.ui.screens.splash.SplashScreen

/**
 * Raiz da interface: Scaffold com a barra inferior (só nas telas principais) e o NavHost.
 */
@Composable
fun ClimaAlertaApp(navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val currentTopLevel = TopLevelDestination.entries.firstOrNull { currentDestination.isOn(it) }

    Scaffold(
        // As telas internas tratam seus próprios insets (cada uma tem seu Scaffold com TopAppBar).
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (currentTopLevel != null) {
                AppBottomBar(
                    currentDestination = currentTopLevel,
                    onDestinationSelected = navController::navigateToTopLevel
                )
            }
        }
    ) { innerPadding ->
        AppNavHost(
            navController = navController,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        )
    }
}

@Composable
private fun AppBottomBar(
    currentDestination: TopLevelDestination,
    onDestinationSelected: (TopLevelDestination) -> Unit
) {
    NavigationBar {
        TopLevelDestination.entries.forEach { destination ->
            val selected = destination == currentDestination
            NavigationBarItem(
                selected = selected,
                onClick = { onDestinationSelected(destination) },
                icon = {
                    Icon(
                        imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                        contentDescription = null
                    )
                },
                label = { Text(stringResource(destination.labelRes)) }
            )
        }
    }
}

@Composable
private fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = SplashRoute,
        modifier = modifier
    ) {
        composable<SplashRoute> {
            SplashScreen(
                onSplashFinished = { destination ->
                    when (destination) {
                        SplashDestination.ONBOARDING -> navController.navigate(OnboardingRoute) {
                            popUpTo<SplashRoute> { inclusive = true }
                        }
                        SplashDestination.HOME -> navController.navigate(HomeRoute) {
                            popUpTo<SplashRoute> { inclusive = true }
                        }
                    }
                }
            )
        }
        composable<OnboardingRoute> {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(HomeRoute) {
                        popUpTo<OnboardingRoute> { inclusive = true }
                    }
                }
            )
        }
        composable<HomeRoute> {
            HomeScreen(
                onCityClick = { city -> navController.navigate(CityDetailRoute.from(city)) },
                onSearchClick = { navController.navigateToTopLevel(TopLevelDestination.SEARCH) }
            )
        }
        composable<SearchRoute> {
            SearchScreen(
                onCityClick = { city -> navController.navigate(CityDetailRoute.from(city)) }
            )
        }
        composable<AlertsRoute> {
            AlertsScreen(
                onCityClick = { city -> navController.navigate(CityDetailRoute.from(city)) }
            )
        }
        composable<GuideRoute> {
            GuideScreen()
        }
        composable<CityDetailRoute> {
            CityDetailScreen(onNavigateBack = { navController.navigateUp() })
        }
    }
}

/** Navega entre abas preservando o estado de cada uma e sem empilhar telas repetidas. */
private fun NavHostController.navigateToTopLevel(destination: TopLevelDestination) {
    navigate(destination.route) {
        popUpTo<HomeRoute> { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

private fun NavDestination?.isOn(destination: TopLevelDestination): Boolean =
    this?.hierarchy?.any { it.hasRoute(destination.route::class) } == true
