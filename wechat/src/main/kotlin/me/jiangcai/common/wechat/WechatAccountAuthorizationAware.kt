package me.jiangcai.common.wechat

import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.beans.factory.NoUniqueBeanDefinitionException
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import javax.servlet.http.HttpServletRequest

/**
 * 可以知晓微信授权书的办法，如果提供了
 * * wechat.appId
 * * wechat.appSecret
 * 系统参数则会提供一个默认的
 * @author CJ
 */
interface WechatAccountAuthorizationAware {
    fun requestWechatAccountAuthorization(request: HttpServletRequest): WechatAccountAuthorization
}

fun ApplicationContext.requestWechatAccountAuthorization(request: HttpServletRequest): WechatAccountAuthorization {
    val bean = try {
        getBean(WechatAccountAuthorizationAware::class.java)
    } catch (e: NoUniqueBeanDefinitionException) {
        throw IllegalStateException("WechatAccountAuthorizationAware 一个就可以了", e)
    } catch (e: NoSuchBeanDefinitionException) {
        val env = getBean(Environment::class.java)
        return WechatAccountAuthorization(
            accountAppId = env.getProperty("wechat.appId"),
            accountAppSecret = env.getProperty("wechat.appSecret"),
            miniAppId = env.getProperty("wechat.miniAppId"),
            miniAppSecret = env.getProperty("wechat.miniAppSecret")
        )
    }
    return bean.requestWechatAccountAuthorization(request)
}