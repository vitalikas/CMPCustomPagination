package lt.vitalijus.cmp_custom_pagination

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

actual class HttpClientEngineFactory actual constructor() {
    actual fun create(): HttpClientEngine = OkHttp.create()
}
