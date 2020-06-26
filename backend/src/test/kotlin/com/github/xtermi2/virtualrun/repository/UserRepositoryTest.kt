package com.github.xtermi2.virtualrun.repository

import com.github.xtermi2.virtualrun.model.SecurityRole
import com.github.xtermi2.virtualrun.model.User
import com.github.xtermi2.virtualrun.model.UserId
import io.quarkus.test.junit.QuarkusTest
import org.assertj.core.api.Assertions.assertThat
import org.bson.Document
import org.bson.types.ObjectId
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import javax.inject.Inject

@QuarkusTest
class UserRepositoryTest {
    @Inject
    private var userRepository: UserRepository? = null

    @BeforeEach
    internal fun setUp() {
        userRepository!!.deleteAll()
    }

    @Test
    internal fun findById_ObjectIdString() {
        val user = User(id = UserId(ObjectId().toString()),
                username = "andi",
                password = "secret")
        userRepository!!.persist(user)

        val res = userRepository!!.findById(user.id)

        assertThat(res)
                .isEqualTo(user)
    }

    @Test
    internal fun findByUsername_found_existing_user() {
        val username = "andi"
        val newUser = userRepository!!.createNewUser(username, "andi", setOf(SecurityRole.USER))
        assertThat(newUser.id)
                .`as`("newUser.id")
                .isNotNull()

        val res = userRepository!!.findByUsername(username)

        assertThat(res)
                .`as`("findByUsername result")
                .isEqualTo(newUser)
                .isEqualToComparingFieldByField(newUser)
    }

    @Test
    internal fun findByUsername_notFound() {
        val res = userRepository!!.findByUsername("unknown")

        assertThat(res)
                .isNull()
    }

    @Test
    internal fun findByUsernameIn_returns_only_selected_users() {
        val andi = userRepository!!.createNewUser("andi", "andi", setOf(SecurityRole.USER))
        val admin = userRepository!!.createNewUser("admin", "admin", setOf(SecurityRole.ADMIN))
        userRepository!!.createNewUser("foo", "foo", setOf(SecurityRole.USER))

        val res = userRepository!!.findByUsernameIn(setOf(andi.username, admin.username))

        assertThat(res)
                .containsExactlyInAnyOrder(andi, admin)
    }

    @Test
    internal fun findByUsernameIn_nothingFound() {
        userRepository!!.createNewUser("andi", "andi", setOf(SecurityRole.USER))

        val res = userRepository!!.findByUsernameIn(setOf("unknown", "unknown2"))

        assertThat(res)
                .isEmpty()
    }

    @Test
    internal fun findAllUsernames_shouldReturnAllUsernames() {
        val andi = userRepository!!.createNewUser("andi", "andi", setOf(SecurityRole.USER))
        val admin = userRepository!!.createNewUser("admin", "admin", setOf(SecurityRole.ADMIN))

        val res = userRepository!!.findAllUsernames()

        assertThat(res)
                .containsExactlyInAnyOrder(andi.username, admin.username)
    }

    @Test
    internal fun createNewUser_doesEncodePassword() {
        val newUser = userRepository!!.createNewUser("username", "pw", emptySet())

        assertThat(newUser.password)
                .`as`("createNewUser.password")
                .startsWith("\$2a\$04\$")
                .hasSize(60)
    }

    @Test
    internal fun createNewUser_setsUsername() {
        val username = "username"
        val newUser = userRepository!!.createNewUser(username, "pw", emptySet())

        assertThat(newUser.username)
                .`as`("createNewUser.username")
                .isEqualTo(username)
    }

    @Test
    internal fun createNewUser_setsRoles() {
        val newUser = userRepository!!.createNewUser("username", "pw", setOf(SecurityRole.ADMIN))

        assertThat(newUser.roles)
                .`as`("createNewUser.roles")
                .containsExactlyInAnyOrder(SecurityRole.ADMIN)
    }

    @Test
    internal fun convert_ObjectId_to_UserId_field() {
        val id = ObjectId()
        val user = User(
                id = UserId(id),
                username = "name",
                password = "pw"
        )
        val document = Document(mapOf(
                "_id" to id,
                "username" to user.username,
                "password" to user.password,
                "benachrichtigunsIntervall" to user.benachrichtigunsIntervall.name,
                "roles" to user.roles
        ))
        val collection = userRepository!!.mongoDatabase().getCollection("users")
        collection.insertOne(document)


        val res = userRepository!!.findAll().firstResult<User>()


        assertThat(res)
                .isEqualToComparingFieldByField(user)

        assertThat(userRepository!!.findByIdOrObjectId(user.id))
                .`as`("findById")
                .isEqualTo(user)
    }

    @Test
    internal fun convert_String_to_UserId_field() {
        val id = UUID.randomUUID().toString()
        val user = User(
                id = UserId(id),
                username = "name",
                password = "pw"
        )
        val document = Document(mapOf(
                "_id" to id,
                "username" to user.username,
                "password" to user.password,
                "benachrichtigunsIntervall" to user.benachrichtigunsIntervall.name,
                "roles" to user.roles
        ))
        val collection = userRepository!!.mongoDatabase().getCollection("users")
        collection.insertOne(document)


        val res = userRepository!!.findAll().firstResult<User>()


        assertThat(res)
                .isEqualToComparingFieldByField(user)

        assertThat(userRepository!!.findByIdOrObjectId(user.id))
                .`as`("findByIdOrObjectId")
                .isEqualTo(user)

        assertThat(userRepository!!.findById(user.id))
                .`as`("findById")
                .isEqualTo(user)

        assertThat(userRepository!!.findByIdOptional(user.id))
                .`as`("findByIdOptional")
                .contains(user)
    }
}