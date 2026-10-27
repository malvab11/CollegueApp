package com.example.collegueapp.platform

import androidx.compose.runtime.Composable
import kotlinx.datetime.*
import kotlin.random.Random

interface SchoolStorage {
    fun get(key: String): String?
    fun put(key: String, value: String?)
}
@Composable
expect fun SchoolBackHandler(enabled: Boolean, onBack: () -> Unit)

fun LocalDate.Companion.now(): LocalDate = kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
fun LocalDate.plusDays(days: Long): LocalDate = plus(DatePeriod(days = days.toInt()))
fun LocalDate.minusDays(days: Long): LocalDate = plusDays(-days)
fun LocalDate.plusWeeks(weeks: Long): LocalDate = plusDays(weeks * 7)
fun LocalDate.isBefore(other: LocalDate): Boolean = this < other
fun LocalDate.isAfter(other: LocalDate): Boolean = this > other
val DayOfWeek.value: Int get() = isoDayNumber
fun LocalDate.startOfWeek(): LocalDate = minusDays((dayOfWeek.isoDayNumber - 1).toLong())
fun LocalDate.nextMonday(): LocalDate = plusDays((8 - dayOfWeek.isoDayNumber).toLong())
fun currentTimestamp(): String = kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()
fun randomId(): String = Random.nextBytes(16).joinToString("") { (it.toInt() and 255).toString(16).padStart(2, '0') }
fun timeLabel(minutes: Int): String = "${(minutes / 60).toString().padStart(2, '0')}:${(minutes % 60).toString().padStart(2, '0')}"
private val months = listOf("enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre")
fun shortDate(date: LocalDate): String = "${date.dayOfMonth} ${months[date.monthNumber - 1].take(3)} ${date.year}"
fun fullDate(date: LocalDate): String = "${listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")[date.dayOfWeek.isoDayNumber - 1]}, ${date.dayOfMonth} de ${months[date.monthNumber - 1]}"
