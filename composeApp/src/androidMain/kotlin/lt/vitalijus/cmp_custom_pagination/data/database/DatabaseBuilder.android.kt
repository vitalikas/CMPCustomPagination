package lt.vitalijus.cmp_custom_pagination.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Android implementation of database builder
 */
fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<AppDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("app_database.db")
    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}
