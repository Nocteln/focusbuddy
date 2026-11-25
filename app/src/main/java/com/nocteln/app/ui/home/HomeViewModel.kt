package com.nocteln.app.ui.home

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds


const val TIMER_MAX_MINUTES = 60
const val TIMER_STEP_MINUTES = 1
const val DEFAULT_STARTING_MINUTES = 1


class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var timer: CountDownTimer? = null

    private fun endSession() {
        _uiState.update { currentState ->
            currentState.copy(
                isStarted = false,
                remainingSeconds = 0
            )
        }

        timer?.cancel()

        timer = null
    }

    private fun createTimer(seconds: Long): CountDownTimer
    {
        return object : CountDownTimer(
            seconds.seconds.inWholeMilliseconds,
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
                endSession()

                _uiState.update { cur ->
                    cur.copy(
                        sessionEnded = true
                    )
                }


            }
        }
    }

    fun startSession() {
        if (uiState.value.isStarted)
            return

        _uiState.update { currentState ->
            currentState.copy(
                remainingSeconds = currentState.minutes.minutes.inWholeSeconds,
                isStarted = true,
                sessionEnded = false
            )
        }

        timer = createTimer(uiState.value.minutes.minutes.inWholeSeconds)
        timer?.start()
    }

    fun onSliderChange(fraction: Float) {
        if (uiState.value.isStarted)
            return

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

    fun giveUpSession() {
        if (!uiState.value.isStarted)
            return

        // TODO: penaliser
        endSession()
    }
    
    fun stopSession() {
        if (!uiState.value.isStarted)
            return
        // TODO: add error notification

       endSession()
    }

    override fun onCleared() {
        super.onCleared()

        timer?.cancel()
    }

    fun resetSession() {
        _uiState.update { cur ->
            cur.copy(
                sessionEnded = false
            )
        }
    }

}