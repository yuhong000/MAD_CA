package np.ict.mad.mad_ca

import android.content.Context
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
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import np.ict.mad.mad_ca.data.AppDatabase
import java.util.Date
import kotlin.random.Random

@Composable
fun GameScreen(
    context: Context,
    currentUserId: Int,
    onNavigateToSettings: () -> Unit
) {
    val db = remember { AppDatabase.getDatabase(context) }
    val scope = rememberCoroutineScope()

    var score by remember { mutableIntStateOf(0) }
    var timeLeft by remember { mutableIntStateOf(30) }
    var moleIndex by remember { mutableIntStateOf(-1) }
    var isRunning by remember { mutableStateOf(false) }
    var showGameOver by remember { mutableStateOf(false) }
    var personalBest by remember { mutableIntStateOf(0) }

    LaunchedEffect(currentUserId) {
        personalBest = db.appDao().getPersonalBest(currentUserId) ?: 0
    }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (timeLeft > 0) {
                delay(1000L)
                timeLeft--
            }
            isRunning = false
            showGameOver = true
            moleIndex = -1


            scope.launch {
                val timestamp = Date().time
                db.appDao().insertScore(currentUserId, score, timestamp)
                if (score > personalBest) {
                    personalBest = score
                }
            }
        }
    }

    // --- Mole Movement Logic
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Personal Best: $personalBest",
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
                    Button(
                        onClick = {
                            if (isRunning && index == moleIndex) {
                                score++
                                moleIndex = -1
                            }
                        },
                        modifier = Modifier.height(90.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (index == moleIndex) Color.Red else Color.LightGray
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        if (index == moleIndex) Text(
                            text = "🐭",
                            fontSize = 50.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (isRunning) {
                        score = 0
                        timeLeft = 0
                        showGameOver = true
                        isRunning = false
                        moleIndex = -1
                    }else{
                        score = 0
                        timeLeft = 30
                        showGameOver = false
                        isRunning = true
                        moleIndex = -1
                    }
                },
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Text(if (isRunning) "Stop Game" else "Start Game")
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