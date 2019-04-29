package com.mingshz.qr.test

import com.mingshz.qr.QRService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.awt.image.BufferedImage

/**
 * @author CJ
 */
@Controller
class HelloController(
    @Autowired
    private val qrService: QRService
) {
    @GetMapping("/echoQR/{input}")
    fun echo(@PathVariable input: String): BufferedImage {
        return qrService.generateQRCode(input)
    }
}