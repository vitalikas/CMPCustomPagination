package lt.vitalijus.cmp_custom_pagination.core.utils

import kotlin.math.roundToInt

fun formatPrice(cents: Double): String {
    val intCents = cents.roundToInt()
    val dollars = intCents / 100
    val remainderCents = intCents % 100
    return "${dollars}.${remainderCents.toString().padStart(2, '0')}"
}
