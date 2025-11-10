package lt.vitalijus.cmp_custom_pagination.data.persistence

import java.io.File
import java.util.Properties

class DesktopKeyValueStorage : KeyValueStorage {
    private val propsFile = File(System.getProperty("user.home"), ".app_prefs.properties")
    private val props = Properties()

    init {
        if (propsFile.exists()) {
            propsFile.inputStream().use { props.load(it) }
        }
    }

    override fun putString(key: String, value: String) {
        props.setProperty(key, value)
        save()
    }

    override fun getString(key: String): String? {
        return props.getProperty(key)
    }

    override fun remove(key: String) {
        props.remove(key)
        save()
    }

    override fun clear() {
        props.clear()
        save()
    }

    private fun save() {
        propsFile.outputStream().use { props.store(it, "App Preferences") }
    }
}

actual fun createKeyValueStorage(): KeyValueStorage {
    return DesktopKeyValueStorage()
}
