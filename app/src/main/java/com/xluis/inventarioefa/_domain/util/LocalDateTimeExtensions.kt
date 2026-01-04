package com.xluis.inventarioefa._domain.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun LocalDateTime.toReadable(): String {

    val today = LocalDate.now()
    val date = this.toLocalDate()

    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val fullFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale("es", "ES"))

    return when {
        date.isEqual(today) -> "Hoy, ${this.format(timeFormatter)}"
        date.isEqual(today.minusDays(1)) -> "Ayer, ${this.format(timeFormatter)}"
        else -> this.format(fullFormatter)
    }
}
