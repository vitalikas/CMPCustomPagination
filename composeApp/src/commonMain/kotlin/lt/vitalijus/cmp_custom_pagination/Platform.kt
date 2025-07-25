package lt.vitalijus.cmp_custom_pagination

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform