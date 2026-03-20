
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

fun Date.toReadableDateTime(
    zoneId: ZoneId = ZoneId.systemDefault(),
    locale: Locale = Locale.getDefault()
): String {
    val now = LocalDate.now(zoneId)
    val dateTime = this.toInstant().atZone(zoneId).toLocalDateTime()
    val date = dateTime.toLocalDate()
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", locale)

    val timeString = dateTime.format(timeFormatter) // muestra la hora en 24h, ej. 15:30

    return when {
        date.isEqual(now) -> "Hoy a las $timeString"
        date.isEqual(now.minusDays(1)) -> "Ayer a las $timeString"
        date.isAfter(now.minusDays(7)) -> {
            val days = ChronoUnit.DAYS.between(date, now)
            "Hace $days días a las $timeString"
        }
        else -> {
            val dateFormatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", locale)
            "${date.format(dateFormatter)} a las $timeString"
        }
    }
}