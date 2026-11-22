package com.nocteln.app.ui.home

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.minutes


const val TIMER_MAX_MINUTES = 60
const val TIMER_STEP_MINUTES = 1

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var timer: CountDownTimer? = null

    fun startTimer() {

        _uiState.update { currentState ->
            currentState.copy(
                remainingSeconds = currentState.minutes.minutes.inWholeSeconds,
                isStarted = true
            )
        }

        timer = object : CountDownTimer(
            uiState.value.minutes.minutes.inWholeMilliseconds,
            1000
        ) {

            override fun onTick(millisUntilFinished: Long) {
                _uiState.update { currentState ->
                    currentState.copy(
                        remainingSeconds = (millisUntilFinished + 999) / 1000
                    )
                }
            }

            override fun onFinish() {
                _uiState.update { currentState ->
                    currentState.copy(
                        isStarted = false,
                        minutes = 25
                    )
                }
            }
        }.start()
    }

    fun onSliderChange(fraction: Float) {
        _uiState.update { currentState ->
            currentState.copy(
                minutes =
                    ((fraction * TIMER_MAX_MINUTES / TIMER_STEP_MINUTES).roundToInt() * TIMER_STEP_MINUTES).coerceIn(
                        TIMER_STEP_MINUTES,
                        TIMER_MAX_MINUTES
                    )
            )
        }

    }


}