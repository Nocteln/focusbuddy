package com.nocteln.app.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nocteln.app.R
import com.nocteln.app.ui.components.CircularSlider
import com.nocteln.app.ui.theme.PlantFocusTheme

private fun formatTime(seconds: Long): String {
    val min = seconds / 60
    val sec = "${seconds % 60}".padStart(2, '0')
    return "$min:$sec"
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onSessionEnded: () -> Unit,
    viewModel: HomeViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.sessionEnded) {
        if (uiState.sessionEnded) {
            onSessionEnded()
        }
    }

    SessionTimer(
        uiState = uiState,
        onSliderChange = { frac -> viewModel.onSliderChange(frac) },
        startSession = { viewModel.startSession() },
        restartSession = { viewModel.restartSession() },
        pauseSession = { viewModel.pauseSession() },
        stopSession = { viewModel.stopSession() },
        modifier = modifier
    )
}

@Composable
fun SessionTimer(
    uiState: HomeUiState,
    onSliderChange: (Float) -> Unit,
    startSession: () -> Unit,
    restartSession: () -> Unit,
    pauseSession: () -> Unit,
    stopSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Texte d'état sous le chrono
    val status = when {
        !uiState.isStarted -> "Glisse le cercle pour choisir la durée"
        uiState.isPaused -> "En pause"
        else -> "Reste concentré, ta plante pousse"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Titre de l'écran
        Text(
            text = "Focus",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.Start)
        )

        // Espace flexible : centre le cercle et le chrono verticalement
        Spacer(modifier = Modifier.weight(1f))

        Box(contentAlignment = Alignment.Center) {
            CircularSlider(
                value = if (uiState.isStarted) uiState.remainingSeconds.toFloat() / (60f * TIMER_MAX_MINUTES) else uiState.minutes / TIMER_MAX_MINUTES.toFloat(),
                onValueChange = { fraction ->
                    onSliderChange(fraction)
                },
                modifier = Modifier.size(260.dp),
                enabled = !uiState.isStarted
            )
            Image(
                painter = painterResource(R.drawable.plant),
                contentDescription = "Ta plante",
                modifier = Modifier.size(150.dp)
            )
        }

        Text(
            text = if (uiState.isStarted) formatTime(uiState.remainingSeconds) else "${uiState.minutes} min",
            // "tnum" : chiffres de largeur égale, le chrono ne bouge plus d'un pixel à l'autre
            style = MaterialTheme.typography.displayLarge.copy(fontFeatureSettings = "tnum"),
            modifier = Modifier.padding(top = 24.dp)
        )
        Text(
            text = status,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Boutons en bas de l'écran
        if (uiState.isStarted) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { if (uiState.isPaused) restartSession() else pauseSession() },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(if (uiState.isPaused) "Reprendre" else "Pause")
                }
                Button(
                    onClick = stopSession,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Terminer")
                }
            }
        } else {
            Button(
                onClick = startSession,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text("Commencer")
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    PlantFocusTheme {
        HomeScreen(
            onSessionEnded = {},
            viewModel = viewModel()
        )
    }
}