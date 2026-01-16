package com.example.numberguessinggame

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import kotlin.math.abs
import kotlin.random.Random

// Definiujemy możliwe tryby gry
enum class GameMode {
    CLASSIC,
    TIME_ATTACK,
    SURVIVAL
}

data class GameState(
    val gameMode: GameMode = GameMode.CLASSIC,
    val hint: String = "Select a mode to start!",
    val guessCount: Int = 0,
    val highScore: Int = Int.MAX_VALUE,
    val gameWon: Boolean = false,
    val isGameActive: Boolean = false // Czy gra się w ogóle rozpoczęła
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private var randomNumber: Int = 0
    private val highScoreManager = HighScoreManager(application)
    private val notificationHelper = NotificationHelper(application)

    var uiState by mutableStateOf(GameState())
        private set

    var userGuess by mutableStateOf("")
        private set

    // Nowa funkcja do rozpoczynania gry w konkretnym trybie
    fun startGame(mode: GameMode) {
        randomNumber = Random.nextInt(1, 101)
        val savedHighScore = highScoreManager.getHighScore() // Na razie odczytujemy tylko jeden rekord
        uiState = GameState(
            gameMode = mode,
            hint = "I'm thinking of a number between 1 and 100.",
            highScore = savedHighScore,
            isGameActive = true
        )
        userGuess = ""
    }

    fun updateUserGuess(guess: String) {
        userGuess = guess
    }

    fun handleGuess() {
        if (!uiState.isGameActive) return

        val guessNumber = userGuess.toIntOrNull()

        if (guessNumber == null) {
            uiState = uiState.copy(hint = "Please enter a valid number.")
            return
        }

        val newGuessCount = uiState.guessCount + 1
        var newHint: String
        var newGameWon = false
        var newHighScore = uiState.highScore

        val distance = abs(randomNumber - guessNumber)

        when {
            distance == 0 -> {
                newHint = "You got it in $newGuessCount tries!"
                if (newGuessCount < uiState.highScore) {
                    newHighScore = newGuessCount
                    highScoreManager.saveHighScore(newHighScore)
                }
                newGameWon = true
                notificationHelper.showGameWonNotification(newGuessCount)
            }
            distance in 1..3 -> newHint = "Burning hot! You're so close!"
            distance in 4..10 -> newHint = "Getting hot! Really close!"
            distance in 11..25 -> newHint = "Warm. You're on the right track."
            distance in 26..50 -> newHint = "Cold... Try a different range."
            else -> newHint = "Freezing cold! You're far away."
        }

        uiState = uiState.copy(
            hint = newHint,
            guessCount = newGuessCount,
            highScore = newHighScore,
            gameWon = newGameWon
        )

        userGuess = ""
    }

    fun resetGame() {
        // "Play Again" teraz po prostu restartuje grę w tym samym trybie
        startGame(uiState.gameMode)
    }
}