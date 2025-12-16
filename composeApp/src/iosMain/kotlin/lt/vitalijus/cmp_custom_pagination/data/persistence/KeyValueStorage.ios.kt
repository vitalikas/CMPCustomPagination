package lt.vitalijus.cmp_custom_pagination.data.persistence

import platform.Foundation.NSUserDefaults

class IosKeyValueStorage : KeyValueStorage {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    override suspend fun putString(key: String, value: String) {
        userDefaults.setObject(value, forKey = key)
    }

    override suspend fun getString(key: String): String? {
        return userDefaults.stringForKey(key)
    }

    override suspend fun remove(key: String) {
        userDefaults.removeObjectForKey(key)
    }

    override suspend fun clear() {
        val dictionary = userDefaults.dictionaryRepresentation()
        dictionary.keys.forEach { key ->
            userDefaults.removeObjectForKey(key as String)
        }
    }
}
