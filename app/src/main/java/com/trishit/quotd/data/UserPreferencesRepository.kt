package com.trishit.quotd.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private object PreferencesKeys {
        val LAST_QUOTE_POSITION = intPreferencesKey("last_quote_position")
    }

    val lastQuotePosition: Flow<Int> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.LAST_QUOTE_POSITION] ?: 0
        }

    suspend fun saveLastQuotePosition(position: Int) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.LAST_QUOTE_POSITION] = position
        }
    }
}
