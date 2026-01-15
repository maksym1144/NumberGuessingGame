package com.example.numberguessinggame

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.numberguessinggame.ui.theme.NumberGuessingGameTheme

class MainActivity : ComponentActivity() {

    private val gameViewModel by viewModels<GameViewModel>()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->

        }

    private fun askForNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        askForNotificationPermission()

        setContent {
            NumberGuessingGameTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GameScreen(
                        userGuess = gameViewModel.userGuess,
                        gameState = gameViewModel.uiState,
                        onGuessChanged = { gameViewModel.updateUserGuess(it) },
                        onGuessClicked = { gameViewModel.handleGuess() },
                        onPlayAgainClicked = { gameViewModel.resetGame() }
                    )
                }
            }
        }
    }
}

@Composable
fun GameScreen(
    userGuess: String,
    gameState: GameState,
    onGuessChanged: (String) -> Unit,
    onGuessClicked: () -> Unit,
    onPlayAgainClicked: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Guess the Number!",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = gameState.hint,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = userGuess,
                onValueChange = onGuessChanged,
                label = { Text("Your guess") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = !gameState.gameWon
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (gameState.gameWon) {
                Button(onClick = onPlayAgainClicked) {
                    Text(text = "PLAY AGAIN")
                }
            } else {
                Button(
                    onClick = onGuessClicked,
                    enabled = !gameState.gameWon
                ) {
                    Text(text = "GUESS")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            val highScoreText = if (gameState.highScore == Int.MAX_VALUE) "--" else gameState.highScore.toString()
            Text(
                text = "High Score: $highScoreText",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    NumberGuessingGameTheme {
        GameScreen(
            userGuess = "50",
            gameState = GameState(),
            onGuessChanged = {},
            onGuessClicked = {},
            onPlayAgainClicked = {}
        )
    }
}