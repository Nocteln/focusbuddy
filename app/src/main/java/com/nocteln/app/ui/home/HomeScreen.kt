package com.nocteln.app.ui.home

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nocteln.app.R
import com.nocteln.app.data.buddy.Buddy
import com.nocteln.app.data.buddy.BuddyMood
import com.nocteln.app.data.buddy.MAX_BUDDY_HEALTH
import com.nocteln.app.ui.components.CircularSlider
import com.nocteln.app.ui.theme.PlantFocusTheme

private fun formatTime(seconds: Long): String {
    val min = seconds / 60
    val sec = "${seconds % 60}".padStart(2, '0')
    return "$min:$sec"
}

// --- Stateful entry point: reads the ViewModel, then delegates to HomeContent ---

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onSessionEnded: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenStats: () -> Unit,
    onOpenShop: () -> Unit,
    modifier: Modifier = Modifier,
    coins: Int = 0,        // TODO: real value from a WalletRepository later
    streakDays: Int = 0    // TODO: real value later
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val buddy by viewModel.buddy.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.sessionEnded) {
        if (uiState.sessionEnded) {
            onSessionEnded()
        }
    }

    HomeContent(
        uiState = uiState,
        buddy = buddy,
        coins = coins,
        streakDays = streakDays,
        onSliderChange = { fraction -> viewModel.onSliderChange(fraction) },
        onStartSession = { viewModel.startSession() },
        onGiveUpSession = { viewModel.giveUpSession() },
        onOpenSettings = onOpenSettings,
        onOpenStats = onOpenStats,
        onOpenShop = onOpenShop,
        modifier = modifier
    )
}

// --- Stateless UI: only plain values and callbacks, so previews work ---

@Composable
fun HomeContent(
    uiState: HomeUiState,
    buddy: Buddy,
    coins: Int,
    streakDays: Int,
    onSliderChange: (Float) -> Unit,
    onStartSession: () -> Unit,
    onGiveUpSession: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenStats: () -> Unit,
    onOpenShop: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HomeTopBar(
            coins = coins,
            streakDays = streakDays,
            onOpenSettings = onOpenSettings,
            onOpenStats = onOpenStats,
            onOpenShop = onOpenShop
        )

        Spacer(modifier = Modifier.weight(1f))

        // Idle: the slider picks the duration. Running: it shows the remaining time.
        val sliderValue = if (uiState.isStarted) {
            uiState.remainingSeconds.toFloat() / (60f * TIMER_MAX_MINUTES)
        } else {
            uiState.minutes / TIMER_MAX_MINUTES.toFloat()
        }

        BuddyAvatar(
            health = buddy.health,
            sliderValue = sliderValue,
            onSliderChange = onSliderChange,
            sliderEnabled = !uiState.isStarted
        )

        Text(
            text = if (uiState.isStarted) formatTime(uiState.remainingSeconds) else stringResource(R.string.home_minutes, uiState.minutes),
            // "tnum": equal-width digits, so the countdown doesn't wiggle
            style = MaterialTheme.typography.displayLarge.copy(fontFeatureSettings = "tnum"),
            modifier = Modifier.padding(top = 16.dp)
        )

        if (uiState.isStarted) {
            Text(
                text = stringResource(R.string.home_stay_focused),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        } else {
            BuddyStatus(buddy = buddy)
        }

        Spacer(modifier = Modifier.weight(1f))

        if (uiState.isStarted) {
            OutlinedButton(
                onClick = onGiveUpSession,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(stringResource(R.string.home_give_up))
            }
            Text(
                text = stringResource(R.string.home_give_up_warning),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        } else {
            Button(
                onClick = onStartSession,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text(stringResource(R.string.home_start_session))
            }
        }
    }
}

/** Top bar: settings on the left, streak (→ stats) in the middle, coins (→ shop) on the right. */
@Composable
private fun HomeTopBar(
    coins: Int,
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
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            IconButton(onClick = onOpenSettings) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = stringResource(R.string.home_settings),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Pill(
            text = stringResource(R.string.home_streak, streakDays),
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            onClick = onOpenStats
        )

        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
            Pill(
                text = stringResource(R.string.home_coins, coins),
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                onClick = onOpenShop
            )
        }
    }
}

