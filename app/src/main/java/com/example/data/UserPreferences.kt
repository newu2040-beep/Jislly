package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ui.theme.AppThemeStyle
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "jislly_preferences")

class UserPreferences(private val context: Context) {
    companion object {
        val KEY_THEME_STYLE = stringPreferencesKey("theme_style")
        val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        val KEY_USER_NAME = stringPreferencesKey("user_name")
        val KEY_USER_AGE = stringPreferencesKey("user_age")
        val KEY_USER_BIO = stringPreferencesKey("user_bio")
        val KEY_USER_AVATAR_URI = stringPreferencesKey("user_avatar_uri")
        val KEY_HAPTICS = booleanPreferencesKey("haptics_enabled")
        val KEY_COMPACT_MODE = booleanPreferencesKey("compact_mode")
        val KEY_ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        val KEY_PERMISSIONS_ASKED = booleanPreferencesKey("permissions_asked")
    }

    val themeStyleFlow: Flow<AppThemeStyle> = context.dataStore.data.map { prefs ->
        val name = prefs[KEY_THEME_STYLE] ?: AppThemeStyle.ICY_BLUE.name
        try {
            AppThemeStyle.valueOf(name)
        } catch (e: Exception) {
            AppThemeStyle.ICY_BLUE
        }
    }

    val themeModeFlow: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        val name = prefs[KEY_THEME_MODE] ?: ThemeMode.SYSTEM.name
        try {
            ThemeMode.valueOf(name)
        } catch (e: Exception) {
            ThemeMode.SYSTEM
        }
    }

    val userNameFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_NAME] ?: "JISLLY"
    }

    val userAgeFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_AGE] ?: "22"
    }

    val userBioFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_BIO] ?: "Edit your own kind of magic ♡"
    }

    val userAvatarUriFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_AVATAR_URI]
    }

    val hapticsFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_HAPTICS] ?: true
    }

    val compactModeFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_COMPACT_MODE] ?: false
    }

    val onboardingDoneFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_ONBOARDING_DONE] ?: false
    }

    val permissionsAskedFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_PERMISSIONS_ASKED] ?: false
    }

    suspend fun setThemeStyle(style: AppThemeStyle) {
        context.dataStore.edit { it[KEY_THEME_STYLE] = style.name }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[KEY_THEME_MODE] = mode.name }
    }

    suspend fun setProfile(name: String, age: String, bio: String, avatarUri: String?) {
        context.dataStore.edit {
            it[KEY_USER_NAME] = name
            it[KEY_USER_AGE] = age
            it[KEY_USER_BIO] = bio
            if (avatarUri != null) {
                it[KEY_USER_AVATAR_URI] = avatarUri
            }
        }
    }

    suspend fun setHaptics(enabled: Boolean) {
        context.dataStore.edit { it[KEY_HAPTICS] = enabled }
    }

    suspend fun setCompactMode(enabled: Boolean) {
        context.dataStore.edit { it[KEY_COMPACT_MODE] = enabled }
    }

    suspend fun setOnboardingDone(done: Boolean) {
        context.dataStore.edit { it[KEY_ONBOARDING_DONE] = done }
    }

    suspend fun setPermissionsAsked(asked: Boolean) {
        context.dataStore.edit { it[KEY_PERMISSIONS_ASKED] = asked }
    }
}
