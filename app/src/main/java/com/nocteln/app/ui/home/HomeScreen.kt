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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    onOpenSettings: () -> Unit,
    onOpenStats: () -> Unit,
    onOpenShop: () -> Unit,
    viewModel: HomeViewModel,
    seeds: Int = 0,        // à remplacer par la vraie valeur (Room/DataStore) plus tard
    streakDays: Int = 0
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
        stopSession = { viewModel.stopSession() },
        giveUpSession = { viewModel.giveUpSession() },
        seeds = seeds,
        streakDays = streakDays,
        onOpenSettings = onOpenSettings,
        onOpenStats = onOpenStats,
        onOpenShop = onOpenShop,
        modifier = modifier
    )
}

/**
 * Barre du haut : réglages à gauche, série (→ stats) au centre, solde de graines (→ boutique) à droite.
 * Les 3 zones ont le même poids pour que la série reste centrée.
 */
@Composable
private fun HomeTopBar(
    seeds: Int,
    streakDays: Int,
    onOpenSettings: () -> Unit,
    onOpenStats: () -> Unit,
    onOpenShop: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Gauche
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            IconButton(onClick = onOpenSettings) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Réglages",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Centre : série de jours → statistiques
        Pill(
            text = "🔥 $streakDays j",
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            onClick = onOpenStats
        )

        // Droite : solde de graines → boutique
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
            Pill(
                text = "$seeds 🌱",
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                onClick = onOpenShop
            )
        }
    }
}

/** Pastille cliquable (48 dp de haut = taille tactile minimale recommandée). */
@Composable
private fun Pill(
    text: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = containerColor,
        contentColor = contentColor
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .heightIn(min = 48.dp)
                .padding(horizontal = 16.dp)
                .wrapContentHeight(Alignment.CenterVertically)
        )
    }
}

@Composable
fun SessionTimer(
    uiState: HomeUiState,
    onSliderChange: (Float) -> Unit,
    startSession: () -> Unit,
    giveUpSession: () -> Unit,
    stopSession: () -> Unit,
    seeds: Int,
    streakDays: Int,
    onOpenSettings: () -> Unit,
    onOpenStats: () -> Unit,
    onOpenShop: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Texte d'état sous le chrono
    val status = when {
        !uiState.isStarted -> "Glisse le cercle pour choisir la durée"
        else -> "Reste concentré, ta plante pousse"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HomeTopBar(
            seeds = seeds,
            streakDays = streakDays,
            onOpenSettings = onOpenSettings,
            onOpenStats = onOpenStats,
            onOpenShop = onOpenShop
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
            if (uiState.isStarted) {
                OutlinedButton(
                    onClick = giveUpSession,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text("Abandonner")
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
            onOpenSettings = {},
            onOpenStats = {},
            onOpenShop = {},
            viewModel = viewModel(),
            seeds = 340,
            streakDays = 4
        )
    }
}