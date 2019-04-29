package com.mingshz.qr.bean

import org.springframework.core.MethodParameter
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodReturnValueHandler
import org.springframework.web.method.support.ModelAndViewContainer
import java.awt.image.RenderedImage
import javax.imageio.ImageIO
import javax.servlet.http.HttpServletResponse

/**
 * @author CJ
 */
class ImageResolver : HandlerMethodReturnValueHandler {

    override fun supportsReturnType(returnType: MethodParameter): Boolean {
        return RenderedImage::class.java.isAssignableFrom(returnType.parameterType)
    }

    @Throws(Exception::class)
    override fun handleReturnValue(
        returnValue: Any,
        returnType: MethodParameter,
        mavContainer: ModelAndViewContainer,
        webRequest: NativeWebRequest
    ) {
        val image = returnValue as RenderedImage

        val response = webRequest.getNativeResponse(HttpServletResponse::class.java)
        response.contentType = "image/png"

        response.outputStream.use { outputStream ->
            ImageIO.write(image, "png", outputStream)
            outputStream.flush()
        }

        mavContainer.isRequestHandled = true
    }
}