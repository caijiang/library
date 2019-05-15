package com.mingshz.owner.support

import com.mingshz.owner.entity.OwnerEntity
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.GenericBeanDefinition
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar
import org.springframework.core.type.AnnotationMetadata

/**
 * @author CJ
 */
class SingleOwnerConfig : ImportBeanDefinitionRegistrar {
    override fun registerBeanDefinitions(
        importingClassMetadata: AnnotationMetadata,
        registry: BeanDefinitionRegistry
    ) {
        val data = importingClassMetadata.getAnnotationAttributes("com.mingshz.owner.EnableSingleOwner")
        val ownerClass = data["ownerClass"]!!.toString()

        // 把这个类给取出来咯。
        val owner = (Class.forName(ownerClass.removePrefix("class ")).newInstance() as OwnerEntity).copy()

        // 注册这个独特的 ownerEntity
        val bean = GenericBeanDefinition()
        bean.beanClass = SingleFindOwnerService::class.java
        bean.propertyValues.add("ownerEntity", owner)

        registry.registerBeanDefinition("SingleFindOwnerService", bean)
    }
}