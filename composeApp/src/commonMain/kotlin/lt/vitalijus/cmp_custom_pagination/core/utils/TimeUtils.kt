package lt.vitalijus.cmp_custom_pagination.core.utils

/**
 * Get current timestamp in milliseconds (platform-specific)
 */
expect fun currentTimeMillis(): Long

/**
 * Format timestamp to relative time string (e.g., "2 minutes ago", "Just now")
 */
fun formatRelativeTime(timestampMs: Long?): String {
    if (timestampMs == null) return "Never synced"
    
    val now = currentTimeMillis()
    val diffMs = now - timestampMs
    
    if (diffMs < 0) return "Just now" // Clock skew
    
    val seconds = diffMs / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    
    return when {
        seconds < 10 -> "Just now"
        seconds < 60 -> "$seconds seconds ago"
        minutes < 60 -> if (minutes == 1L) "1 minute ago" else "$minutes minutes ago"
        hours < 24 -> if (hours == 1L) "1 hour ago" else "$hours hours ago"
        days < 7 -> if (days == 1L) "1 day ago" else "$days days ago"
        else -> "${days / 7} weeks ago"
    }
}
