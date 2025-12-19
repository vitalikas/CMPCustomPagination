package lt.vitalijus.cmp_custom_pagination.data.persistence

import lt.vitalijus.cmp_custom_pagination.domain.model.SyncFrequency
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
     * Save sync frequency preference.
     */
    suspend fun saveSyncFrequency(frequency: SyncFrequency)
    
    /**
     * Save show sync timestamp preference.
     */
    suspend fun saveShowSyncTimestamp(show: Boolean)
    
    /**
     * Get last sync timestamp (milliseconds).
     */
    suspend fun getLastSyncTimestamp(): Long?
    
    /**
     * Save last sync timestamp (milliseconds).
     */
    suspend fun saveLastSyncTimestamp(timestamp: Long)
    
    /**
     * Get whether all items were loaded (for pagination).
     */
    suspend fun getAllItemsLoaded(): Boolean
    
    /**
     * Save whether all items were loaded (for pagination).
     */
    suspend fun saveAllItemsLoaded(loaded: Boolean)
    
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
        private const val KEY_SYNC_FREQUENCY = "settings_sync_frequency"
        private const val KEY_SHOW_SYNC_TIMESTAMP = "settings_show_sync_timestamp"
        private const val KEY_LAST_SYNC_TIMESTAMP = "last_sync_timestamp"
        private const val KEY_ALL_ITEMS_LOADED = "all_items_loaded"
    }
    
    override suspend fun getSettings(): UserSettings {
        return UserSettings(
            viewLayoutPreference = ViewLayoutPreference.fromString(
                keyValueStorage.getString(KEY_VIEW_LAYOUT)
            ),
            enableNotifications = keyValueStorage.getString(KEY_NOTIFICATIONS)?.toBoolean() ?: true,
            enableAnalytics = keyValueStorage.getString(KEY_ANALYTICS)?.toBoolean() ?: true,
            syncFrequency = SyncFrequency.fromString(
                keyValueStorage.getString(KEY_SYNC_FREQUENCY)
            ),
            showSyncTimestamp = keyValueStorage.getString(KEY_SHOW_SYNC_TIMESTAMP)?.toBoolean() ?: true
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
    
    override suspend fun saveSyncFrequency(frequency: SyncFrequency) {
        keyValueStorage.putString(KEY_SYNC_FREQUENCY, frequency.name)
    }
    
    override suspend fun saveShowSyncTimestamp(show: Boolean) {
        keyValueStorage.putString(KEY_SHOW_SYNC_TIMESTAMP, show.toString())
    }
    
    override suspend fun getLastSyncTimestamp(): Long? {
        return keyValueStorage.getString(KEY_LAST_SYNC_TIMESTAMP)?.toLongOrNull()
    }
    
    override suspend fun saveLastSyncTimestamp(timestamp: Long) {
        keyValueStorage.putString(KEY_LAST_SYNC_TIMESTAMP, timestamp.toString())
    }
    
    override suspend fun getAllItemsLoaded(): Boolean {
        return keyValueStorage.getString(KEY_ALL_ITEMS_LOADED)?.toBoolean() ?: false
    }
    
    override suspend fun saveAllItemsLoaded(loaded: Boolean) {
        keyValueStorage.putString(KEY_ALL_ITEMS_LOADED, loaded.toString())
    }
    
    override suspend fun resetToDefaults() {
        keyValueStorage.remove(KEY_VIEW_LAYOUT)
        keyValueStorage.putString(KEY_NOTIFICATIONS, "true")
        keyValueStorage.putString(KEY_ANALYTICS, "true")
        keyValueStorage.remove(KEY_SYNC_FREQUENCY)
        keyValueStorage.putString(KEY_SHOW_SYNC_TIMESTAMP, "true")
        keyValueStorage.remove(KEY_ALL_ITEMS_LOADED) // Reset pagination state
    }
}
