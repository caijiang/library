package com.mingshz.qr.bean

import com.google.zxing.*
import com.google.zxing.client.j2se.BufferedImageLuminanceSource
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import com.google.zxing.qrcode.QRCodeWriter
import com.mingshz.qr.QRService
import org.springframework.stereotype.Service
import java.awt.image.BufferedImage

/**
 * @author CJ
 */
@Service
class QRServiceImpl : QRService {
    override fun generateQRCode(url: String, size: Int): BufferedImage {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(url, BarcodeFormat.QR_CODE, size, size)
        return MatrixToImageWriter.toBufferedImage(bitMatrix)
    }

    override fun scanImage(image: BufferedImage): String {
        val source = BufferedImageLuminanceSource(image)
        val bitmap = BinaryBitmap(HybridBinarizer(source))
        val reader = QRCodeReader()
        try {
            return reader.decode(bitmap).text
        } catch (e: NotFoundException) {
            throw IllegalArgumentException(e)
        } catch (e: ChecksumException) {
            throw IllegalArgumentException(e)
        } catch (e: FormatException) {
            throw IllegalArgumentException(e)
        }
    }
}