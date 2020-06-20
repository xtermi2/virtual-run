package com.github.xtermi2.virtualrun.api

import com.github.xtermi2.virtualrun.repository.ActivityRepository
import org.bson.types.ObjectId
import javax.validation.constraints.NotNull
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/activities")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class ActivityResource(val activityRepository: ActivityRepository) {

    @GET
    @Path("/{id}")
    fun getById(@NotNull @PathParam("id") id: ObjectId): Response {
        return activityRepository.findByIdOptional(id)
                .map { Response.ok(it).build() }
                .orElse(Response.status(Response.Status.NOT_FOUND).build())
    }
}