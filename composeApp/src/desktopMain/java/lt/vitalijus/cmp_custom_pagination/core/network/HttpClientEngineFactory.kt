package lt.vitalijus.cmp_custom_pagination.core.network

actual class HttpClientEngineFactory actual constructor() {
    actual fun create(): io.ktor.client.engine.HttpClientEngine = OkHttp.create()
}