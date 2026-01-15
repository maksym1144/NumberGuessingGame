package com.example.numberguessinggame

import android.content.Context
import androidx.core.content.edit

class HighScoreManager(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getHighScore(): Int {
        return prefs.getInt(KEY_HIGH_SCORE, Int.MAX_VALUE)
    }

    fun saveHighScore(score: Int) {
        prefs.edit {
            putInt(KEY_HIGH_SCORE, score)
        }
    }

    companion object {
        private const val PREFS_NAME = "GamePrefs"
        private const val KEY_HIGH_SCORE = "high_score"
    }
}