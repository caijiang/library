package com.mingshz.qr

import com.mingshz.qr.bean.ImageResolver
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodReturnValueHandler
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

/**
 * @author CJ
 */
@Configuration
@ComponentScan("com.mingshz.qr.bean")
internal open class QRConfig : WebMvcConfigurerAdapter() {


    override fun addReturnValueHandlers(returnValueHandlers: MutableList<HandlerMethodReturnValueHandler>?) {
        super.addReturnValueHandlers(returnValueHandlers)
        returnValueHandlers?.add(ImageResolver())
    }
}