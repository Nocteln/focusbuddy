package com.nocteln.app.ui.home

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nocteln.app.R
import com.nocteln.app.data.buddy.Buddy
import com.nocteln.app.data.buddy.MAX_BUDDY_HEALTH
import com.nocteln.app.ui.theme.PlantFocusTheme

// --- Stateful entry point: reads the shared HomeViewModel ---

@Composable
fun EndSessionScreen(
    viewModel: HomeViewModel,
    coinsEarned: Int,          // TODO: real value once coins exist (WalletRepository)
    onBackHome: () -> Unit,
    onNewSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val buddy by viewModel.buddy.collectAsStateWithLifecycle()

    EndSessionContent(
        minutes = uiState.minutes,
        buddy = buddy,
        coinsEarned = coinsEarned,
        healthBonus = END_SESSION_HEALTH_BONUS,
        onBackHome = onBackHome,
        onNewSession = onNewSession,
        modifier = modifier
    )
}

// --- Stateless UI: plain values only, so previews work ---

@Composable
fun EndSessionContent(
    minutes: Int,
    buddy: Buddy,
    coinsEarned: Int,
    healthBonus: Int,
    onBackHome: () -> Unit,
    onNewSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // "Session complete" badge
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.end_session_badge, minutes),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        Text(
            text = stringResource(R.string.end_session_title),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = stringResource(R.string.end_session_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 6.dp)
        )

        // The buddy in its circle, fading with its health like on Home
        Box(
            modifier = Modifier
                .padding(top = 20.dp)
                .size(220.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.plant),
                contentDescription = stringResource(R.string.end_session_buddy_description),
                modifier = Modifier
                    .size(160.dp)
                    .alpha(0.45f + 0.55f * buddy.health / MAX_BUDDY_HEALTH)
            )
        }

        // Rewards card
        Card(
            modifier = Modifier
                .padding(top = 22.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
            )
        ) {
            Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.end_session_total, coinsEarned),
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.end_session_coins),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                StatRow(
                    label = pluralStringResource(R.plurals.end_session_focus_minutes, minutes, minutes),
                    value = stringResource(R.string.end_session_total, coinsEarned)
                )
                StatRow(
                    label = stringResource(R.string.end_session_mood_bonus),
                    value = stringResource(R.string.end_session_total, healthBonus),
                    valueColor = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Pushes the buttons to the bottom of the screen
        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onBackHome,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text(stringResource(R.string.end_session_back_home))
        }
        TextButton(
            onClick = onNewSession,
            modifier = Modifier
                .padding(top = 6.dp)
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(stringResource(R.string.end_session_new_session))
        }
    }
}

// A "label on the left, value on the right" row
@Composable
private fun StatRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(text = value, style = MaterialTheme.typography.titleMedium, color = valueColor)
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun EndSessionPreview() {
    PlantFocusTheme {
        EndSessionContent(
            minutes = 45,
            buddy = Buddy(health = 90),
            coinsEarned = 45,
            healthBonus = 10,
            onBackHome = {},
            onNewSession = {}
        )
    }
}