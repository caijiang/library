package me.jiangcai.common.resource

import org.springframework.test.context.ContextConfiguration

/**
 * @author CJ
 */
@ContextConfiguration(classes = [AbstractResourceServiceTest.RemoteResourceServiceTestConfig::class])
class RemoteResourceServiceTest : AbstractResourceServiceTest() {
    override fun uploadResource() {
        if (System.getProperty("user.name") != "CJ")
            return
        super.uploadResource()
    }
}