/** Clickable pill (48 dp high = minimum recommended touch target). */
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

/**
 * The buddy inside the circular slider. It fades as its health drops.
 * The slider is only draggable when no session is running.
 */
@Composable
private fun BuddyAvatar(
    health: Int,
    sliderValue: Float,
    onSliderChange: (Float) -> Unit,
    sliderEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        CircularSlider(
            value = sliderValue,
            onValueChange = onSliderChange,
            modifier = Modifier.size(260.dp),
            enabled = sliderEnabled
        )
        Image(
            painter = painterResource(R.drawable.plant),
            contentDescription = stringResource(R.string.home_buddy_description),
            modifier = Modifier
                .size(150.dp)
                // 45 % opacity when neglected, 100 % at full health
                .alpha(0.45f + 0.55f * health / MAX_BUDDY_HEALTH)
        )
    }
}

/** Mood title, short sentence and health gauge, shown when no session is running. */
@Composable
private fun BuddyStatus(buddy: Buddy, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(top = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(moodTitle(buddy.mood)),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Text(
            text = stringResource(moodLine(buddy.mood)),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )

        Column(modifier = Modifier
            .padding(top = 16.dp)
            .width(200.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.home_mood_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = stringResource(R.string.home_health_percent, buddy.health),
                    style = MaterialTheme.typography.labelMedium
                )
            }
            LinearProgressIndicator(
                progress = { buddy.health / MAX_BUDDY_HEALTH.toFloat() },
                modifier = Modifier
                    .padding(top = 6.dp)
                    .fillMaxWidth()
                    .height(8.dp),
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round
            )
        }
    }
}

// UI texts per mood: return a string resource id, resolved with stringResource()
@StringRes
private fun moodTitle(mood: BuddyMood): Int = when (mood) {
    BuddyMood.THRIVING -> R.string.mood_title_thriving
    BuddyMood.HAPPY -> R.string.mood_title_happy
    BuddyMood.NEUTRAL -> R.string.mood_title_neutral
    BuddyMood.SAD -> R.string.mood_title_sad
    BuddyMood.NEGLECTED -> R.string.mood_title_neglected
}

@StringRes
private fun moodLine(mood: BuddyMood): Int = when (mood) {
    BuddyMood.THRIVING -> R.string.mood_line_thriving
    BuddyMood.HAPPY -> R.string.mood_line_happy
    BuddyMood.NEUTRAL -> R.string.mood_line_neutral
    BuddyMood.SAD -> R.string.mood_line_sad
    BuddyMood.NEGLECTED -> R.string.mood_line_neglected
}

// --- Previews: they use HomeContent, so no ViewModel is needed ---

@Preview(showSystemUi = true, name = "Idle")
@Composable
private fun HomeIdlePreview() {
    PlantFocusTheme {
        HomeContent(
            uiState = HomeUiState(minutes = 45),
            buddy = Buddy(health = 70),
            coins = 340,
            streakDays = 4,
            onSliderChange = {},
            onStartSession = {},
            onGiveUpSession = {},
            onOpenSettings = {},
            onOpenStats = {},
            onOpenShop = {}
        )
    }
}

@Preview(showSystemUi = true, name = "Session running")
@Composable
private fun HomeRunningPreview() {
    PlantFocusTheme {
        HomeContent(
            uiState = HomeUiState(minutes = 45, remainingSeconds = 24 * 60 + 13, isStarted = true),
            buddy = Buddy(health = 70),
            coins = 340,
            streakDays = 4,
            onSliderChange = {},
            onStartSession = {},
            onGiveUpSession = {},
            onOpenSettings = {},
            onOpenStats = {},
            onOpenShop = {}
        )
    }
}

@Preview(showSystemUi = true, name = "Sad buddy")
@Composable
private fun HomeSadPreview() {
    PlantFocusTheme {
        HomeContent(
            uiState = HomeUiState(minutes = 25),
            buddy = Buddy(health = 25),
            coins = 120,
            streakDays = 0,
            onSliderChange = {},
            onStartSession = {},
            onGiveUpSession = {},
            onOpenSettings = {},
            onOpenStats = {},
            onOpenShop = {}
        )
    }
}