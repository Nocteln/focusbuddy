package com.nocteln.app.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Timer(uiState = uiState, viewModel = viewModel)
}

@Composable
fun Timer(
    modifier: Modifier = Modifier,
    uiState: HomeUiState,
    viewModel: HomeViewModel
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            CircularSlider(
                value = if (uiState.isStarted) uiState.remainingSeconds.toFloat() / (60f * TIMER_MAX_MINUTES) else uiState.minutes / TIMER_MAX_MINUTES.toFloat(),
                onValueChange = { fraction ->
                    viewModel.onSliderChange(fraction)
                },
                modifier = Modifier.size(260.dp),
                enabled = !uiState.isStarted
            )
            Image(painterResource(R.drawable.plant), contentDescription = null)
        }

        Text(
            text = if (uiState.isStarted) formatTime(uiState.remainingSeconds) else "${uiState.minutes} min",
            style = MaterialTheme.typography.displaySmall
        )

        if (uiState.isStarted) {
            Row() {
                Button(
                    onClick = {
                        if (uiState.isPaused) viewModel.restartSession() else viewModel.pauseSession()
                    }
                ) {
                    Text(text = if (uiState.isPaused) "resume" else "pause")
                }
                Button(
                    onClick = { viewModel.stopSession() }
                ) {
                    Text(text = "Stop")
                }
            }

        } else {
            Button(
//                    enabled = !uiState.isStarted,
                onClick = { viewModel.startSession() }
            ) {
                Text("Start session")
            }
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