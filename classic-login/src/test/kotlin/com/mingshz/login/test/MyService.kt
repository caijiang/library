package com.mingshz.login.test

import org.springframework.security.access.prepost.PreAuthorize

/**
 * @author CJ
 */
interface MyService {
    @PreAuthorize("hasAnyRole('ROOT')")
    fun wellDone()
}