package com.mingshz.login.bean

import com.mingshz.login.AuthenticationType
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.support.DefaultMessageSourceResolvable
import org.springframework.security.authentication.*
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Service
import java.util.*

/**
 * @author CJ
 */
@Service("authenticationManager")
class LifeAuthenticationManager(
    @Autowired
    private val applicationContext: ApplicationContext
) : AuthenticationManager {
    private val logger = LogFactory.getLog(LifeAuthenticationManager::class.java)

    private var ps: List<AuthenticationProvider>? = null


    private fun getProviders(): List<AuthenticationProvider> {
        if (ps == null) {
            ps = applicationContext.getBeansOfType(AuthenticationType::class.java)
                .values
                .map { it.authenticationProvider() }
                .toList()
        }
        return ps!!
    }

    override fun authenticate(authentication: Authentication): Authentication {
        val toTest = authentication.javaClass
        var lastException: AuthenticationException? = null
        var result: Authentication? = null
        val debug = logger.isDebugEnabled

        for (provider in getProviders()) {
            if (!provider.supports(toTest)) {
                continue
            }

            if (debug) {
                logger.debug("Authentication attempt using " + provider.javaClass.getName())
            }

            try {
                result = provider.authenticate(authentication)

                if (result != null) {
                    copyDetails(authentication, result)
                    break
                }
            } catch (e: AccountStatusException) {
                prepareException(e, authentication)
                // SEC-546: Avoid polling additional providers if auth failure is due to
                // invalid account status
                throw e
            } catch (e: InternalAuthenticationServiceException) {
                prepareException(e, authentication)
                throw e
            } catch (e: AuthenticationException) {
                lastException = e
            }

        }

//        if (result == null && parent != null) {
//            // Allow the parent to try.
//            try {
//                result = parent.authenticate(authentication)
//            } catch (e: ProviderNotFoundException) {
//                // ignore as we will throw below if no other exception occurred prior to
//                // calling parent and the parent
//                // may throw ProviderNotFound even though a provider in the child already
//                // handled the request
//            } catch (e: AuthenticationException) {
//                lastException = e
//            }
//
//        }

        if (result != null) {
//            if (eraseCredentialsAfterAuthentication && result is CredentialsContainer) {
//                // Authentication is complete. Remove credentials and other secret data
//                // from authentication
//                (result as CredentialsContainer).eraseCredentials()
//            }
//
//            eventPublisher.publishAuthenticationSuccess(result)
            return result
        }

        // Parent was null, or didn't authenticate (or throw an exception).

        if (lastException == null) {
            val msg = DefaultMessageSourceResolvable(
                arrayOf("ProviderManager.providerNotFound")
                , arrayOf(toTest.name)
                , "No AuthenticationProvider found for {0}"
            )
            lastException = ProviderNotFoundException(
                applicationContext.getMessage(msg, Locale.getDefault())
            )
        }

        prepareException(lastException, authentication)

        throw lastException
    }

    private fun prepareException(ex: AuthenticationException, auth: Authentication) {
//        eventPublisher.publishAuthenticationFailure(ex, auth)
    }

    private fun copyDetails(source: Authentication, dest: Authentication) {
        if (dest is AbstractAuthenticationToken && dest.getDetails() == null) {
            dest.details = source.details
        }
    }
}