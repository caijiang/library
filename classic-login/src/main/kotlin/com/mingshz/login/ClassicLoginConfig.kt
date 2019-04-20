package com.mingshz.login

import com.mingshz.login.password.PasswordFilter
import com.mingshz.login.password.PasswordProvider
import com.mingshz.login.password.UsernamePasswordAuthenticationType
import me.jiangcai.common.jpa.JpaPackageScanner
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.RootBeanDefinition
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

        val df = RootBeanDefinition()
        df.beanClass = ClassicLoginConfigCore::class.java
        registry.registerBeanDefinition("classicLoginConfigCore", df)

        val df2 = RootBeanDefinition()
        df2.beanClass = UsernamePasswordAuthenticationType::class.java
        registry.registerBeanDefinition("usernamePasswordAuthenticationType", df2)
//
//
//        val df4 = RootBeanDefinition()
//        df4.beanClass = LifeAuthenticationManager::class.java
//        registry.registerBeanDefinition("authenticationManager", df4)

        val df3 = RootBeanDefinition()
        df3.beanClass = PasswordFilter::class.java
        registry.registerBeanDefinition("passwordFilter", df3)

        val df4 = RootBeanDefinition()
        df4.beanClass = PasswordProvider::class.java
        registry.registerBeanDefinition("passwordProvider", df4)

    }


}