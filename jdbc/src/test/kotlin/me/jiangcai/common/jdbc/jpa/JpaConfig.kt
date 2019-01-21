package me.jiangcai.common.jdbc.jpa

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportResource

/**
 * @author CJ
 */
@Configuration
@ImportResource("classpath:/datasource.xml")
open class JpaConfig