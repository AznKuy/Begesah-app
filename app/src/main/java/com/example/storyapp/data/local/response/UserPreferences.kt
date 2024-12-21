package com.example.storyapp.data.local.response

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class UserPreferences private constructor(private val context: Context) {

    companion object {
        private val Context.dataStore by preferencesDataStore(name = "user_preferences")
        private val KEY_TOKEN = stringPreferencesKey("token_key")

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: UserPreferences? = null

        fun getInstance(context: Context): UserPreferences =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserPreferences(context).also { INSTANCE = it }
            }
    }

    val token: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_TOKEN]
        }

    suspend fun getToken(): String? {
        return token.firstOrNull()
    }

    suspend fun hasToken(): Boolean {
        return !getToken().isNullOrEmpty()
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_TOKEN] = token
        }
    }

    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(KEY_TOKEN)
        }
        Log.d("TOKEN", "Token Cleared")
    }
}

