package com.github.xtermi2.virtualrun.helper

import com.github.xtermi2.virtualrun.model.Activity
import com.github.xtermi2.virtualrun.model.AktivitaetsAufzeichnung
import com.github.xtermi2.virtualrun.model.AktivitaetsTyp
import com.github.xtermi2.virtualrun.repository.ActivityRepository
import java.math.BigDecimal
import java.time.LocalDateTime

fun createActivities(owner: String,
                     count: Int,
                     activityRepository: ActivityRepository?): List<Activity> {
    val activities = IntRange(0, count - 1).map { i ->
        Activity(owner = owner,
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