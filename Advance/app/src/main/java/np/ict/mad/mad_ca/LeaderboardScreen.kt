package np.ict.mad.mad_ca

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import np.ict.mad.mad_ca.data.AppDatabase
import np.ict.mad.mad_ca.data.LeaderboardEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    currentUserId: Int,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }
    var leaderboardList by remember { mutableStateOf(emptyList<LeaderboardEntry>()) }

    LaunchedEffect(Unit) {
        leaderboardList = db.appDao().getLeaderboard()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Leaderboard") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Rank", style = MaterialTheme.typography.titleMedium)
                Text("Player", style = MaterialTheme.typography.titleMedium)
                Text("Score", style = MaterialTheme.typography.titleMedium)
            }
            HorizontalDivider()

            // List
            LazyColumn {
                itemsIndexed(leaderboardList) { index, entry ->
                    val isCurrentUser = entry.userId == currentUserId

                    val backgroundColor = if (isCurrentUser) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        Color.Transparent
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(backgroundColor)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("#${index + 1}")
                        Text(
                            text = if (isCurrentUser) "${entry.username} (You)" else entry.username,
                            fontWeight = if (isCurrentUser) FontWeight.Bold else FontWeight.Normal
                        )
                        Text(entry.maxScore.toString(), fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider()
                }
            }
        }
    }
}