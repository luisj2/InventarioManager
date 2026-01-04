package com.xluis.inventarioefa.data.Database.Datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.xluis.inventarioefa.utils.USER_DATASTORE_UID
import com.xluis.inventarioefa.utils.USER_PREFS_KEY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object UserDataStore {
    private val Context.dataStore by preferencesDataStore(name = USER_PREFS_KEY)
    private lateinit var appContext : Context

    private val USER_UID_KEY = stringPreferencesKey(USER_DATASTORE_UID)

    fun init(context: Context){
        appContext = context
    }

    suspend fun saveUserUid(uid: String) {
        appContext.dataStore.edit { prefs ->
            prefs[USER_UID_KEY] = uid
        }
    }

    suspend fun clearUserUid() {
        appContext.dataStore.edit { prefs ->
            prefs.remove(USER_UID_KEY)
        }
    }



    fun getUserUid(): Flow<String?> {
        return appContext.dataStore.data.map { prefs ->
            prefs[USER_UID_KEY]
        }
    }
}