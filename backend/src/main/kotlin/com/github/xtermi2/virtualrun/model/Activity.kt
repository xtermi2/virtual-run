package com.github.xtermi2.virtualrun.model

import com.fasterxml.jackson.annotation.JsonIgnore
import io.quarkus.mongodb.panache.MongoEntity
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonIgnore
import org.bson.types.ObjectId
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Max
import javax.validation.constraints.Past

val TAUSEND = BigDecimal.valueOf(1000L)

data class ActivityId(var id: String = ObjectId().toString()) {
    constructor(id: ObjectId) : this(id.toString())

    override fun toString() = id
}

@MongoEntity(collection = "activities")
data class Activity(

        @BsonId
        var id: ActivityId,

        @DecimalMin(value = "0.001")
        @Max(value = 1000)
        var distanzInKilometer: BigDecimal,

        var typ: AktivitaetsTyp,

        @Past
        var aktivitaetsDatum: LocalDateTime,

        var eingabeDatum: LocalDateTime,

        var updatedDatum: LocalDateTime? = null,

        var aufzeichnungsart: AktivitaetsAufzeichnung,

        var bezeichnung: String? = null,

        var owner: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Activity

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    @JsonIgnore
    @BsonIgnore
    fun getDistanzInMeter(): Int {
        return distanzInKilometer.multiply(TAUSEND).toInt()
    }
}