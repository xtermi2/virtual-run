package com.github.xtermi2.virtualrun.repository

import com.github.xtermi2.virtualrun.model.Activity
import com.github.xtermi2.virtualrun.model.AktivitaetsAufzeichnung
import com.github.xtermi2.virtualrun.model.AktivitaetsTyp
import com.github.xtermi2.virtualrun.repository.dto.ActivitySearchRequest
import com.github.xtermi2.virtualrun.repository.dto.AktivitaetSortProperties
import io.quarkus.test.junit.QuarkusTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

@QuarkusTest
class ActivityRepositoryTest {

    @Inject
    private var activityRepository: ActivityRepository? = null

    @BeforeEach
    internal fun setUp() {
        activityRepository!!.deleteAll()
    }

    @Test
    internal fun findByOwnerIn() {
        createActivities("andi", 1)
        createActivities("foo", 2)
        createActivities("bar", 1)

        val res = activityRepository!!.findByOwnerIn(setOf("andi", "foo"))

        assertThat(res)
                .hasSize(3)
                .allSatisfy {
                    assertThat(it.owner)
                            .isIn("andi", "foo")
                }
    }

    @Test
    fun searchAktivities_paging_works() {
        val owner = "andi"
        val aktivities: List<Activity> = createActivities(owner, 12)
        val searchRequest = ActivitySearchRequest(
                owner = owner,
                pageableFirstElement = 0,
                pageSize = 5,
                sortProperty = AktivitaetSortProperties.DISTANZ_IN_KILOMETER,
                sortAsc = true)

        val firstPage = activityRepository!!.findByOwner(searchRequest)
        assertThat(firstPage)
                .containsExactlyElementsOf(aktivities.subList(0, 5))
        val secondPage = activityRepository!!.findByOwner(searchRequest.copy(
                pageableFirstElement = 5))
        assertThat(secondPage)
                .containsExactlyElementsOf(aktivities.subList(5, 10))
        val thirdPage: List<Activity> = activityRepository!!.findByOwner(searchRequest.copy(
                pageableFirstElement = 10))
        assertThat(thirdPage)
                .containsExactlyElementsOf(aktivities.subList(10, 12))
    }

    @Test
    fun searchAktivities_filter_by_owner_works() {
        val andi = "andi"
        val aktivitiesAndi: List<Activity> = createActivities(andi, 1)
        val foo = "foo"
        createActivities(foo, 1)
        val searchRequest = ActivitySearchRequest(
                owner = andi,
                pageableFirstElement = 0,
                pageSize = 5,
                sortProperty = AktivitaetSortProperties.DISTANZ_IN_KILOMETER,
                sortAsc = true)

        val firstPage: List<Activity> = activityRepository!!.findByOwner(searchRequest)

        assertThat(firstPage)
                .containsExactlyElementsOf(aktivitiesAndi)
    }

    @Test
    fun searchAktivities_sort_By_distance() {
        val owner = "andi"
        var activities: List<Activity> = createActivities(owner, 12)
        val searchRequest = ActivitySearchRequest(
                owner = owner,
                pageableFirstElement = 0,
                pageSize = 12,
                sortProperty = AktivitaetSortProperties.DISTANZ_IN_KILOMETER,
                sortAsc = false)

        val firstPage = activityRepository!!.findByOwner(searchRequest)

        activities = activities.sortedBy { it.distanzInKilometer }.reversed()
        assertThat(firstPage)
                .containsExactlyElementsOf(activities)
    }

    @Test
    fun searchAktivities_sort_By_activityDate() {
        val owner = "andi"
        var activities: List<Activity> = createActivities(owner, 12)
        val searchRequest = ActivitySearchRequest(
                owner = owner,
                pageableFirstElement = 0,
                pageSize = 12,
                sortProperty = AktivitaetSortProperties.AKTIVITAETS_DATUM,
                sortAsc = true)

        val asc: List<Activity> = activityRepository!!.findByOwner(searchRequest)
        activities = activities.sortedWith(Comparator.comparing(Activity::aktivitaetsDatum))
        assertThat(asc)
                .containsExactlyElementsOf(activities)
        val desc: List<Activity> = activityRepository!!.findByOwner(searchRequest.copy(
                sortAsc = false))
        activities = activities.sortedWith(Comparator.comparing(Activity::aktivitaetsDatum).reversed())
        assertThat(desc)
                .containsExactlyElementsOf(activities)
    }

    @Test
    fun searchAktivities_sort_By_aufzeichnungsart() {
        val owner = "andi"
        var activities: List<Activity> = createActivities(owner, 12)
        val searchRequest = ActivitySearchRequest(
                owner = owner,
                pageableFirstElement = 0,
                pageSize = 12,
                sortProperty = AktivitaetSortProperties.AUFZEICHNUNGSART,
                sortAsc = true)

        val asc: List<Activity> = activityRepository!!.findByOwner(searchRequest)
        activities = activities.sortedWith(Comparator.comparing(Activity::aufzeichnungsart))
        assertThat(asc)
                .containsExactlyElementsOf(activities)
        val desc: List<Activity> = activityRepository!!.findByOwner(searchRequest.copy(
                sortAsc = false))
        activities = activities.sortedWith(Comparator.comparing(Activity::aufzeichnungsart).reversed())
        assertThat(desc)
                .containsExactlyElementsOf(activities)
    }

    @Test
    fun searchAktivities_sort_By_bezeichnung() {
        val owner = "andi"
        var activities = createActivities(owner, 12)
        val searchRequest = ActivitySearchRequest(
                owner = owner,
                pageableFirstElement = 0,
                pageSize = 12,
                sortProperty = AktivitaetSortProperties.BEZEICHNUNG,
                sortAsc = true)

        val asc: List<Activity> = activityRepository!!.findByOwner(searchRequest)
        activities = activities.sortedBy { it.bezeichnung }
        assertThat(asc)
                .containsExactlyElementsOf(activities)
        val desc: List<Activity> = activityRepository!!.findByOwner(searchRequest.copy(
                sortAsc = false))
        activities = activities.sortedBy { it.bezeichnung }.reversed()
        assertThat(desc)
                .containsExactlyElementsOf(activities)
    }

    @Test
    fun searchAktivities_sort_By_typ() {
        val owner = "andi"
        var activities: List<Activity> = createActivities(owner, 12)
        val searchRequest = ActivitySearchRequest(
                owner = owner,
                pageableFirstElement = 0,
                pageSize = 12,
                sortProperty = AktivitaetSortProperties.TYP,
                sortAsc = true)

        val asc: List<Activity> = activityRepository!!.findByOwner(searchRequest)
        activities = activities.sortedBy { it.typ.name }
        assertThat(asc.map { it.typ })
                .containsExactlyElementsOf(activities.map { it.typ })
        val desc: List<Activity> = activityRepository!!.findByOwner(searchRequest.copy(
                sortAsc = false))

        activities = activities.sortedBy { it.typ.name }.reversed()
        assertThat(desc.map { it.typ })
                .containsExactlyElementsOf(activities.map { it.typ })
    }

    @Test
    fun countActivities_should_count_activities_of_given_user() {
        val owner = "andi"
        val activitiesOfAndi: List<Activity> = createActivities(owner, 12)
        createActivities("fred", 3)
        val res: Long = activityRepository!!.countByOwner(owner)
        assertThat(res)
                .isEqualTo(activitiesOfAndi.size.toLong())
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