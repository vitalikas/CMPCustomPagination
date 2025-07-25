package lt.vitalijus.cmp_custom_pagination

import io.ktor.client.engine.HttpClientEngine

expect class HttpClientEngineFactory() {

    fun create(): HttpClientEngine
}
