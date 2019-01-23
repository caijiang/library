package me.jiangcai.common.bs

import org.springframework.context.annotation.*
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 *
 * @author CJ
 */
@Configuration
@Import(MakeBusinessSafeConfig::class)
@ComponentScan("me.jiangcai.common.bs.bean")
@EnableTransactionManagement(mode = AdviceMode.PROXY)
@ImportResource("classpath:/datasource.xml")
open class MakeBusinessSafeConfigTest