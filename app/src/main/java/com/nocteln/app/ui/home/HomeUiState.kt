package com.nocteln.app.ui.home

data class HomeUiState(
    val minutes: Int = DEFAULT_STARTING_MINUTES,
    val remainingSeconds: Long = 0,
    val isStarted: Boolean = false,
    val isPaused: Boolean = false,
    val sessionEnded: Boolean = false
)