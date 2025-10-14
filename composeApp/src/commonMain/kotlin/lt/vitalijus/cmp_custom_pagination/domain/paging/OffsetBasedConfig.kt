package lt.vitalijus.cmp_custom_pagination.domain.paging

import lt.vitalijus.cmp_custom_pagination.core.utils.pager.PagingConfig

data class OffsetBasedConfig(
    override val pageSize: Int = 10,
    override val initialKey: Int = 1,
    override val enableRetry: Boolean = true,
    override val maxRetries: Int = 2
) : PagingConfig
