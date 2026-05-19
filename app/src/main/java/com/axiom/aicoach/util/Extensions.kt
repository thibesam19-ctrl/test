package com.axiom.aicoach.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

fun newId(): String = UUID.randomUUID().toString()

fun LocalDate.toDbString(): String = this.format(DateTimeFormatter.ISO_LOCAL_DATE)
fun LocalDateTime.toDbString(): String = this.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
fun String.toLocalDate(): LocalDate = LocalDate.parse(this, DateTimeFormatter.ISO_LOCAL_DATE)
fun String.toLocalDateTime(): LocalDateTime = LocalDateTime.parse(this, DateTimeFormatter.ISO_LOCAL_DATE_TIME)

fun Float.roundTo(decimals: Int): Float {
    val factor = Math.pow(10.0, decimals.toDouble())
    return (Math.round(this * factor) / factor).toFloat()
}

fun Int.formatWithCommas(): String = "%,d".format(this)

fun Float.toCalorieString(): String = this.toInt().formatWithCommas() + " kcal"
