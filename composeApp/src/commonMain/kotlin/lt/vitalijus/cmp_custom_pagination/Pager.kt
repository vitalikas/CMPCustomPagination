package lt.vitalijus.cmp_custom_pagination

class Pager<KEY, ITEM>(
    private val initialKey: KEY,
    private val onLoadUpdated: (Boolean) -> Unit,
    private val onRequest: suspend (nextKey: KEY) -> Result<ITEM>,
    private val getNextKey: suspend (currentKey: KEY, result: ITEM) -> KEY,
    private val onError: suspend (Throwable?) -> Unit,
    private val onSuccess: suspend (result: ITEM, newKey: KEY) -> Unit,
    private val endReached: (currentKey: KEY, result: ITEM) -> Boolean
) {

    companion object {

        const val INITIAL_PAGE_KEY = 0
        const val PAGE_SIZE = 10
    }

    private var currentKey = initialKey
    private var isMakingRequest = false
    private var isEndReached = false

    suspend fun loadNextItems() {
        if (isMakingRequest || isEndReached) return

        isMakingRequest = true
        onLoadUpdated(true)

        val result = onRequest(currentKey)
        isMakingRequest = false

        val item = result.getOrElse { error ->
            onError(error)
            onLoadUpdated(false)
            return
        }

        currentKey = getNextKey(currentKey, item)

        onSuccess(item, currentKey)
        onLoadUpdated(false)

        isEndReached = endReached(currentKey, item)
    }

    fun reset() {
        currentKey = initialKey
        isEndReached = false
    }
}
