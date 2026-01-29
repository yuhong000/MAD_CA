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
                            },
                            onNavigateToSignUp = {
                                navController.navigate("signup")
                            }
                        )
                    }

                    composable("signup") {
                        SignUpScreen(
                            onSignUpSuccess = { userId ->
                                navController.navigate("game/$userId") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    // 3. GAME SCREEN
                    composable(
                        route = "game/{userId}",
                        arguments = listOf(navArgument("userId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                        GameScreen(
                            context = LocalContext.current,
                            currentUserId = userId,
                            onNavigateToSettings = { navController.navigate("settings") },
                            onNavigateToLeaderboard = { navController.navigate("leaderboard")}

                        )
                    }

                    // 4. SETTINGS SCREEN
                    composable("settings") {
                        SettingsScreen(onBack = { navController.popBackStack() })
                    }

                    composable("leaderboard") {
                        LeaderboardScreen(onBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}
