package com.nocteln.app.ui.screens

import android.os.CountDownTimer
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nocteln.app.R
import com.nocteln.app.ui.components.CircularSlider
import com.nocteln.app.ui.theme.PlantFocusTheme
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.minutes

private const val MAX_MINUTES = 60
private const val STEP_MINUTES = 1

private fun startTimer(minutes: Int, onSec: (Long) -> Unit, onDone: () -> Unit): CountDownTimer {
    return object : CountDownTimer(
        minutes.minutes.inWholeMilliseconds,
        1000
    ) {

        override fun onTick(millisUntilFinished: Long) {
            onSec(((millisUntilFinished + 999) / 1000))
        }

        override fun onFinish() {
            onDone()
        }
    }.start()
}

private fun formatTime(seconds: Long): String {
    val min = seconds / 60
    val sec = "${seconds % 60}".padStart(2, '0')
    return "$min:$sec"
}


@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    var remainingSeconds by rememberSaveable { mutableLongStateOf(0) }
    var minutes by rememberSaveable { mutableIntStateOf(25) }
    var isStarted by rememberSaveable { mutableStateOf(false) }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            CircularSlider(
                value = if (isStarted) remainingSeconds.toFloat() / (60f * MAX_MINUTES) else minutes / MAX_MINUTES.toFloat(),
                onValueChange = { fraction ->
                    minutes =
                        ((fraction * MAX_MINUTES / STEP_MINUTES).roundToInt() * STEP_MINUTES).coerceIn(STEP_MINUTES, MAX_MINUTES)
                },
                modifier = Modifier.size(260.dp),
                enabled = !isStarted
            )
            Image(painterResource(R.drawable.plant), contentDescription = null)
        }

        Text(
            text = if (isStarted) formatTime(remainingSeconds) else "$minutes min",
            style = MaterialTheme.typography.displaySmall
        )

        Button(
            enabled = !isStarted,
            onClick = {
                remainingSeconds = minutes.minutes.inWholeSeconds
                isStarted = true
                startTimer(
                    minutes = minutes,
                    onSec = { sec -> remainingSeconds = sec },
                    onDone = { isStarted = false }
                )
            }
        ) {
            Text("Start session")
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    PlantFocusTheme {
        HomeScreen()
    }
}