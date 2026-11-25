package com.nocteln.app.data.buddy

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.buddyDataStore by preferencesDataStore(name = "buddy")

