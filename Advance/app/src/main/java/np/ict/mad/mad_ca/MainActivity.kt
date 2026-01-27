package np.ict.mad.mad_ca

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import np.ict.mad.mad_ca.ui.theme.MAD_CATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MAD_CATheme {
                val navController = rememberNavController()
                val context = LocalContext.current
                val scoreManager = remember { HighScoreManager(context) }

                NavHost(navController = navController, startDestination = "game") {
                    composable("game") {
                        GameScreen(
                            highScoreManager = scoreManager,
                            onNavigateToSettings = { navController.navigate("settings") }
                        )
                    }
                    composable("settings") {
                        SettingsScreen(onBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}