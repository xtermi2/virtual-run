package com.github.xtermi2.virtualrun.repository

import com.github.xtermi2.virtualrun.model.SecurityRole
import io.quarkus.test.junit.QuarkusTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
}