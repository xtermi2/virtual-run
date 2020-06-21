package com.github.xtermi2.virtualrun.model

import com.fasterxml.jackson.annotation.JsonIgnore
import io.quarkus.mongodb.panache.MongoEntity
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.codecs.pojo.annotations.BsonIgnore
import org.bson.types.ObjectId
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

val EMPTY_USER: User = User(username = "DUMMY", password = "DUMMY")

@MongoEntity(collection = "users")
data class User(
        @BsonId
        var id: ObjectId? = null,

        @NotBlank
        var username: String,

        @NotBlank
        var password: String,

        var roles: Set<SecurityRole> = emptySet(),

        @Size(min = 1)
        var nickname: String? = null,

        @Email
        var email: String? = null,

        var benachrichtigunsIntervall: BenachrichtigunsIntervall = BenachrichtigunsIntervall.deaktiviert,

        var includeMeInStatisticMail: Boolean = false
) : Comparable<User> {

    @JsonIgnore
    @BsonIgnore
    fun getAnzeigename(): String {
        val tmpNickname = nickname
        return if (null == tmpNickname) {
            username
        } else {
            tmpNickname
        }
    }

    override fun compareTo(other: User) =
            Comparator
                    .comparing(User::id)
                    .compare(this, other)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "User(id=$id, username='$username', roles=$roles, nickname=$nickname, email=$email, " +
                "benachrichtigunsIntervall=$benachrichtigunsIntervall, includeMeInStatisticMail=$includeMeInStatisticMail)"
    }
}