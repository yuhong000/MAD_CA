package np.ict.mad.mad_ca

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import np.ict.mad.mad_ca.ui.theme.MAD_CATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MAD_CATheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "login") {

                    // 1. LOGIN SCREEN
                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = { userId ->
                                navController.navigate("game/$userId") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }

                    // 2. GAME SCREEN
                    composable(
                        route = "game/{userId}",
                        arguments = listOf(navArgument("userId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                        GameScreen(
                            context = LocalContext.current,
                            currentUserId = userId,
                            onNavigateToSettings = { navController.navigate("settings/$userId") },
                            onNavigateToLeaderboard = { navController.navigate("leaderboard/$userId") }
                        )
                    }

                    // 3. SETTINGS SCREEN
                    composable(
                        route = "settings/{userId}",
                        arguments = listOf(navArgument("userId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                        SettingsScreen(
                            currentUserId = userId,
                            onLogout = {
                                navController.navigate("login") {
                                    popUpTo(0) { inclusive = true }
                                }
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    // 4. LEADERBOARD SCREEN
                    composable(
                        route = "leaderboard/{userId}",
                        arguments = listOf(navArgument("userId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                        LeaderboardScreen(
                            currentUserId = userId,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
