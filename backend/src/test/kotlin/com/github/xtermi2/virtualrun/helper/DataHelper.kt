package com.github.xtermi2.virtualrun.helper

import com.github.xtermi2.virtualrun.model.Activity
import com.github.xtermi2.virtualrun.model.ActivityId
import com.github.xtermi2.virtualrun.model.AktivitaetsAufzeichnung
import com.github.xtermi2.virtualrun.model.AktivitaetsTyp
import com.github.xtermi2.virtualrun.repository.ActivityRepository
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.*

val LOCAL_DATE_TIME_FORMATTER = DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .append(DateTimeFormatter.ISO_LOCAL_DATE)
        .appendLiteral('T')
        .appendValue(ChronoField.HOUR_OF_DAY, 2)
        .appendLiteral(':')
        .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
        .optionalStart()
        .appendLiteral(':')
        .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
        .optionalStart()
        .appendFraction(ChronoField.MILLI_OF_SECOND, 0, 3, true)
        .toFormatter(Locale.GERMAN)

fun createActivities(owner: String,
                     count: Int,
                     activityRepository: ActivityRepository?): List<Activity> {
    val activities = IntRange(0, count - 1).map { i ->
        Activity(
                id = ActivityId(),
                owner = owner,
                bezeichnung = "bez $i",
                typ = AktivitaetsTyp.values()[i % AktivitaetsTyp.values().size],
                distanzInKilometer = BigDecimal.valueOf(i.toLong()),
                aktivitaetsDatum = LocalDateTime.now().minusDays(i.toLong()),
                aufzeichnungsart = AktivitaetsAufzeichnung.values()[i % AktivitaetsAufzeichnung.values().size],
                eingabeDatum = LocalDateTime.now()
        )
    }

    activityRepository!!.persist(activities)
    return activities
}