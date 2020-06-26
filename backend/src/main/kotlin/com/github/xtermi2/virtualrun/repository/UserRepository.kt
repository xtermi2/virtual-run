package com.github.xtermi2.virtualrun.repository

import com.github.xtermi2.virtualrun.model.SecurityRole
import com.github.xtermi2.virtualrun.model.User
import com.github.xtermi2.virtualrun.model.UserId
import com.github.xtermi2.virtualrun.security.BcryptPasswordEncoder
import io.quarkus.mongodb.panache.PanacheMongoRepositoryBase
import java.util.stream.Collectors
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class UserRepository(val passwordEncoder: BcryptPasswordEncoder) : PanacheMongoRepositoryBase<User, String> {

    fun findByUsername(username: String): User? {
        return find("username", username)
                .singleResultOptional<User>()
                .orElse(null)
    }

    fun findByUsernameIn(usernames: Set<String>): List<User> {
        // totally ugly, but seems panache does not support list/array parameters
        val paramsString = usernames.mapIndexed { index, _ -> "?${index + 1}" }.joinToString { it }
        return find("{'username' :{\$in : [$paramsString]}}", *usernames.toTypedArray()).list()
    }

    fun findAllUsernames(): List<String> {
        return findAll().stream<User>()
                .map { it.username }
                .distinct()
                .collect(Collectors.toList())
    }

    fun createNewUser(username: String, password: String, roles: Set<SecurityRole>): User {
        val newUser = User(
                id = UserId(),
                username = username,
                password = passwordEncoder.encode(password),
                roles = roles)
        persist(newUser)
        return newUser
    }
}