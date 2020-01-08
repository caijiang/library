package me.jiangcai.common.resource

import org.springframework.test.context.ContextConfiguration

/**
 * @author CJ
 */
@ContextConfiguration(classes = [AbstractResourceServiceTest.OSSResourceServiceTestConfig::class])
class OSSResourceServiceTest : AbstractResourceServiceTest()