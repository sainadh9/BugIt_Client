package com.bugit.bugit.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class BugPrefImplementation(private val dataStore: DataStore<Preferences>) : BugPref {

    override fun getLatestTab(): Flow<String> {
        return dataStore.data.catch {
            emit(emptyPreferences())
        }.map {
            it[NEW_TAB] ?: ""
        }
    }

    override suspend fun saveLatestTab(name: String) {
        dataStore.edit {
            it[NEW_TAB] = name
        }
    }

    override fun getLatestToken(): Flow<String> {
        return dataStore.data.catch {
            emit(emptyPreferences())
        }.map {
            it[TOKEN] ?: ""
        }
    }

    override suspend fun saveOauthToken(token: String) {
        dataStore.edit {
            it[TOKEN] = token
        }
    }


}