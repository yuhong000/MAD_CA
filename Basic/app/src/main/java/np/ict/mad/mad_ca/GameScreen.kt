package np.ict.mad.mad_ca


import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun GameScreen(highScoreManager: HighScoreManager, onNavigateToSettings: () -> Unit) {
    var score by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(30) }
    var moleIndex by remember { mutableIntStateOf(-1) }
    var isRunning by remember { mutableStateOf(false) }
    var highScore by remember { mutableIntStateOf(highScoreManager.getHighScore()) }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (timeLeft > 0) {
                delay(1000L)
                timeLeft--
            }
            isRunning = false
            if (score > highScore) {
                highScoreManager.updateHighScore(score)
                highScore = highScoreManager.getHighScore()
            }
        }
    }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (true) {
                delay(Random.nextLong(700, 1001))
                moleIndex = Random.nextInt(9)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier.fillMaxWidth().height(64.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Wack-a-Mole", style = MaterialTheme.typography.titleLarge)
                IconButton(onClick = onNavigateToSettings) {
                    Icon(Icons.Default.Settings, contentDescription = null)
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("High Score: $highScore")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Text("Score: $score")
                Text("Time: $timeLeft")
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.size(300.dp).padding(16.dp)
            ) {
                items(9) { index ->
                    Button(
                        onClick = { if (isRunning && index == moleIndex) score++ },
                        modifier = Modifier.height(80.dp).padding(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (index == moleIndex) Color.Red else Color.Gray
                        )
                    ) {
                        if (index == moleIndex) Text("M")
                    }
                }
            }

            Button(onClick = {
                score = 0
                timeLeft = 30
                isRunning = true
            }) {
                Text("Start Game")
            }
        }
    }
}

class HighScoreManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("game_prefs", Context.MODE_PRIVATE)

    fun getHighScore(): Int = prefs.getInt("high_score", 0)

    fun updateHighScore(newScore: Int) {
        if (newScore > getHighScore()) {
            prefs.edit().putInt("high_score", newScore).apply()
        }
    }
}