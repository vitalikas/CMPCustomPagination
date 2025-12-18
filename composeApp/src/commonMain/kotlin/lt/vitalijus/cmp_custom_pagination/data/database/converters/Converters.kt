package lt.vitalijus.cmp_custom_pagination.data.database.converters

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import lt.vitalijus.cmp_custom_pagination.domain.model.Review

/**
 * Room TypeConverters for complex data types
 */
class Converters {
    
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { Json.encodeToString(it) }
    }
    
    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let { Json.decodeFromString(it) }
    }
    
    @TypeConverter
    fun fromReviewList(value: List<Review>?): String? {
        return value?.let { Json.encodeToString(it) }
    }
    
    @TypeConverter
    fun toReviewList(value: String?): List<Review>? {
        return value?.let { Json.decodeFromString(it) }
    }
}
