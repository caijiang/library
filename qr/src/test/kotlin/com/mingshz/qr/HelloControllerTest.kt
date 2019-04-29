package com.mingshz.qr

import me.jiangcai.common.test.MvcTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.awt.image.BufferedImage
import java.awt.image.RenderedImage
import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

/**
 * @author CJ
 */
@ContextConfiguration(classes = [TestConfig::class])
class HelloControllerTest : MvcTest() {

    @Autowired
    private lateinit var qrService: QRService

    @Test
    fun echo() {
        assertThat(
            RenderedImage::class.java.isAssignableFrom(BufferedImage::class.java)
        )
            .isTrue()

        val r = randomMobile()
        val data = mockMvc.perform(get("/echoQR/$r"))
            .andExpect(status().isOk)
            .andExpect(content().contentTypeCompatibleWith(MediaType.IMAGE_PNG))
            .andReturn()
            .response
            .contentAsByteArray

        val image = ImageIO.read(ByteArrayInputStream(data))

        assertThat(qrService.scanImage(image))
            .isEqualTo(r)
    }
}