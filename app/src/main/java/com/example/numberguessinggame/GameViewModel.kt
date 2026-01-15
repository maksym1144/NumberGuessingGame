package com.example.numberguessinggame

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.random.Random


data class GameState(
    val hint: String = "I'm thinking of a number between 1 and 100.",
    val guessCount: Int = 0,
    val highScore: Int = Int.MAX_VALUE,
    val gameWon: Boolean = false
)

class GameViewModel : ViewModel() {


    private var randomNumber: Int = 0


    var uiState by mutableStateOf(GameState())
        private set


    var userGuess by mutableStateOf("")
        private set

    init {

        resetGame()
    }

    fun updateUserGuess(guess: String) {
        userGuess = guess
    }


    fun handleGuess() {
        val guessNumber = userGuess.toIntOrNull()

        if (guessNumber == null) {
            uiState = uiState.copy(hint = "Please enter a valid number.")
            return
        }

        val newGuessCount = uiState.guessCount + 1
        var newHint: String
        var newGameWon = false
        var newHighScore = uiState.highScore

        when {
            guessNumber > randomNumber -> newHint = "Hint: Too high!"
            guessNumber < randomNumber -> newHint = "Hint: Too low!"
            else -> {
                newHint = "You got it in $newGuessCount tries!"
                if (newGuessCount < uiState.highScore) {
                    newHighScore = newGuessCount
                }
                newGameWon = true
            }
        }


        uiState = uiState.copy(
            hint = newHint,
            guessCount = newGuessCount,
            highScore = newHighScore,
            gameWon = newGameWon
        )


        updateUserGuess("")
    }


    fun resetGame() {
        randomNumber = Random.nextInt(1, 101)
        uiState = GameState(highScore = uiState.highScore)
        updateUserGuess("")
    }
}