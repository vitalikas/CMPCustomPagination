package lt.vitalijus.cmp_custom_pagination.core.network

import io.ktor.client.engine.HttpClientEngine

expect class HttpClientEngineFactory() {

    fun create(): HttpClientEngine
}
