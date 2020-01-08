package me.jiangcai.common.resource

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.PropertySource
import org.springframework.core.io.ClassPathResource
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration
import java.io.ByteArrayInputStream
import java.util.*

/**
 * @author CJ
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner::class)
abstract class AbstractResourceServiceTest {

    private val random = Random()

    @Autowired
    private lateinit var resourceService: ResourceService

    @Test
    open fun uploadResource() {
        val randomData = ByteArray(random.nextInt(300) + 400)
        random.nextBytes(randomData)

        val path = UUID.randomUUID().toString()

        val buf = ByteArrayInputStream(randomData)
        var resource = resourceService.uploadResource(path, buf)

        buf.reset()
        assertThat(resource.exists())
            .isTrue()
        assertThat(resource.inputStream)
            .hasSameContentAs(buf)

        resource = resourceService.getResource(path)

        System.out.println(resource.httpUrl())

        buf.reset()
        assertThat(resource.exists())
            .isTrue()
        assertThat(resource.inputStream)
            .hasSameContentAs(buf)

        resourceService.deleteResource(path)
        assertThat(resource.exists())
            .isFalse()

        val path1 = UUID.randomUUID().toString()
        resourceService.uploadResource(path1, buf)
        val path2 = UUID.randomUUID().toString()
        resourceService.moveResource(path2, path1)
        assertThat(resourceService.getResource(path1).exists())
            .isFalse()
        assertThat(resourceService.getResource(path2).exists())
            .isTrue()
        assertThat(resourceService.getResource(path2).inputStream)
            .hasSameContentAs(buf)
        resourceService.deleteResource(path2)


        withImage(ClassPathResource("example.png"), "png")
        withImage(ClassPathResource("example.jpeg"), "jpeg")


    }

    private fun withImage(image: ClassPathResource, type: String) {
        val base = UUID.randomUUID().toString().replace("-", "")
        val rs = resourceService.uploadImage(base, image.inputStream, preview = 10, browse = 20, origin = true)
        assertThat(rs.previewUrl).isNotNull()
        assertThat(rs.browseUrl).isNotNull()
        assertThat(rs.originUrl)
            .isNotNull()
            .endsWith(type)
        println(rs.originUrl)
        resourceService.deleteImage(base)
    }

    @Import(ResourceSpringConfig::class)
    @PropertySource(name = "resourceLD", value = ["classpath:/localResource.properties"])
    class LocalResourceServiceTestConfig

    @Import(ResourceSpringConfig::class)
    @PropertySource(name = "resourceLD", value = ["classpath:/remoteResource.properties"])
    class RemoteResourceServiceTestConfig

    @Import(ResourceSpringConfig::class)
    @PropertySource(name = "resourceLD", value = ["classpath:/ossResource.properties"])
    class OSSResourceServiceTestConfig
}