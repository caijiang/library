package me.jiangcai.common.resource.bean

import com.fasterxml.jackson.databind.ObjectMapper
import me.jiangcai.common.resource.AbstractResourceServiceTest
import me.jiangcai.common.resource.Resource
import me.jiangcai.common.resource.ResourceService
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration

/**
 * @author CJ
 */
@ContextConfiguration(classes = [AbstractResourceServiceTest.LocalResourceServiceTestConfig::class])
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner::class)
class ResourceJsonModuleTest {


    class TestModel(
        val resource1: Resource? = null,
        val resource2: Resource? = null
    ) {
        override fun toString(): String {
            return "TestModel(resource1=$resource1, resource2=$resource2)"
        }
    }

    @Autowired
    private lateinit var resourceService: ResourceService

    @Autowired
    private lateinit var resourceJsonModule: ResourceJsonModule

    @Test
    fun readAndWrite() {
        // 首先我们得有一个 resourceService
        val objectMapper = ObjectMapper()
        objectMapper.registerModule(resourceJsonModule)


        // 那么
        val json = objectMapper.writeValueAsString(
            TestModel(
                resource1 = resourceService.getResource("foo"),
                resource2 = resourceService.getResource("bar")
            )
        )
        println(
            json
        )
        // read from json
        val value = objectMapper.readValue(json, TestModel::class.java)
        println(value)
        assertThat(value.resource1)
            .isNotNull
        assertThat(value.resource2)
            .isNotNull

        val value2 = objectMapper.readValue("{\"resource1\":\"foo\",\"resource2\":\"bar\"}", TestModel::class.java)
        println(value2)
        assertThat(value2.resource1)
            .isNotNull
        assertThat(value2.resource2)
            .isNotNull


    }

}