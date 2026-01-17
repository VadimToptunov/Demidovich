package com.vtoptunov.passwordgenerator.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.vtoptunov.passwordgenerator.domain.model.AcademyGame
import com.vtoptunov.passwordgenerator.domain.model.GameDifficulty
import com.vtoptunov.passwordgenerator.domain.model.PlayerStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AcademyProgressDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object Keys {
        val CURRENT_LEVEL = intPreferencesKey("academy_current_level")
        val CURRENT_XP = intPreferencesKey("academy_current_xp")
        val TOTAL_XP = intPreferencesKey("academy_total_xp")
        val TOTAL_GAMES_PLAYED = intPreferencesKey("academy_total_games")
        val TOTAL_GAMES_WON = intPreferencesKey("academy_total_wins")
        val WIN_STREAK = intPreferencesKey("academy_win_streak")
        val BEST_STREAK = intPreferencesKey("academy_best_streak")
        val GAMES_WON_BY_DIFFICULTY = stringPreferencesKey("academy_wins_by_difficulty")
        val UNLOCKED_GAMES = stringPreferencesKey("academy_unlocked_games")
    }

    val playerStats: Flow<PlayerStats> = dataStore.data.map { prefs ->
        val currentLevel = prefs[Keys.CURRENT_LEVEL] ?: 1
        val currentXP = prefs[Keys.CURRENT_XP] ?: 0
        val totalXP = prefs[Keys.TOTAL_XP] ?: 0
        
        val xpForCurrentLevel = calculateXPForLevel(currentLevel)
        val xpForNextLevel = calculateXPForLevel(currentLevel + 1)
        val xpNeededForLevelUp = xpForNextLevel - xpForCurrentLevel
        val xpProgress = if (xpNeededForLevelUp > 0) {
            currentXP.toFloat() / xpNeededForLevelUp
        } else {
            0f
        }
        
        val totalGamesPlayed = prefs[Keys.TOTAL_GAMES_PLAYED] ?: 0
        val totalGamesWon = prefs[Keys.TOTAL_GAMES_WON] ?: 0
        
        val gamesWonByDifficulty = try {
            val json = prefs[Keys.GAMES_WON_BY_DIFFICULTY] ?: "{}"
            Json.decodeFromString<Map<String, Int>>(json).mapKeys { 
                GameDifficulty.valueOf(it.key)
            }
        } catch (e: Exception) {
            emptyMap()
        }

        PlayerStats(
            level = currentLevel,
            totalXP = totalXP,
            totalGamesPlayed = totalGamesPlayed,
            totalGamesWon = totalGamesWon,
            currentStreak = prefs[Keys.WIN_STREAK] ?: 0,
            bestStreak = prefs[Keys.BEST_STREAK] ?: 0,
            gamesWonByDifficulty = gamesWonByDifficulty,
            xpProgress = xpProgress
        )
    }

    val unlockedGames: Flow<Set<AcademyGame>> = dataStore.data.map { prefs ->
        try {
            val json = prefs[Keys.UNLOCKED_GAMES] ?: "[]"
            Json.decodeFromString<List<String>>(json).mapNotNull { 
                try { AcademyGame.valueOf(it) } catch (e: Exception) { null }
            }.toSet()
        } catch (e: Exception) {
            setOf(AcademyGame.MEMORY_MATCH) // Always unlocked
        }
    }

    suspend fun updateStats(
        xpEarned: Int,
        isWin: Boolean,
        difficulty: GameDifficulty
    ) {
        dataStore.edit { prefs ->
            val currentXP = (prefs[Keys.CURRENT_XP] ?: 0) + xpEarned
            val currentLevel = prefs[Keys.CURRENT_LEVEL] ?: 1
            val totalXP = (prefs[Keys.TOTAL_XP] ?: 0) + xpEarned
            val totalGamesPlayed = (prefs[Keys.TOTAL_GAMES_PLAYED] ?: 0) + 1
            val totalGamesWon = (prefs[Keys.TOTAL_GAMES_WON] ?: 0) + if (isWin) 1 else 0
            val winStreak = if (isWin) (prefs[Keys.WIN_STREAK] ?: 0) + 1 else 0
            val bestStreak = maxOf(prefs[Keys.BEST_STREAK] ?: 0, winStreak)

            var newLevel = currentLevel
            var remainingXP = currentXP
            while (true) {
                val xpForNextLevel = calculateXPForLevel(newLevel + 1) - calculateXPForLevel(newLevel)
                if (remainingXP >= xpForNextLevel) {
                    remainingXP -= xpForNextLevel
                    newLevel++
                } else {
                    break
                }
            }

            // Update wins by difficulty
            val winsByDifficulty = try {
                val json = prefs[Keys.GAMES_WON_BY_DIFFICULTY] ?: "{}"
                Json.decodeFromString<Map<String, Int>>(json).toMutableMap()
            } catch (e: Exception) {
                mutableMapOf()
            }
            
            if (isWin) {
                winsByDifficulty[difficulty.name] = (winsByDifficulty[difficulty.name] ?: 0) + 1
            }

            prefs[Keys.CURRENT_LEVEL] = newLevel
            prefs[Keys.CURRENT_XP] = remainingXP
            prefs[Keys.TOTAL_XP] = totalXP
            prefs[Keys.TOTAL_GAMES_PLAYED] = totalGamesPlayed
            prefs[Keys.TOTAL_GAMES_WON] = totalGamesWon
            prefs[Keys.WIN_STREAK] = winStreak
            prefs[Keys.BEST_STREAK] = bestStreak
            prefs[Keys.GAMES_WON_BY_DIFFICULTY] = Json.encodeToString(winsByDifficulty)
        }
    }

    suspend fun unlockGame(game: AcademyGame) {
        dataStore.edit { prefs ->
            val currentUnlocked = try {
                val json = prefs[Keys.UNLOCKED_GAMES] ?: "[]"
                Json.decodeFromString<List<String>>(json).toMutableSet()
            } catch (e: Exception) {
                mutableSetOf("MEMORY_MATCH")
            }
            
            currentUnlocked.add(game.name)
            prefs[Keys.UNLOCKED_GAMES] = Json.encodeToString(currentUnlocked.toList())
        }
    }

    suspend fun resetProgress() {
        dataStore.edit { prefs ->
            prefs.clear()
            prefs[Keys.CURRENT_LEVEL] = 1
            prefs[Keys.UNLOCKED_GAMES] = Json.encodeToString(listOf("MEMORY_MATCH"))
        }
    }

    private fun calculateXPForLevel(level: Int): Int {
        // Returns CUMULATIVE XP needed to reach this level
        // Level 1: 0 XP (starting level)
        // Level 2: 100 XP
        // Level 3: 250 XP (100 + 150)
        // Level 4: 450 XP (100 + 150 + 200)
        // Formula: sum from 1 to (level-1) of [n * 100 + (n - 1) * 50]
        if (level <= 1) return 0
        return (1 until level).sumOf { n -> n * 100 + (n - 1) * 50 }
    }
}

