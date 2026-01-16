package com.example.numberguessinggame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ModeSelectionScreen(
    onModeSelected: (GameMode) -> Unit
) {
    // ##### POCZĄTEK ZMIANY #####
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState) // Dodajemy możliwość przewijania
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // ##### KONIEC ZMIANY #####
        Text(
            text = "Number Guessing Game",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(64.dp))

        ModeButton(
            title = "Classic Mode",
            description = "Beat your best score.",
            onClick = { onModeSelected(GameMode.CLASSIC) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        ModeButton(
            title = "Time Attack",
            description = "Guess before time runs out.",
            onClick = { onModeSelected(GameMode.TIME_ATTACK) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        ModeButton(
            title = "Survival Mode",
            description = "Guess with a limited number of tries.",
            onClick = { onModeSelected(GameMode.SURVIVAL) }
        )
    }
}

@Composable
private fun ModeButton(title: String, description: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = description, fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
        }
    }
}