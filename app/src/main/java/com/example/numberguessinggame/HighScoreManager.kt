package com.example.numberguessinggame

import android.content.Context
import androidx.core.content.edit

class HighScoreManager(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // --- Tryb Klasyczny ---
    fun getClassicHighScore(): Int {
        return prefs.getInt(KEY_CLASSIC_HIGH_SCORE, Int.MAX_VALUE)
    }

    fun saveClassicHighScore(score: Int) {
        prefs.edit { putInt(KEY_CLASSIC_HIGH_SCORE, score) }
    }

    // --- Tryb na Czas ---
    fun getTimeAttackBestTime(): Int {
        // Najlepszy czas to najniższa wartość
        return prefs.getInt(KEY_TIME_ATTACK_BEST_TIME, Int.MAX_VALUE)
    }

    fun saveTimeAttackBestTime(timeInSeconds: Int) {
        prefs.edit { putInt(KEY_TIME_ATTACK_BEST_TIME, timeInSeconds) }
    }

    // --- Tryb Przetrwania ---
    fun getSurvivalWinStreak(): Int {
        // Najlepsza seria to najwyższa wartość
        return prefs.getInt(KEY_SURVIVAL_WIN_STREAK, 0)
    }

    fun saveSurvivalWinStreak(streak: Int) {
        prefs.edit { putInt(KEY_SURVIVAL_WIN_STREAK, streak) }
    }

    fun resetSurvivalWinStreak() {
        prefs.edit { putInt(KEY_SURVIVAL_WIN_STREAK, 0) }
    }


    companion object {
        private const val PREFS_NAME = "GamePrefs"
        // Klucze do zapisu w pamięci
        private const val KEY_CLASSIC_HIGH_SCORE = "classic_high_score"
        private const val KEY_TIME_ATTACK_BEST_TIME = "time_attack_best_time"
        private const val KEY_SURVIVAL_WIN_STREAK = "survival_win_streak"
    }
}