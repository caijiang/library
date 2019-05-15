package com.mingshz.owner.support

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

/**
 * @author CJ
 */
@Configuration
@ComponentScan("com.mingshz.owner.support.classic")
open class ClassicOwnerConfig {
//    override fun registerBeanDefinitions(
//        importingClassMetadata: AnnotationMetadata?,
//        registry: BeanDefinitionRegistry
//    ) {
//        val bean = GenericBeanDefinition()
//        bean.beanClass = ClassicFindOwnerService::class.java
//
//        registry.registerBeanDefinition("ClassicFindOwnerService", bean)
//    }
}