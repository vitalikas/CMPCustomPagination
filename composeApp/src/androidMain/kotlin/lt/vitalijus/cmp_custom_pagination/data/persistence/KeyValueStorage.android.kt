package lt.vitalijus.cmp_custom_pagination.data.persistence

import android.content.Context
import android.content.SharedPreferences

class AndroidKeyValueStorage(context: Context) : KeyValueStorage {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "app_prefs",
        Context.MODE_PRIVATE
    )

    override fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    override fun getString(key: String): String? {
        return prefs.getString(key, null)
    }

    override fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }

    override fun clear() {
        prefs.edit().clear().apply()
    }
}

private var storageInstance: KeyValueStorage? = null

fun initializeStorage(context: Context) {
    storageInstance = AndroidKeyValueStorage(context)
}

actual fun createKeyValueStorage(): KeyValueStorage {
    return storageInstance
        ?: throw IllegalStateException("Storage not initialized. Call initializeStorage() first.")
}
