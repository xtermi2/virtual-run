package com.github.xtermi2.virtualrun.security

import com.github.xtermi2.virtualrun.repository.UserRepository
import io.quarkus.security.AuthenticationFailedException
import io.quarkus.security.identity.AuthenticationRequestContext
import io.quarkus.security.identity.IdentityProvider
import io.quarkus.security.identity.SecurityIdentity
import io.quarkus.security.identity.request.UsernamePasswordAuthenticationRequest
import io.quarkus.security.runtime.QuarkusPrincipal
import io.quarkus.security.runtime.QuarkusSecurityIdentity
import io.smallrye.mutiny.Uni
import org.jboss.logging.Logger
import org.wildfly.security.credential.PasswordCredential
import org.wildfly.security.evidence.PasswordGuessEvidence
import org.wildfly.security.password.Password
import org.wildfly.security.password.util.ModularCrypt
import java.security.spec.InvalidKeySpecException
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class MongoIdentityProvider(val userRepository: UserRepository) : IdentityProvider<UsernamePasswordAuthenticationRequest> {

    private val logger: Logger = Logger.getLogger(MongoIdentityProvider::class.java)

    override fun getRequestType(): Class<UsernamePasswordAuthenticationRequest> = UsernamePasswordAuthenticationRequest::class.java

    override fun authenticate(request: UsernamePasswordAuthenticationRequest?,
                              context: AuthenticationRequestContext?): Uni<SecurityIdentity> {
        return if (context != null) {
            context.runBlocking({
                try {
                    authenticate(request)
                } catch (e: SecurityException) {
                    logger.debug("Authentication failed", e)
                    throw AuthenticationFailedException(e)
                }
            })
        } else {
            throw NullPointerException("Expression 'context' must not be null")
        }
    }

    private fun authenticate(request: UsernamePasswordAuthenticationRequest?): SecurityIdentity? {
        if (null != request) {
            val userDB = userRepository.findByUsername(request.username)
                    ?: throw AuthenticationFailedException("user does not exist")
            val mcfPassword = getMcfPassword(userDB.password)
            val builder = checkPassword(mcfPassword, request)
            return builder
                    .addRoles(setOf("ADMIN", "USER"))
                    .build()
        } else {
            throw AuthenticationFailedException("request is null")
        }
    }

    protected fun checkPassword(storedPassword: Password,
                                request: UsernamePasswordAuthenticationRequest): QuarkusSecurityIdentity.Builder {
        val sentPasswordEvidence = PasswordGuessEvidence(request.password.password)
        val storedPasswordCredential = PasswordCredential(storedPassword)
        if (!storedPasswordCredential.verify(sentPasswordEvidence)) {
            throw AuthenticationFailedException()
        }
        val builder = QuarkusSecurityIdentity.builder()
        builder.setPrincipal(QuarkusPrincipal(request.username))
        builder.addCredential(request.password)
        return builder
    }

    protected fun getMcfPassword(modularCryptFormatPassword: String): Password {
        return try {
            ModularCrypt.decode(modularCryptFormatPassword)
        } catch (e: InvalidKeySpecException) {
            throw IllegalArgumentException("could not decode modular crypt format password!", e)
        }
    }
}