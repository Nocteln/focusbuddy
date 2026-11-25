package com.nocteln.app.ui

import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.nocteln.app.MainApplication
import com.nocteln.app.ui.home.HomeViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(mainApplication().buddyRepository)
        }
    }
}

fun CreationExtras.mainApplication(): MainApplication = this[APPLICATION_KEY] as MainApplication