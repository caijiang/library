package com.mingshz.login.test

import com.mingshz.login.LoginDelegate

/**
 * @author CJ
 */
interface MyLoginDelegate : LoginDelegate {

    //    @PreAuthorize("hasAnyRole('ROOT')")
    fun wellDone()

}