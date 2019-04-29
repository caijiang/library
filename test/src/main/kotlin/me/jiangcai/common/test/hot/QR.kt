package me.jiangcai.common.test.hot

import com.mingshz.qr.QRService
import org.springframework.test.web.servlet.ResultActions
import javax.imageio.ImageIO

/**
 * @return 解释QR的结果
 */
fun ResultActions.reverseQR(service: QRService): String {
    return service.scanImage(ImageIO.read(andReturn().response.contentAsByteArray.inputStream()))
}