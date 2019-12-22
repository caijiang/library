package me.jiangcai.crud.controller

import me.jiangcai.common.jpa.EnableJpa
import me.jiangcai.common.jpa.JpaProvider
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ContextConfiguration

/**
 * @author CJ
 */
@ContextConfiguration(classes = [HibernateCrudControllerTest.MyConfig::class])
class HibernateCrudControllerTest : CrudControllerTest() {

    @EnableJpa(useH2TempDataSource = true, provider = JpaProvider.Hibernate)
    @Configuration
    open class MyConfig
}