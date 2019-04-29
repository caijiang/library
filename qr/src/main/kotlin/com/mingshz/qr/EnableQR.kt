package com.mingshz.qr

import org.springframework.context.annotation.Import
import java.lang.annotation.Inherited

/**
 * 启用QR
 * 一个点是允许spring mvc处理[java.awt.image.RenderedImage] 直接为image/png
 * 另一个点是引入[QRService]
 * @author CJ
 */
@Suppress("unused")
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention
@MustBeDocumented
@Inherited
@Import(QRConfig::class)
annotation class EnableQR