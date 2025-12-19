package lt.vitalijus.cmp_custom_pagination.domain.model

/**
 * Sync frequency preference for data refresh.
 */
enum class SyncFrequency(
    val displayName: String,
    val durationMs: Long
) {
    REAL_TIME("Real-time (Always fresh)", 0L), // Always refresh
    EVERY_MINUTE("Every minute", 60_000L),
    EVERY_5_MINUTES("Every 5 minutes", 300_000L),
    EVERY_15_MINUTES("Every 15 minutes", 900_000L),
    EVERY_HOUR("Every hour", 3_600_000L),
    MANUAL_ONLY("Manual only", Long.MAX_VALUE); // Never auto-refresh

    companion object {
        val DEFAULT = EVERY_5_MINUTES

        fun fromString(value: String?): SyncFrequency {
            return values().find { it.name == value } ?: DEFAULT
        }
    }
}
