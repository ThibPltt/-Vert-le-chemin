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
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import com.example.vertlechemin.ui.theme.data.repository.UserRepository
import com.example.vertlechemin.ui.theme.screen.login.home.HomeScreen
import com.example.vertlechemin.ui.theme.screen.login.LoginScreen
import com.example.vertlechemin.ui.theme.screen.login.register.RegisterScreen

// Navigation destinations
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object ForgotPassword : Screen("forgot_password") // nouveau
}

// ViewModel Hilt
@HiltViewModel
class NavViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    val isLoggedIn = userRepository.currentUser
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
                },
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
                }
            )
        }
    }
}
