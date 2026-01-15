package com.example.numberguessinggame
import android.app.Application
import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.numberguessinggame.ui.theme.NumberGuessingGameTheme

class MainActivity : ComponentActivity() {

    private val gameViewModel by viewModels<GameViewModel>()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

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
                        // Przekazujemy cały ViewModel, aby mieć dostęp do jego funkcji
                        viewModel = gameViewModel
                    )
                }
            }
        }
    }
}

@Composable
fun GameScreen(viewModel: GameViewModel) {
    // Pobieramy stany bezpośrednio z ViewModelu
    val userGuess = viewModel.userGuess
    val gameState = viewModel.uiState

    // Stan dla naszej nowej animacji skali
    val animatedScale by animateFloatAsState(
        targetValue = if (gameState.gameWon) 1.2f else 1.0f,
        animationSpec = tween(durationMillis = 500),
        label = "scaleAnimation"
    )

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

            // APLIKUJEMY EFEKT SKALOWANIA DO TEKSTU Z PODPOWIEDZIĄ
            Text(
                text = gameState.hint,
                modifier = Modifier.scale(animatedScale), // Tutaj dzieje się magia
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedTextField(
                value = userGuess,
                onValueChange = { viewModel.updateUserGuess(it) },
                label = { Text("Your guess") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = !gameState.gameWon
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (gameState.gameWon) {
                Button(onClick = { viewModel.resetGame() }) {
                    Text(text = "PLAY AGAIN")
                }
            } else {
                Button(
                    onClick = { viewModel.handleGuess() },
                    enabled = userGuess.isNotBlank()
                ) {
                    Text(text = "GUESS")
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            val highScoreText =
                if (gameState.highScore == Int.MAX_VALUE) "--" else gameState.highScore.toString()
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
    // Podgląd nie ma ViewModelu, więc używamy sztucznych danych
    val fakeViewModel = GameViewModel(Application())
    GameScreen(viewModel = fakeViewModel)
}