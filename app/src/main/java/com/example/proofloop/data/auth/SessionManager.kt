package com.example.proofloop.data.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.proofloop.domain.models.UserSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "proofloop_session_prefs")

class SessionManager(private val context: Context) {

    companion object {
        private val KEY_AUTH_TOKEN = stringPreferencesKey("auth_token")
        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        private val KEY_USER_NAME = stringPreferencesKey("user_name")
        private val KEY_IS_DEMO = booleanPreferencesKey("is_demo_user")
        private val KEY_REMEMBER_ME = booleanPreferencesKey("remember_me")
        private val KEY_SAVED_IDENTIFIER = stringPreferencesKey("saved_identifier")
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")

        @Volatile
        private var INSTANCE: SessionManager? = null

        fun getInstance(context: Context): SessionManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SessionManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[KEY_IS_LOGGED_IN] ?: false
        }

    val sessionFlow: Flow<UserSession?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            val isLoggedIn = preferences[KEY_IS_LOGGED_IN] ?: false
            if (!isLoggedIn) {
                null
            } else {
                UserSession(
                    userId = preferences[KEY_USER_ID] ?: "user_default",
                    name = preferences[KEY_USER_NAME] ?: "Student",
                    email = preferences[KEY_USER_EMAIL] ?: "",
                    isDemoUser = preferences[KEY_IS_DEMO] ?: false
                )
            }
        }

    val authTokenFlow: Flow<String?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[KEY_AUTH_TOKEN]
        }

    val rememberMeFlow: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[KEY_REMEMBER_ME] ?: false
        }

    val savedIdentifierFlow: Flow<String?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[KEY_SAVED_IDENTIFIER]
        }

    suspend fun saveSession(
        token: String,
        user: UserSession,
        rememberMe: Boolean,
        identifier: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[KEY_AUTH_TOKEN] = token
            preferences[KEY_USER_ID] = user.userId
            preferences[KEY_USER_EMAIL] = user.email
            preferences[KEY_USER_NAME] = user.name
            preferences[KEY_IS_DEMO] = user.isDemoUser
            preferences[KEY_IS_LOGGED_IN] = true
            preferences[KEY_REMEMBER_ME] = rememberMe
            if (rememberMe) {
                preferences[KEY_SAVED_IDENTIFIER] = identifier
            } else {
                preferences.remove(KEY_SAVED_IDENTIFIER)
            }
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            val rememberMe = preferences[KEY_REMEMBER_ME] ?: false
            val savedIdentifier = preferences[KEY_SAVED_IDENTIFIER]

            preferences.remove(KEY_AUTH_TOKEN)
            preferences.remove(KEY_USER_ID)
            preferences.remove(KEY_USER_EMAIL)
            preferences.remove(KEY_USER_NAME)
            preferences.remove(KEY_IS_DEMO)
            preferences[KEY_IS_LOGGED_IN] = false

            if (rememberMe && savedIdentifier != null) {
                preferences[KEY_SAVED_IDENTIFIER] = savedIdentifier
                preferences[KEY_REMEMBER_ME] = true
            } else {
                preferences.remove(KEY_SAVED_IDENTIFIER)
                preferences.remove(KEY_REMEMBER_ME)
            }
        }
    }
}
