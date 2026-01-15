package com.example.numberguessinggame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.numberguessinggame.ui.theme.NumberGuessingGameTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NumberGuessingGameTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GameScreen()
                }
            }
        }
    }
}

@Composable
fun GameScreen() {

    var userGuess by remember { mutableStateOf("") }
    var hint by remember { mutableStateOf("I'm thinking of a number between 1 and 100.") }


    val randomNumber by remember { mutableStateOf(Random.nextInt(1, 101)) }
    var guessCount by remember { mutableStateOf(0) }
    var highScore by remember { mutableStateOf(Int.MAX_VALUE) }
    var gameWon by remember { mutableStateOf(false) }



    fun handleGuess() {
        val guessNumber = userGuess.toIntOrNull()

        if (guessNumber == null) {
            hint = "Please enter a valid number."
            return
        }

        guessCount++

        when {
            guessNumber > randomNumber -> hint = "Hint: Too high!"
            guessNumber < randomNumber -> hint = "Hint: Too low!"
            else -> {
                hint = "You got it in $guessCount tries!"
                if (guessCount < highScore) {
                    highScore = guessCount
                }
                gameWon = true
            }
        }
        userGuess = ""
    }

    fun resetGame() {

    }



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

            // Tekst podpowiedzi teraz pobiera wartość ze zmiennej stanu `hint`
            Text(
                text = hint,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = userGuess, // Powiązanie wartości pola z naszym stanem
                onValueChange = { newValue ->
                    userGuess = newValue // Aktualizacja stanu przy każdej zmianie w polu
                },
                label = { Text("Your guess") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                enabled = !gameWon // Wyłącz pole tekstowe po wygranej
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { handleGuess() }, // Wywołanie naszej logiki po kliknięciu
                enabled = !gameWon // Wyłącz przycisk po wygranej
            ) {
                Text(text = "GUESS")
            }

            // TODO: Dodać przycisk "Play Again" gdy gra jest wygrana

            Spacer(modifier = Modifier.height(32.dp))

            // Wyświetlanie najlepszego wyniku
            val highScoreText = if (highScore == Int.MAX_VALUE) "--" else highScore.toString()
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
        GameScreen()
    }
}