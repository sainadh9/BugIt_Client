package com.bugit.bugit.local

import androidx.datastore.preferences.core.stringPreferencesKey
import com.bugit.bugit.utils.Constants
import kotlinx.coroutines.flow.Flow


val NEW_TAB = stringPreferencesKey(Constants.NEW_TAB)
val TOKEN = stringPreferencesKey(Constants.TOKEN)

interface BugPref {
    fun getLatestTab(): Flow<String>

    suspend fun saveLatestTab(name: String)

    suspend fun saveOauthToken(token: String)

    fun getLatestToken(): Flow<String>

}