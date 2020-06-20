package com.github.xtermi2.virtualrun.api

import com.github.xtermi2.virtualrun.model.Activity
import com.github.xtermi2.virtualrun.model.AktivitaetsAufzeichnung
import com.github.xtermi2.virtualrun.model.AktivitaetsTyp
import com.github.xtermi2.virtualrun.repository.ActivityRepository
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

@QuarkusTest
class ActivityResourceTest {

    @Inject
    private var activityRepository: ActivityRepository? = null

    @BeforeEach
    internal fun setUp() {
        activityRepository!!.deleteAll()
    }

    @Test
    fun findById_returnsFoundEntity() {
        val andisActivity = createActivities("andi", 1)[0]

        val res = given().`when`()
                .get("/activities/{id}", andisActivity.id.toString())
                .then()
                .statusCode(200)
                .body("id", equalTo(andisActivity.id.toString()))
                .extract().`as`(Activity::class.java)

        assertThat(res)
                .isEqualTo(andisActivity)
    }

    @Test
    fun findById_notFound() {
        given().`when`()
                .get("/activities/{id}", ObjectId(Date()).toString())
                .then()
                .statusCode(404)
    }

    private fun createActivities(owner: String, count: Int): List<Activity> {
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
}