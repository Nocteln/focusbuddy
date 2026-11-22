package com.nocteln.app.ui.home

data class HomeUiState(
    val minutes: Int = 25,
    val remainingSeconds: Long = 0,
    val isStarted: Boolean = false
)