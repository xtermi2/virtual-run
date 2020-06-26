package com.github.xtermi2.virtualrun.api

import com.github.xtermi2.virtualrun.helper.LOCAL_DATE_TIME_FORMATTER
import com.github.xtermi2.virtualrun.helper.createActivities
import com.github.xtermi2.virtualrun.model.Activity
import com.github.xtermi2.virtualrun.model.EMPTY_USER
import com.github.xtermi2.virtualrun.model.SecurityRole
import com.github.xtermi2.virtualrun.model.User
import com.github.xtermi2.virtualrun.repository.ActivityRepository
import com.github.xtermi2.virtualrun.repository.UserRepository
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

    @Inject
    private var userRepository: UserRepository? = null

    private val cleartextPassword = "superSecret"
    private var user: User = EMPTY_USER

    @BeforeEach
    internal fun setUp() {
        userRepository!!.deleteAll()
        activityRepository!!.deleteAll()

        user = userRepository!!.createNewUser("andi", cleartextPassword, setOf(SecurityRole.USER))
    }

    @Test
    fun findById_returnsFoundEntity() {
        val andisActivity = createActivities("andi", 1, activityRepository)[0]

        val res = given().`when`()
                .log().ifValidationFails()
                .auth().basic(user.username, cleartextPassword)
                .get("/activities/{id}", andisActivity.id.toString())
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .body("id.id", equalTo(andisActivity.id.toString()))
                .body("aktivitaetsDatum", equalTo(andisActivity.aktivitaetsDatum.format(LOCAL_DATE_TIME_FORMATTER)))
                .extract().`as`(Activity::class.java)

        assertThat(res)
                .isEqualTo(andisActivity)
    }

    @Test
    fun findById_notFound() {
        given().`when`()
                .log().ifValidationFails()
                .auth().basic(user.username, cleartextPassword)
                .get("/activities/{id}", ObjectId(Date()).toString())
                .then()
                .log().ifValidationFails()
                .statusCode(404)
    }

    @Test
    fun findById_unauthorized() {
        given().`when`()
                .log().ifValidationFails()
                .get("/activities/{id}", ObjectId(Date()).toString())
                .then()
                .log().ifValidationFails()
                .statusCode(401)
    }

    @Test
    fun findByOwner_with_defaults() {
        val andisActivities = createActivities("andi", 5, activityRepository)
        createActivities("foo", 2, activityRepository)[0]

        val res = given().`when`()
                .auth().basic(user.username, cleartextPassword)
                .body(ActivitySearchRequest(owner = "andi"))
                .contentType(ContentType.JSON)
                .log().ifValidationFails()
                .post("/activities")
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", `is`(andisActivities.size))
                .log().ifValidationFails()
                .extract().`as`(Array<Activity>::class.java)

        assertThat(res)
                .containsExactlyElementsOf(andisActivities.sortedByDescending { it.aktivitaetsDatum })
    }

    @Test
    fun findByOwner_empty_result() {
        given().`when`()
                .auth().basic(user.username, cleartextPassword)
                .body("""{
                    "owner": "unknown",
                    "sortProperty": "AKTIVITAETS_DATUM",
                    "sortAsc": false,
                    "pageableFirstElement": 0,
                    "pageSize": 10
                    }""")
                .contentType(ContentType.JSON)
                .log().ifValidationFails()
                .post("/activities")
                .then()
                .statusCode(200)
                .assertThat()
                .body("size()", `is`(0))
                .log().ifValidationFails()
    }

    @Test
    fun findByOwner_unauthorized() {
        given().`when`()
                .body(ActivitySearchRequest(owner = "andi"))
                .contentType(ContentType.JSON)
                .log().ifValidationFails()
                .post("/activities")
                .then()
                .log().ifValidationFails()
                .statusCode(401)
    }
}