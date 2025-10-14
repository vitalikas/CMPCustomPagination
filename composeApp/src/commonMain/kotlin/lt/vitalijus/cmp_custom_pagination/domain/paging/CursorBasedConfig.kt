package lt.vitalijus.cmp_custom_pagination.domain.paging

import lt.vitalijus.cmp_custom_pagination.core.utils.pager.PagingConfig

data class CursorBasedConfig(
    override val pageSize: Int = 7,
    override val initialKey: Any = "", // Empty string represents no cursor (start from beginning)
    override val enableRetry: Boolean = true,
    override val maxRetries: Int = 1
) : PagingConfig
