package com.example.ch07.util

private const val MINUTE_MS = 60_000L
private const val HOUR_MS = 60 * MINUTE_MS
private const val DAY_MS = 24 * HOUR_MS

// Selisih `now - then` diubah jadi teks pendek — negatif dianggap 0 (data lama).
// Satu bulan dihitung 30 hari, satu tahun 365 hari.
fun formatRelativeTime(now: Long, then: Long): String {
    val diff = (now - then).coerceAtLeast(0L)
    return when {
        diff < MINUTE_MS  -> "baru saja"
        diff < HOUR_MS    -> "${diff / MINUTE_MS} menit lalu"
        diff < DAY_MS     -> "${diff / HOUR_MS} jam lalu"
        diff < 30 * DAY_MS  -> "${diff / DAY_MS} hari lalu"
        diff < 365 * DAY_MS -> "${diff / (30 * DAY_MS)} bulan lalu"
        else              -> "${diff / (365 * DAY_MS)} tahun lalu"
    }
}
