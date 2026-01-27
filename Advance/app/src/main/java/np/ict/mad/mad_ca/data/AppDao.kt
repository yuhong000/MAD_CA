package np.ict.mad.mad_ca.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface AppDao {
    @Insert
    suspend fun insertUser(user: User): Long

    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    suspend fun login(username: String, password: String): User?

    @Query("INSERT INTO scores (userId, score, timestamp) VALUES (:userId, :score, :timestamp)")
    suspend fun insertScore(userId: Int, score: Int, timestamp: Long)

    @Query("""
        SELECT u.username, MAX(s.score) as maxScore 
        FROM users u 
        INNER JOIN scores s ON u.userId = s.userId 
        GROUP BY u.userId 
        ORDER BY maxScore DESC
    """)
    suspend fun getLeaderboard(): List<LeaderboardEntry>
}

// Helper class for the leaderboard result
data class LeaderboardEntry(
    val username: String,
    val maxScore: Int
)