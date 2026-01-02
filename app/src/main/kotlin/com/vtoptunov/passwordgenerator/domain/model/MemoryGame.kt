package com.vtoptunov.passwordgenerator.domain.model

/**
 * Memory training game where users memorize a password
 * and then select the correct one from similar options
 */
data class MemoryGame(
    val id: String,
    val correctPassword: String,
    val decoyPasswords: List<String>,
    val difficulty: GameDifficulty,
    val memorizeTimeSeconds: Int,
    val maxAttempts: Int = 3,
    val currentAttempts: Int = 0,
    val isCompleted: Boolean = false,
    val isSuccessful: Boolean = false,
    val startTime: Long = System.currentTimeMillis(),
    val xpReward: Int
)

enum class GameDifficulty(
    val displayName: String,
    val decoyCount: Int,
    val memorizeTime: Int,
    val xpReward: Int,
    val minPasswordLength: Int
) {
    EASY("Easy", 3, 10, 10, 8),
    MEDIUM("Medium", 5, 8, 25, 12),
    HARD("Hard", 7, 6, 50, 16),
    EXPERT("Expert", 9, 5, 100, 20)
}

data class GameResult(
    val isSuccess: Boolean,
    val attemptsUsed: Int,
    val timeSpentSeconds: Int,
    val xpEarned: Int,
    val difficulty: GameDifficulty,
    val password: String
)

data class GameSession(
    val game: MemoryGame,
    val phase: GamePhase,
    val remainingMemorizeTime: Int? = null,
    val selectedPassword: String? = null,
    val result: GameResult? = null
)

enum class GamePhase {
    MEMORIZING,
    SELECTING,
    RESULT
}

data class PlayerStats(
    val totalGamesPlayed: Int = 0,
    val totalGamesWon: Int = 0,
    val totalXP: Int = 0,
    val level: Int = 1,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val gamesWonByDifficulty: Map<GameDifficulty, Int> = emptyMap()
) {
    val winRate: Float
        get() = if (totalGamesPlayed > 0) {
            (totalGamesWon.toFloat() / totalGamesPlayed.toFloat()) * 100
        } else 0f
    
    val xpForNextLevel: Int
        get() = level * 100
    
    val xpProgress: Float
        get() = (totalXP % xpForNextLevel).toFloat() / xpForNextLevel.toFloat()
}

