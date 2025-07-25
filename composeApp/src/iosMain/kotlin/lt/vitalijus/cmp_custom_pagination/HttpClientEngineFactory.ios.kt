package lt.vitalijus.cmp_custom_pagination

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin

actual class HttpClientEngineFactory actual constructor() {
    actual fun create(): HttpClientEngine = Darwin.create()
}
