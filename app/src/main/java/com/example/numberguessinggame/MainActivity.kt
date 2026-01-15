package com.example.numberguessinggame

import android.Manifest
import android.app.Application
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                    GameScreen(viewModel = gameViewModel)
                }
            }
        }
    }
}

@Composable
fun GameScreen(viewModel: GameViewModel) {
    val userGuess = viewModel.userGuess
    val gameState = viewModel.uiState

    val animatedScale by animateFloatAsState(
        targetValue = if (gameState.gameWon) 1.2f else 1.0f,
        animationSpec = tween(durationMillis = 500),
        label = "scaleAnimation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // GÓRNA CZĘŚĆ: Rekord
        Text(
            text = "High Score: ${if (gameState.highScore == Int.MAX_VALUE) "--" else gameState.highScore}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary
        )

        // Ten spacer jest teraz bardzo mały, więc nie będzie prawie wcale "wypychał" od góry.
        Spacer(Modifier.weight(0.2f))

        // ŚRODKOWA CZĘŚĆ: Główny interfejs gry
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Quiz,
                contentDescription = "Guess Icon",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Guess the Number",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = gameState.hint,
                modifier = Modifier
                    .scale(animatedScale)
                    .padding(horizontal = 16.dp),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                lineHeight = 30.sp
            )
        }

        // Dodajemy tutaj mały, stały odstęp, aby oddzielić tekst od przycisków.
        Spacer(Modifier.height(32.dp))

        // DOLNA CZĘŚĆ: Pole do wpisywania i przyciski
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(60.dp)
        ) {
            OutlinedTextField(
                value = userGuess,
                onValueChange = { viewModel.updateUserGuess(it) },
                label = { Text("Your guess") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = !gameState.gameWon,
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.secondary,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )

            if (gameState.gameWon) {
                Button(
                    onClick = { viewModel.resetGame() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text(text = "PLAY AGAIN", fontSize = 16.sp)
                }
            } else {
                Button(
                    onClick = { viewModel.handleGuess() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp),
                    enabled = userGuess.isNotBlank(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(text = "GUESS", fontSize = 16.sp)
                }
            }
        }

        // Ten spacer jest teraz bardzo duży i "wypchnie" wszystko powyżej do góry.
        Spacer(Modifier.weight(1f))
    }
}


@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    NumberGuessingGameTheme {
        // Podgląd nie ma ViewModelu, więc używamy sztucznych danych
        val fakeViewModel = GameViewModel(Application())
        GameScreen(viewModel = fakeViewModel)
    }
}