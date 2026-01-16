package com.example.numberguessinggame

import android.content.Context
import androidx.core.content.edit

class HighScoreManager(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Tryb Klasyczny
    fun getClassicHighScore(): Int {
        return prefs.getInt(KEY_CLASSIC_HIGH_SCORE, Int.MAX_VALUE)
    }

    fun saveClassicHighScore(score: Int) {
        prefs.edit { putInt(KEY_CLASSIC_HIGH_SCORE, score) }
    }

    // TODO: Dodać logikę dla Time Attack i Survival

    companion object {
        private const val PREFS_NAME = "GamePrefs"
        private const val KEY_CLASSIC_HIGH_SCORE = "classic_high_score"
        // TODO: Dodać klucze dla nowych rekordów
    }
}