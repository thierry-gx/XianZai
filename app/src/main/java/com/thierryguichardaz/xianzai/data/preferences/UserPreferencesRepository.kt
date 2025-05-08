package com.thierryguichardaz.xianzai.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey // <-- AGGIUNGI IMPORT
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Estensione per creare l'istanza di DataStore a livello di Context
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

// Valore di default se il cronotipo non è ancora stato impostato
const val DEFAULT_CHRONOTYPE = "Intermediate"

class UserPreferencesRepository(private val context: Context) {

    // Definisce le chiavi per i nostri valori
    private object PreferencesKeys {
        val IS_ONBOARDING_COMPLETE = booleanPreferencesKey("is_onboarding_complete")
        val USER_CHRONOTYPE = stringPreferencesKey("user_chronotype") // <-- CHIAVE PER CRONOTIPO
        // Potresti aggiungere chiavi per nome e età qui
        // val USER_NAME = stringPreferencesKey("user_name")
        // val USER_AGE = intPreferencesKey("user_age")
    }

    // Flow per leggere lo stato di completamento dell'onboarding
    val isOnboardingComplete: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.IS_ONBOARDING_COMPLETE] ?: false
        }

    // Funzione suspend per scrivere che l'onboarding è completo
    suspend fun setOnboardingComplete(isComplete: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.IS_ONBOARDING_COMPLETE] = isComplete
        }
    }

    // --- NUOVE FUNZIONI PER IL CRONOTIPO ---

    // Flow per leggere il cronotipo salvato
    val userChronotype: Flow<String> = context.dataStore.data
        .map { preferences ->
            // Legge la stringa del cronotipo, usa il default se non esiste
            preferences[PreferencesKeys.USER_CHRONOTYPE] ?: DEFAULT_CHRONOTYPE
        }

    // Funzione suspend per salvare il cronotipo
    suspend fun saveChronotype(chronotype: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_CHRONOTYPE] = chronotype
        }
    }

    // --- TODO: Funzioni future per salvare/leggere nome, età ---
    // suspend fun saveUserName(name: String) { ... }
    // val userName: Flow<String?> = ...
    // suspend fun saveUserAge(age: Int) { ... }
    // val userAge: Flow<Int?> = ...
}