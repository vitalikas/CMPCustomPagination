package lt.vitalijus.cmp_custom_pagination.core.utils.pager

interface PagingConfig {
    val pageSize: Int
    val initialKey: Any
    val enableRetry: Boolean
    val maxRetries: Int
}

interface PagingProvider<KEY, ITEM> {
    suspend fun loadPage(key: KEY): Result<ITEM>
    suspend fun getNextKey(currentKey: KEY, result: ITEM): KEY
    fun isEndReached(currentKey: KEY, result: ITEM): Boolean
}

interface PagingStateHandler<ITEM> {
    fun onLoadingStateChanged(isLoading: Boolean)
    suspend fun onSuccess(result: ITEM)
    suspend fun onError(error: Throwable?)
}

class Pager<KEY, ITEM>(
    private val pagingConfig: PagingConfig,
    private val pagingProvider: PagingProvider<KEY, ITEM>,
    private val pagingStateHandler: PagingStateHandler<ITEM>
) {

    @Suppress("UNCHECKED_CAST")
    private var currentKey = pagingConfig.initialKey as KEY
    private var isMakingRequest = false
    private var isEndReached = false
    private var retryCount = 0

    suspend fun loadNextItems() {
        if (isMakingRequest || isEndReached) return

        isMakingRequest = true
        pagingStateHandler.onLoadingStateChanged(true)

        try {
            val result = pagingProvider.loadPage(currentKey)
            result.fold(
                onSuccess = { item ->
                    pagingStateHandler.onSuccess(item)
                    // Check if end is reached BEFORE updating the key
                    // This allows strategies to use the current key and response for end detection
                    isEndReached = pagingProvider.isEndReached(currentKey, item)
                    // Update key for next request
                    if (!isEndReached) {
                        currentKey = pagingProvider.getNextKey(currentKey, item)
                    }
                    retryCount = 0 // Reset retry count on success
                },
                onFailure = { error ->
                    if (pagingConfig.enableRetry && retryCount < pagingConfig.maxRetries) {
                        retryCount++
                        loadNextItems()
                    } else {
                        pagingStateHandler.onError(error)
                    }
                }
            )
        } finally {
            isMakingRequest = false
            pagingStateHandler.onLoadingStateChanged(false)
        }
    }

    fun reset() {
        @Suppress("UNCHECKED_CAST")
        currentKey = pagingConfig.initialKey as KEY
        isEndReached = false
        retryCount = 0
    }
}
