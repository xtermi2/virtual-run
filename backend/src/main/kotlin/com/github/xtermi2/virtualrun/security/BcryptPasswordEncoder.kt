package com.github.xtermi2.virtualrun.security

import io.quarkus.elytron.security.common.BcryptUtil
import org.eclipse.microprofile.config.inject.ConfigProperty
import javax.enterprise.context.ApplicationScoped


@ApplicationScoped
class BcryptPasswordEncoder(
        @ConfigProperty(name = "security.passwordencoder.bcrypt.iterationCount")
        val iterationCount: Int
) {
    fun encode(password: String): String = BcryptUtil.bcryptHash(password, iterationCount)
}