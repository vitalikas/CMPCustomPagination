package lt.vitalijus.cmp_custom_pagination.data.persistence

import lt.vitalijus.cmp_custom_pagination.domain.model.UserSettings
import lt.vitalijus.cmp_custom_pagination.domain.model.ViewLayoutPreference

/**
 * Repository for managing user settings persistence.
 */
interface SettingsRepository {
    /**
     * Get current settings (one-time).
     */
    suspend fun getSettings(): UserSettings
    
    /**
     * Save view layout preference.
     */
    suspend fun saveViewLayoutPreference(preference: ViewLayoutPreference)
    
    /**
     * Save notifications setting.
     */
    suspend fun saveNotificationsEnabled(enabled: Boolean)
    
    /**
     * Save analytics setting.
     */
    suspend fun saveAnalyticsEnabled(enabled: Boolean)
    
    /**
     * Reset all settings to defaults.
     */
    suspend fun resetToDefaults()
}

class LocalSettingsRepository(
    private val keyValueStorage: KeyValueStorage
) : SettingsRepository {
    
    companion object {
        private const val KEY_VIEW_LAYOUT = "settings_view_layout"
        private const val KEY_NOTIFICATIONS = "settings_notifications"
        private const val KEY_ANALYTICS = "settings_analytics"
    }
    
    override suspend fun getSettings(): UserSettings {
        return UserSettings(
            viewLayoutPreference = ViewLayoutPreference.fromString(
                keyValueStorage.getString(KEY_VIEW_LAYOUT)
            ),
            enableNotifications = keyValueStorage.getString(KEY_NOTIFICATIONS)?.toBoolean() ?: true,
            enableAnalytics = keyValueStorage.getString(KEY_ANALYTICS)?.toBoolean() ?: true
        )
    }
    
    override suspend fun saveViewLayoutPreference(preference: ViewLayoutPreference) {
        keyValueStorage.putString(KEY_VIEW_LAYOUT, preference.name)
    }
    
    override suspend fun saveNotificationsEnabled(enabled: Boolean) {
        keyValueStorage.putString(KEY_NOTIFICATIONS, enabled.toString())
    }
    
    override suspend fun saveAnalyticsEnabled(enabled: Boolean) {
        keyValueStorage.putString(KEY_ANALYTICS, enabled.toString())
    }
    
    override suspend fun resetToDefaults() {
        keyValueStorage.remove(KEY_VIEW_LAYOUT)
        keyValueStorage.putString(KEY_NOTIFICATIONS, "true")
        keyValueStorage.putString(KEY_ANALYTICS, "true")
    }
}
