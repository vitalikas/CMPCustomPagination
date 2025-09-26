package lt.vitalijus.cmp_custom_pagination.core.utils.pager

interface PagingConfiguration {
    val pageSize: Int
    val initialKey: Any
    val enableRetry: Boolean
    val maxRetries: Int
}

interface PagingStrategy<KEY, ITEM> {
    suspend fun loadPage(key: KEY): Result<ITEM>
    suspend fun getNextKey(currentKey: KEY, result: ITEM): KEY
    fun isEndReached(currentKey: KEY, result: ITEM): Boolean
}

interface PagingStateHandler<ITEM> {
    fun onLoadingStateChanged(isLoading: Boolean)
    suspend fun onSuccess(result: ITEM)
    suspend fun onError(error: Throwable?)
}

data class DefaultPagingConfiguration(
    override val pageSize: Int = 10,
    override val initialKey: Any = 1,
    override val enableRetry: Boolean = false,
    override val maxRetries: Int = 3
) : PagingConfiguration

class Pager<KEY, ITEM>(
    private val config: PagingConfiguration,
    private val strategy: PagingStrategy<KEY, ITEM>,
    private val stateHandler: PagingStateHandler<ITEM>
) {

    @Suppress("UNCHECKED_CAST")
    private var currentKey = config.initialKey as KEY
    private var isMakingRequest = false
    private var isEndReached = false
    private var retryCount = 0

    suspend fun loadNextItems() {
        if (isMakingRequest || isEndReached) return

        isMakingRequest = true
        stateHandler.onLoadingStateChanged(true)

        try {
            val result = strategy.loadPage(currentKey)
            result.fold(
                onSuccess = { item ->
                    currentKey = strategy.getNextKey(currentKey, item)
                    stateHandler.onSuccess(item)
                    isEndReached = strategy.isEndReached(currentKey, item)
                },
                onFailure = { error ->
                    if (config.enableRetry && retryCount < config.maxRetries) {
                        retryCount++
                        loadNextItems()
                    } else {
                        stateHandler.onError(error)
                    }
                }
            )
        } finally {
            isMakingRequest = false
            stateHandler.onLoadingStateChanged(false)
        }
    }

    fun reset() {
        @Suppress("UNCHECKED_CAST")
        currentKey = config.initialKey as KEY
        isEndReached = false
        retryCount = 0
    }
}
