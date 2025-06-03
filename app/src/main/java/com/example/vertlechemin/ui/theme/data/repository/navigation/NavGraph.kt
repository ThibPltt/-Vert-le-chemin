package com.example.vertlechemin.ui.theme.data.repository.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.NavBackStackEntry
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import com.example.vertlechemin.ui.theme.data.repository.UserRepository
import com.example.vertlechemin.ui.theme.screen.login.home.HomeScreen
import com.example.vertlechemin.ui.theme.screen.login.LoginScreen
import com.example.vertlechemin.ui.theme.screen.login.favoris.FavorisScreen
import com.example.vertlechemin.ui.theme.screen.login.parameters.ParametersScreen
import com.example.vertlechemin.ui.theme.screen.login.register.RegisterScreen
import com.example.vertlechemin.ui.theme.screen.login.trajet.TrajetScreen
import com.example.vertlechemin.ui.theme.screen.login.trajet.DestinationScreen
import com.example.vertlechemin.ui.theme.screen.login.trajet.NavigationScreen
import com.example.vertlechemin.ui.theme.screen.login.trajet.FinishScreen
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.StateFlow

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object ForgotPassword : Screen("forgot_password")
    object Trajet : Screen("trajet")
    object Favoris : Screen("favoris")
    object Parameters : Screen("parameters")
    object Destination : Screen("destination")
    object Navigation : Screen("navigation/{destinationLat}/{destinationLon}") {
        fun createRoute(lat: Double, lon: Double) = "navigation/$lat/$lon"
    }
    object Finish : Screen("finish")
}

@HiltViewModel
class NavViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    val isLoggedIn: StateFlow<FirebaseUser?> = userRepository.currentUser
}

@Composable
fun AppNavigation(
    navViewModel: NavViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val isLoggedIn by navViewModel.isLoggedIn.collectAsState(initial = null)

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn != null) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigateUp()
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToTrajet = {
                    navController.navigate(Screen.Trajet.route)
                },
                onNavigateToFavoris = {
                    navController.navigate(Screen.Favoris.route)
                },
                onNavigateToParameters = {
                    navController.navigate(Screen.Parameters.route)
                }
            )
        }

        composable(Screen.Trajet.route) {
            TrajetScreen(
                onNavigateToTrajet = { navController.navigate(Screen.Trajet.route) },
                onNavigateToFavoris = { navController.navigate(Screen.Favoris.route) },
                onNavigateToParameters = { navController.navigate(Screen.Parameters.route) },
                onNavigateToDestination = { navController.navigate(Screen.Destination.route) }
            )
        }

        composable(Screen.Favoris.route) {
            FavorisScreen(
                onNavigateToTrajet = { navController.navigate(Screen.Trajet.route) },
                onNavigateToFavoris = { navController.navigate(Screen.Favoris.route) },
                onNavigateToParameters = { navController.navigate(Screen.Parameters.route) }
            )
        }

        composable(Screen.Parameters.route) {
            ParametersScreen(
                navController = navController,
                onNavigateToTrajet = { navController.navigate(Screen.Trajet.route) },
                onNavigateToFavoris = { navController.navigate(Screen.Favoris.route) },
                onNavigateToParameters = { navController.navigate(Screen.Parameters.route) }
            )
        }

        composable(Screen.Destination.route) {
            DestinationScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToFavoris = {
                    navController.navigate(Screen.Favoris.route)
                },
                onNavigateToParameters = {
                    navController.navigate(Screen.Parameters.route)
                },
                onStartRace = { destinationLat, destinationLon ->
                    navController.navigate(Screen.Navigation.createRoute(destinationLat, destinationLon))
                }
            )
        }

        composable(
            route = Screen.Navigation.route,
            arguments = listOf(
                navArgument("destinationLat") { type = NavType.StringType },
                navArgument("destinationLon") { type = NavType.StringType }
            )
        ) { backStackEntry: NavBackStackEntry ->
            val destinationLat = backStackEntry.arguments?.getString("destinationLat")?.toDoubleOrNull() ?: 0.0
            val destinationLon = backStackEntry.arguments?.getString("destinationLon")?.toDoubleOrNull() ?: 0.0

            NavigationScreen(
                destinationLat = destinationLat,
                destinationLon = destinationLon,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToFavoris = {
                    navController.navigate(Screen.Favoris.route)
                },
                onNavigateToParameters = {
                    navController.navigate(Screen.Parameters.route)
                },
                onFinishScreen = {
                    navController.navigate(Screen.Finish.route)
                }
            )
        }

        composable(Screen.Finish.route) {
            FinishScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToFavoris = {
                    navController.navigate(Screen.Favoris.route)
                },
                onNavigateToParameters = {
                    navController.navigate(Screen.Parameters.route)
                },
                onFinishScreen = {
                    navController.navigate(Screen.Finish.route)
                }
            )
        }
    }
}
