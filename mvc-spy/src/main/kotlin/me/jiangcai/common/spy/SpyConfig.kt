package me.jiangcai.common.spy

import org.springframework.beans.factory.config.ConstructorArgumentValues
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.GenericBeanDefinition
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar
import org.springframework.core.type.AnnotationMetadata

/**
 * @author CJ
 */
internal class SpyConfig : ImportBeanDefinitionRegistrar {
    override fun registerBeanDefinitions(
        importingClassMetadata: AnnotationMetadata?,
        registry: BeanDefinitionRegistry?
    ) {
        val data = importingClassMetadata!!.getAnnotationAttributes("me.jiangcai.common.spy.EnableSpy")
        val uri = data["value"]!!.toString()

        val def = GenericBeanDefinition()
        def.beanClass = SpyConfigCore::class.java
        def.constructorArgumentValues = ConstructorArgumentValues()
        def.constructorArgumentValues.addIndexedArgumentValue(0, uri)

        registry?.registerBeanDefinition("spyConfigCore", def)
    }
}