package com.nocteln.app.ui.home

import com.nocteln.app.ui.theme.PlantFocusTheme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nocteln.app.R

@Composable
fun EndSessionScreen(
    focusSeeds: Int,
    healthSeeds: Int,
    streakDays: Int,
    onHarvest: () -> Unit,
    onNewSession: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Pastille "Session terminée"
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Text(
                text = "Session terminée · ${uiState.minutes} min",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }

        Text(
            text = "Belle récolte !",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(top = 14.dp)
        )

        // Image de la plante dans un cercle
        Box(
            modifier = Modifier
                .padding(top = 18.dp)
                .size(236.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.plant),
                contentDescription = "Ta plante épanouie",
                modifier = Modifier.size(180.dp)
            )
        }

        // Carte des graines gagnées
        Card(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "+${focusSeeds + healthSeeds}",
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "graines",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                StatRow(label = "${uiState.minutes} minutes de focus", value = "+$focusSeeds")
                StatRow(label = "Plante en pleine santé", value = "+$healthSeeds")
            }
        }

        Text(
            text = "$streakDays jours d'affilée, continue comme ça.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 14.dp)
        )

        // Pousse les boutons tout en bas de l'écran
        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onHarvest,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text("Récolter")
        }
        TextButton(
            onClick = onNewSession,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Nouvelle session")
        }
    }
}

// Une ligne "texte à gauche, valeur à droite"
@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(text = value, style = MaterialTheme.typography.titleMedium)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun EndSessionScreenPreview() {
    PlantFocusTheme {
        EndSessionScreen(
            focusSeeds = 10,
            healthSeeds = 5,
            streakDays = 5,
            onHarvest = {},
            onNewSession = {},
            viewModel = viewModel()
        )
    }
}