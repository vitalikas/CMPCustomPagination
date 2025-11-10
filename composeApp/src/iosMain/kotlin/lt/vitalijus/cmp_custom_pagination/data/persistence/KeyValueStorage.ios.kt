package lt.vitalijus.cmp_custom_pagination.data.persistence

import platform.Foundation.NSUserDefaults

class IosKeyValueStorage : KeyValueStorage {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    override fun putString(key: String, value: String) {
        userDefaults.setObject(value, forKey = key)
    }

    override fun getString(key: String): String? {
        return userDefaults.stringForKey(key)
    }

    override fun remove(key: String) {
        userDefaults.removeObjectForKey(key)
    }

    override fun clear() {
        val dictionary = userDefaults.dictionaryRepresentation()
        dictionary.keys.forEach { key ->
            userDefaults.removeObjectForKey(key as String)
        }
    }
}

actual fun createKeyValueStorage(): KeyValueStorage {
    return IosKeyValueStorage()
}
