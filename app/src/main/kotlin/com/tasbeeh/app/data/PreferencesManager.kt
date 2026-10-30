package com.tasbeeh.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "tasbeeh_prefs")

class PreferencesManager(private val context: Context) {

    private val gson = Gson()

    companion object {
        val KEY_CURRENT_COUNT = intPreferencesKey("current_count")
        val KEY_SELECTED_DHIKR_ID = intPreferencesKey("selected_dhikr_id")
        val KEY_CUSTOM_DHIKR = stringPreferencesKey("custom_dhikr")
        val KEY_TARGET = intPreferencesKey("target")
        val KEY_IS_CUSTOM_TARGET = booleanPreferencesKey("is_custom_target")
        val KEY_CUSTOM_TARGET_VALUE = intPreferencesKey("custom_target_value")
        val KEY_VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val KEY_SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val KEY_IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        val KEY_SESSIONS = stringPreferencesKey("sessions")
    }

    val tasbeehStateFlow: Flow<TasbeehState> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { prefs ->
            val dhikrId = prefs[KEY_SELECTED_DHIKR_ID] ?: 1
            val selectedDhikr = DHIKR_LIST.find { it.id == dhikrId } ?: DHIKR_LIST[0]
            TasbeehState(
                currentCount = prefs[KEY_CURRENT_COUNT] ?: 0,
                selectedDhikr = selectedDhikr,
                customDhikr = prefs[KEY_CUSTOM_DHIKR] ?: "",
                target = prefs[KEY_TARGET] ?: 33,
                isCustomTarget = prefs[KEY_IS_CUSTOM_TARGET] ?: false,
                customTargetValue = prefs[KEY_CUSTOM_TARGET_VALUE] ?: 100,
                vibrationEnabled = prefs[KEY_VIBRATION_ENABLED] ?: true,
                soundEnabled = prefs[KEY_SOUND_ENABLED] ?: true,
                isDarkTheme = prefs[KEY_IS_DARK_THEME] ?: true
            )
        }

    val sessionsFlow: Flow<List<SessionRecord>> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences())
            else throw exception
        }
        .map { prefs ->
            val json = prefs[KEY_SESSIONS] ?: "[]"
            try {
                val type = object : TypeToken<List<SessionRecord>>() {}.type
                gson.fromJson(json, type) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }

    suspend fun saveCount(count: Int) {
        context.dataStore.edit { prefs -> prefs[KEY_CURRENT_COUNT] = count }
    }

    suspend fun saveDhikr(dhikrId: Int) {
        context.dataStore.edit { prefs -> prefs[KEY_SELECTED_DHIKR_ID] = dhikrId }
    }

    suspend fun saveCustomDhikr(text: String) {
        context.dataStore.edit { prefs -> prefs[KEY_CUSTOM_DHIKR] = text }
    }

    suspend fun saveTarget(target: Int, isCustom: Boolean, customValue: Int) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TARGET] = target
            prefs[KEY_IS_CUSTOM_TARGET] = isCustom
            prefs[KEY_CUSTOM_TARGET_VALUE] = customValue
        }
    }

    suspend fun saveVibration(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[KEY_VIBRATION_ENABLED] = enabled }
    }

    suspend fun saveSound(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[KEY_SOUND_ENABLED] = enabled }
    }

    suspend fun saveTheme(isDark: Boolean) {
        context.dataStore.edit { prefs -> prefs[KEY_IS_DARK_THEME] = isDark }
    }

    suspend fun addSession(session: SessionRecord) {
        context.dataStore.edit { prefs ->
            val json = prefs[KEY_SESSIONS] ?: "[]"
            val type = object : TypeToken<List<SessionRecord>>() {}.type
            val list: MutableList<SessionRecord> = try {
                gson.fromJson(json, type) ?: mutableListOf()
            } catch (e: Exception) {
                mutableListOf()
            }
            list.add(0, session)
            // Keep only last 200 sessions
            val trimmed = if (list.size > 200) list.take(200) else list
            prefs[KEY_SESSIONS] = gson.toJson(trimmed)
        }
    }

    suspend fun clearSessions() {
        context.dataStore.edit { prefs -> prefs[KEY_SESSIONS] = "[]" }
    }
}
