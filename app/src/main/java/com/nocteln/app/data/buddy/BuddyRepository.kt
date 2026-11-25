package com.nocteln.app.data.buddy

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BuddyRepository(private val dataStore: DataStore<Preferences>) {

     val buddy: Flow<Buddy> = dataStore.data.map { preferences ->
         val health: Int = preferences[BUDDY_HEALTH] ?: MAX_BUDDY_HEALTH
         val name: String = preferences[BUDDY_NAME] ?: DEFAULT_BUDDY_NAME

        Buddy(name = name, health = health)
    }

    suspend fun changeHealth(delta: Int)
    {
        dataStore.edit { preferences ->
            val curHealth: Int = preferences[BUDDY_HEALTH] ?: MAX_BUDDY_HEALTH
            preferences[BUDDY_HEALTH] = (curHealth + delta).coerceIn(0, MAX_BUDDY_HEALTH)
        }
    }

    companion object {
        private val BUDDY_HEALTH = intPreferencesKey("buddy_health")
        private val BUDDY_NAME = stringPreferencesKey("buddy_name")
    }
}