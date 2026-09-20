package com.example.proofloop.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.proofloop.domain.models.ProofAttemptRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.proofDataStore: DataStore<Preferences> by preferencesDataStore(name = "proofloop_local_proof_store")

class LocalProofStorage(private val context: Context) {
    companion object {
        private val ATTEMPTS_KEY = stringPreferencesKey("user_proof_attempts_json")
        private val json = Json { ignoreUnknownKeys = true }

        @Volatile
        private var INSTANCE: LocalProofStorage? = null

        fun getInstance(context: Context): LocalProofStorage {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LocalProofStorage(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    val attemptsFlow: Flow<List<ProofAttemptRecord>> = context.proofDataStore.data.map { prefs ->
        val raw = prefs[ATTEMPTS_KEY]
        if (!raw.isNullOrEmpty()) {
            try {
                json.decodeFromString<List<ProofAttemptRecord>>(raw)
            } catch (_: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    suspend fun saveAttempt(record: ProofAttemptRecord) {
        context.proofDataStore.edit { prefs ->
            val existingRaw = prefs[ATTEMPTS_KEY]
            val existingList = if (!existingRaw.isNullOrEmpty()) {
                try {
                    json.decodeFromString<List<ProofAttemptRecord>>(existingRaw)
                } catch (_: Exception) {
                    emptyList()
                }
            } else {
                emptyList()
            }
            val updated = listOf(record) + existingList
            prefs[ATTEMPTS_KEY] = json.encodeToString(updated)
        }
    }
}
