package np.ict.mad.mad_ca

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

import android.content.Context
import android.content.SharedPreferences

class HighScoreManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("wackamole_prefs", Context.MODE_PRIVATE)

    fun getHighScore(): Int {
        // Read stored high score, default to 0
        return prefs.getInt("high_score", 0)
    }

    fun updateHighScore(newScore: Int) {
        val currentHigh = getHighScore()
        // Only update if new score is higher
        if (newScore > currentHigh) {
            prefs.edit().putInt("high_score", newScore).apply()
        }
    }
}

@Composable
fun GameScreen(
    highScoreManager: HighScoreManager,
    onNavigateToSettings: () -> Unit
) {
    // --- State Management ---
    var score by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(30) }
    var moleIndex by remember { mutableIntStateOf(-1) }
    var isRunning by remember { mutableStateOf(false) }
    var showGameOver by remember { mutableStateOf(false) }
    var highScore by remember { mutableIntStateOf(highScoreManager.getHighScore()) }

    // --- Timer Logic (1 second interval) ---
    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (timeLeft > 0) {
                delay(1000L)
                timeLeft--
            }
            isRunning = false
            showGameOver = true
            moleIndex = -1
            if (score > highScore) {
                highScoreManager.updateHighScore(score)
                highScore = highScoreManager.getHighScore()
            }
        }
    }

    // --- Mole Movement Logic ---
    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (timeLeft > 0) {
                delay(Random.nextLong(700, 1001))
                moleIndex = Random.nextInt(9)
            }
        }
    }

    // --- UI Layout ---
    Column(modifier = Modifier.fillMaxSize()) {

        Surface(
            shadowElevation = 3.dp,
            modifier = Modifier.fillMaxWidth().height(64.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Wack-a-Mole",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                IconButton(onClick = onNavigateToSettings) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Main Content Area
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Global High Score: $highScore",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text("Score: $score", style = MaterialTheme.typography.headlineSmall)
                Text("Time: $timeLeft", style = MaterialTheme.typography.headlineSmall)
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.size(320.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(9) { index ->
                    MoleButton(
                        isMole = (index == moleIndex),
                        onClick = {
                            if (isRunning && index == moleIndex) {
                                score++
                                moleIndex = -1
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    score = 0
                    timeLeft = 30
                    showGameOver = false
                    isRunning = true
                    moleIndex = -1
                },
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Text(if (isRunning) "Restart" else "Start Game")
            }

            if (showGameOver) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Game Over! Score: $score",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }
    }
}

@Composable
fun MoleButton(isMole: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(90.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isMole) Color.Red else Color.LightGray
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        if (isMole) Text("M")
    }
}