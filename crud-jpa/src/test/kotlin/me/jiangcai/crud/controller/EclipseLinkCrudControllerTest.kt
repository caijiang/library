package me.jiangcai.crud.controller

import me.jiangcai.common.jpa.EnableJpa
import me.jiangcai.common.jpa.JpaProvider
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration

/**
 * @author CJ
 */
@ContextConfiguration(classes = [EclipseLinkCrudControllerTest.MyConfig::class])
class EclipseLinkCrudControllerTest : CrudControllerTest() {

    @EnableJpa(useH2TempDataSource = true, provider = JpaProvider.EclipseLink)
    @Configuration
    open class MyConfig
}