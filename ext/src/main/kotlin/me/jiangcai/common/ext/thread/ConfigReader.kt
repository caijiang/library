package me.jiangcai.common.ext.thread

import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar
import org.springframework.core.type.AnnotationMetadata

/**
 * @author CJ
 */
internal class ConfigReader : ImportBeanDefinitionRegistrar {
    override fun registerBeanDefinitions(
        importingClassMetadata: AnnotationMetadata,
        registry: BeanDefinitionRegistry?
    ) {
        val data = importingClassMetadata.getAnnotationAttributes("me.jiangcai.common.ext.thread.EnableThreadSafe")
        val name = data["value"]!!.toString()

        ThreadSafeConfig.name = name
    }
}