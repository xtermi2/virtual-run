package com.github.xtermi2.virtualrun.api

import com.github.xtermi2.virtualrun.helper.LOCAL_DATE_TIME_FORMATTER
import com.github.xtermi2.virtualrun.helper.createActivities
import com.github.xtermi2.virtualrun.model.Activity
import com.github.xtermi2.virtualrun.repository.ActivityRepository
import com.github.xtermi2.virtualrun.repository.dto.ActivitySearchRequest
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
        val andisActivity = createActivities("andi", 1, activityRepository)[0]

        val res = given().`when`()
                .auth().basic("andi", "superSecret")
                .get("/activities/{id}", andisActivity.id.toString())
                .then()
                .statusCode(200)
                .body("id", equalTo(andisActivity.id.toString()))
                .body("aktivitaetsDatum", equalTo(andisActivity.aktivitaetsDatum.format(LOCAL_DATE_TIME_FORMATTER)))
                .extract().`as`(Activity::class.java)

        assertThat(res)
                .isEqualTo(andisActivity)
    }

    @Test
    fun findById_notFound() {
        given().`when`()
                .auth().basic("andi", "superSecret")
                .get("/activities/{id}", ObjectId(Date()).toString())
                .then()
                .statusCode(404)
    }

    @Test
    fun findById_unauthorized() {
        given().`when`()
                .get("/activities/{id}", ObjectId(Date()).toString())
                .then()
                .statusCode(401)
    }

    @Test
    fun findByOwner_with_defaults() {
        val andisActivities = createActivities("andi", 5, activityRepository)
        createActivities("foo", 2, activityRepository)[0]

        val res = given().`when`()
                .auth().basic("andi", "superSecret")
                .body(ActivitySearchRequest(owner = "andi"))
                .contentType(ContentType.JSON)
                .log().all(true)
                .post("/activities")
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", `is`(andisActivities.size))
                .log().all(true)
                .extract().`as`(Array<Activity>::class.java)

        assertThat(res)
                .containsExactlyElementsOf(andisActivities.sortedByDescending { it.aktivitaetsDatum })
    }

    @Test
    fun findByOwner_empty_result() {
        val res = given().`when`()
                .auth().basic("andi", "superSecret")
                .body("""{
                    "owner": "unknown",
                    "sortProperty": "AKTIVITAETS_DATUM",
                    "sortAsc": false,
                    "pageableFirstElement": 0,
                    "pageSize": 10
                    }""")
                .contentType(ContentType.JSON)
                .log().all(true)
                .post("/activities")
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", `is`(0))
                .log().all(true)
                .extract().`as`(Array<Activity>::class.java)

        assertThat(res)
                .isEmpty()
    }

    @Test
    fun findByOwner_unauthorized() {
        given().`when`()
                .body(ActivitySearchRequest(owner = "andi"))
                .contentType(ContentType.JSON)
                .log().all(true)
                .post("/activities")
                .then()
                .statusCode(401)
    }
}