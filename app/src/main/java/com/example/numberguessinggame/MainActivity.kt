package com.example.numberguessinggame

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.numberguessinggame.ui.theme.NumberGuessingGameTheme

class MainActivity : ComponentActivity() {

    private val gameViewModel by viewModels<GameViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NumberGuessingGameTheme {
                val gradientBrush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.background
                    )
                )
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(brush = gradientBrush)
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "mode_selection") {
                        composable("mode_selection") {
                            ModeSelectionScreen(
                                onModeSelected = { gameMode ->
                                    gameViewModel.startGame(gameMode)
                                    navController.navigate("game_screen")
                                }
                            )
                        }
                        composable("game_screen") {
                            GameScreen(
                                viewModel = gameViewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
                                    gameViewModel.exitGame()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(viewModel: GameViewModel, onNavigateBack: () -> Unit) {
    val userGuess = viewModel.userGuess
    val gameState = viewModel.uiState

    val animatedScale by animateFloatAsState(
        targetValue = if (gameState.gameWon) 1.2f else 1.0f,
        animationSpec = tween(durationMillis = 500),
        label = "scaleAnimation"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(gameState.gameMode.name.replace('_', ' ')) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopInfoBar(gameState = gameState)

            Spacer(Modifier.weight(0.2f))
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
            Spacer(Modifier.height(32.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                OutlinedTextField(
                    value = userGuess,
                    onValueChange = { viewModel.updateUserGuess(it) },
                    label = { Text("Your guess", fontSize = 18.sp) },
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 22.sp, textAlign = TextAlign.Center),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = gameState.isGameActive
                )

                if (gameState.isGameOver) {
                    Button(
                        onClick = { viewModel.resetGame() },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(text = "PLAY AGAIN", fontSize = 16.sp)
                    }
                } else {
                    Button(
                        onClick = { viewModel.handleGuess() },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = userGuess.isNotBlank() && gameState.isGameActive,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(text = "GUESS", fontSize = 16.sp)
                    }
                }
            }
            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
fun TopInfoBar(gameState: GameState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween, // Zmienione na SpaceBetween
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Lewa strona: Dynamiczny licznik
        val statusText = when (gameState.gameMode) {
            GameMode.CLASSIC -> "Tries: ${gameState.guessCount}"
            GameMode.TIME_ATTACK -> "Time: ${gameState.timeRemaining}s"
            GameMode.SURVIVAL -> "Tries Left: ${gameState.triesRemaining}"
        }
        Text(text = statusText, style = MaterialTheme.typography.bodyLarge)

        // Prawa strona: Dynamiczny rekord
        val recordText = when (gameState.gameMode) {
            GameMode.CLASSIC -> {
                val best = if (gameState.classicHighScore == Int.MAX_VALUE) "--" else gameState.classicHighScore
                "Best: $best tries"
            }
            GameMode.TIME_ATTACK -> {
                val best = if (gameState.timeAttackBestTime == Int.MAX_VALUE) "--" else gameState.timeAttackBestTime
                "Best: ${best}s"
            }
            GameMode.SURVIVAL -> {
                "Streak: ${gameState.survivalBestStreak}"
            }
        }
        Text(text = recordText, style = MaterialTheme.typography.bodyLarge)
    }
}