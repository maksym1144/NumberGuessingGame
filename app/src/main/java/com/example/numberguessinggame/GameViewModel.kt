package com.example.numberguessinggame

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.random.Random

enum class GameMode {
    CLASSIC,
    TIME_ATTACK,
    SURVIVAL
}

data class GameState(
    val gameMode: GameMode = GameMode.CLASSIC,
    val hint: String = "Select a mode to start!",
    val guessCount: Int = 0,
    val gameWon: Boolean = false,
    val isGameActive: Boolean = false,
    val isGameOver: Boolean = false,
    val timeRemaining: Int = GameViewModel.TIME_LIMIT_SECONDS,
    val triesRemaining: Int = GameViewModel.TRIES_LIMIT,
    val classicHighScore: Int = Int.MAX_VALUE,
    val timeAttackBestTime: Int = Int.MAX_VALUE,
    val survivalBestStreak: Int = 0
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val TIME_LIMIT_SECONDS = 60
        const val TRIES_LIMIT = 7
    }

    private var randomNumber: Int = 0
    private var currentSurvivalStreak = 0
    private val highScoreManager = HighScoreManager(application)
    private val notificationHelper = NotificationHelper(application)

    var uiState by mutableStateOf(GameState())
        private set

    var userGuess by mutableStateOf("")
        private set

    private var timerJob: Job? = null

    init {

        currentSurvivalStreak = highScoreManager.getSurvivalWinStreak()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (uiState.timeRemaining > 0 && uiState.isGameActive) {
                delay(1000L)
                uiState = uiState.copy(timeRemaining = uiState.timeRemaining - 1)
                if (uiState.timeRemaining <= 0) {
                    endGame(won = false)
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
    }

    private fun endGame(won: Boolean) {
        stopTimer()
        var finalHint = ""

        if (won) {
            val newGuessCount = uiState.guessCount + 1

            when (uiState.gameMode) {
                GameMode.CLASSIC -> {
                    finalHint = "You got it in $newGuessCount tries!"
                    if (newGuessCount < uiState.classicHighScore) {
                        highScoreManager.saveClassicHighScore(newGuessCount)
                    }
                }
                GameMode.TIME_ATTACK -> {
                    val timeTaken = TIME_LIMIT_SECONDS - uiState.timeRemaining
                    finalHint = "You got it in $timeTaken seconds!"
                    if (timeTaken < uiState.timeAttackBestTime) {
                        highScoreManager.saveTimeAttackBestTime(timeTaken)
                    }
                }
                GameMode.SURVIVAL -> {
                    finalHint = "You survived!"
                    currentSurvivalStreak++
                    if (currentSurvivalStreak > uiState.survivalBestStreak) {
                        highScoreManager.saveSurvivalWinStreak(currentSurvivalStreak)
                    }
                }
            }
            notificationHelper.showGameWonNotification(newGuessCount)
        } else {

            if (uiState.gameMode == GameMode.SURVIVAL) {
                currentSurvivalStreak = 0
                highScoreManager.resetSurvivalWinStreak()
            }
            finalHint = when (uiState.gameMode) {
                GameMode.TIME_ATTACK -> "Time's Up! The number was $randomNumber."
                GameMode.SURVIVAL -> "Out of tries! The number was $randomNumber."
                else -> ""
            }
        }
        uiState = uiState.copy(isGameActive = false, isGameOver = true, hint = finalHint, gameWon = won)
    }

    fun exitGame() {
        stopTimer()
        uiState = GameState()
    }

    fun startGame(mode: GameMode) {
        randomNumber = Random.nextInt(1, 101)


        uiState = GameState(
            gameMode = mode,
            hint = "I'm thinking of a number between 1 and 100.",
            isGameActive = true,
            classicHighScore = highScoreManager.getClassicHighScore(),
            timeAttackBestTime = highScoreManager.getTimeAttackBestTime(),
            survivalBestStreak = highScoreManager.getSurvivalWinStreak()
        )
        userGuess = ""

        if (mode == GameMode.TIME_ATTACK) {
            startTimer()
        }
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
        var newTriesRemaining = uiState.triesRemaining
        if(uiState.gameMode == GameMode.SURVIVAL){
            newTriesRemaining--
        }

        val distance = abs(randomNumber - guessNumber)
        val won = distance == 0

        if (won) {
            uiState = uiState.copy(guessCount = newGuessCount)
            endGame(won = true)
        } else {
            val newHint = when {
                distance in 1..3 -> "Burning hot! You're so close!"
                distance in 4..10 -> "Getting hot! Really close!"
                distance in 11..25 -> "Warm. You're on the right track."
                distance in 26..50 -> "Cold... Try a different range."
                else -> "Freezing cold! You're far away."
            }
            if(uiState.gameMode == GameMode.SURVIVAL && newTriesRemaining <= 0){
                endGame(won = false)
            }
            uiState = uiState.copy(hint = newHint)
        }

        uiState = uiState.copy(
            guessCount = newGuessCount,
            triesRemaining = newTriesRemaining
        )
        userGuess = ""
    }

    fun resetGame() {
        startGame(uiState.gameMode)
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}