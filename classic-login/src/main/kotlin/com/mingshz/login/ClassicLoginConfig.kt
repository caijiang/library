package com.mingshz.login

import me.jiangcai.common.jpa.JpaPackageScanner
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.RootBeanDefinition
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar
import org.springframework.core.type.AnnotationMetadata
import org.springframework.http.MediaType

/**
 * @author CJ
 */
internal class ClassicLoginConfig : ImportBeanDefinitionRegistrar {

    companion object {
        /**
         * 强制通过token授权的token参数名称
         * 通过请求中携带有这个请求参数都可进行强制登录，并且继续业务处理
         */
        var forceAuthenticationTokenParameterName: String = "_token"
        /**
         * 启用经典登录的实体类全限定名称
         */
        var loginClassName: String? = null
        /**
         * 提供登录的uri
         */
        var loginUri: String = "/login"
        /**
         * 登录方法
         */
        var loginMethod: String = "POST"
        /**
         * 允许的登录的请求数据类型
         */
        var loginRequestContentType: String = MediaType.APPLICATION_JSON_VALUE
        var loginRequestUsernameParameterName: String = "username"
        var loginRequestPasswordParameterName: String = "password"
    }

    @Configuration
    open class ClassicLoginConfigCore : JpaPackageScanner {

        override fun addJpaPackage(prefix: String, set: MutableSet<String>) {
            set.add("com.mingshz.login.entity")
            set.add("org.springframework.data.jpa.convert.threeten")
        }
    }


    override fun registerBeanDefinitions(
        importingClassMetadata: AnnotationMetadata,
        registry: BeanDefinitionRegistry
    ) {
        val data = importingClassMetadata.getAnnotationAttributes("com.mingshz.login.EnableClassicLogin")
        data["forceAuthenticationTokenParameterName"]?.let {
            forceAuthenticationTokenParameterName = it.toString()
        }
        data["loginUri"]?.let {
            loginUri = it.toString()
        }
        data["loginMethod"]?.let {
            loginMethod = it.toString()
        }
        data["loginRequestUsernameParameterName"]?.let {
            loginRequestUsernameParameterName = it.toString()
        }
        data["loginRequestPasswordParameterName"]?.let {
            loginRequestPasswordParameterName = it.toString()
        }
        data["loginRequestContentType"]?.let {
            loginRequestContentType = it.toString()
        }
        loginClassName = data["loginClassName"].toString()

        registry.registerBean(ClassicLoginConfigCore::class.java)

        registry.registerBean(TokenConfig::class.java)
        registry.registerBean(PasswordConfig::class.java)
        data["loginExtraConfigClasses"]?.let { list ->
            (list as Array<*>).forEach {
                registry.registerBean(it.toString())
            }
        }
    }


}


@ComponentScan("com.mingshz.login.token")
@Configuration
internal open class TokenConfig

@ComponentScan("com.mingshz.login.password")
@Configuration
internal open class PasswordConfig

private fun BeanDefinitionRegistry.registerBean(type: String) {
    this.registerBean(Class.forName(type))
}
private fun BeanDefinitionRegistry.registerBean(type: Class<*>) {
    val df = RootBeanDefinition()
    df.beanClass = type
    registerBeanDefinition(type.name, df)
}
