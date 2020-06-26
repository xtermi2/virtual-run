package com.github.xtermi2.virtualrun.repository

import com.github.xtermi2.virtualrun.model.Activity
import com.github.xtermi2.virtualrun.model.ActivityId
import com.github.xtermi2.virtualrun.repository.dto.ActivitySearchRequest
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase
import io.quarkus.mongodb.panache.PanacheQuery
import io.quarkus.panache.common.Page
import io.quarkus.panache.common.Sort
import org.bson.types.ObjectId
import java.util.*
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class ActivityRepository : PanacheMongoRepositoryBase<Activity, ActivityId> {

    override fun findByIdOptional(id: ActivityId): Optional<Activity> {
        return Optional.ofNullable(findByIdOrObjectId(id))
    }

    override fun findById(id: ActivityId): Activity? {
        return findByIdOrObjectId(id)
    }

    fun findByIdOrObjectId(id: ActivityId): Activity? {
        return if (ObjectId.isValid(id.toString())) {
            find("_id", ObjectId(id.toString()))
                    .singleResultOptional<Activity>()
                    .orElseGet { findByIdAsString(id.toString()) }
        } else {
            findByIdAsString(id.toString())
        }
    }

    private fun findByIdAsString(id: String): Activity? {
        return find("_id", id)
                .singleResultOptional<Activity>()
                .orElse(null)
    }

    fun findByOwnerIn(usernameFilter: Set<String>): List<Activity> {
        // totally ugly, but seems panache does not support list/array parameters
        val paramsString = usernameFilter.mapIndexed { index, _ -> "?${index + 1}" }.joinToString { it }
        return find("{'owner' :{\$in : [$paramsString]}}", *usernameFilter.toTypedArray()).list()
    }

    fun findByOwner(searchRequest: ActivitySearchRequest): List<Activity> {
        val pageIndex = searchRequest.pageableFirstElement / searchRequest.pageSize
        val sort = when {
            searchRequest.sortAsc -> Sort.by(searchRequest.sortProperty.fieldName).ascending()
            else -> Sort.by(searchRequest.sortProperty.fieldName).descending()
        }
        return createFindByOwnerQuery(searchRequest.owner, sort)
                .page<Activity>(Page.of(pageIndex, searchRequest.pageSize))
                .list()
    }

    fun countByOwner(owner: String): Long {
        return createFindByOwnerQuery(owner, Sort.by()).count()
    }

    private fun createFindByOwnerQuery(owner: String, sort: Sort): PanacheQuery<Activity> {
        return find("owner", sort, owner)
    }
}