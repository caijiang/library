package com.mingshz.qr

import java.awt.image.BufferedImage
import java.io.IOException

/**
 * @author CJ
 */
interface QRService {

    /**
     * 生成二维码
     *
     * @param url 链接
     * @param size 大小
     * @return 二维码图片
     */
    fun generateQRCode(url: String, size: Int = 700): BufferedImage

    /**
     * 扫描二维码
     * @param image 图片
     * @return 二维码内容
     * @throws IOException
     * @throws IllegalArgumentException 找不到二维码
     */
    @Throws(IOException::class, IllegalArgumentException::class)
    fun scanImage(image: BufferedImage): String

}