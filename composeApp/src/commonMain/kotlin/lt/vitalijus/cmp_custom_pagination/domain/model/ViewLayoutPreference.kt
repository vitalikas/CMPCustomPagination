package lt.vitalijus.cmp_custom_pagination.domain.model

/**
 * User's preferred view layout for product lists.
 */
enum class ViewLayoutPreference {
    GRID,
    LIST;
    
    companion object {
        val DEFAULT = GRID
        
        fun fromString(value: String?): ViewLayoutPreference {
            return when (value?.uppercase()) {
                "LIST" -> LIST
                "GRID" -> GRID
                else -> DEFAULT
            }
        }
    }
}
