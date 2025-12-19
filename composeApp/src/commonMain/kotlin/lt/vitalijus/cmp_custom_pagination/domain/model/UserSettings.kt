package lt.vitalijus.cmp_custom_pagination.domain.model

/**
 * User application settings.
 */
data class UserSettings(
    val viewLayoutPreference: ViewLayoutPreference = ViewLayoutPreference.DEFAULT,
    val enableNotifications: Boolean = true,
    val enableAnalytics: Boolean = true,
    val syncFrequency: SyncFrequency = SyncFrequency.DEFAULT,
    val showSyncTimestamp: Boolean = true
)
