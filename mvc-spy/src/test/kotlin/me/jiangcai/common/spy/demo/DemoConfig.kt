package me.jiangcai.common.spy.demo

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc

/**
 * @author CJ
 */
@Configuration
@ComponentScan("me.jiangcai.common.spy.demo.controller")
@EnableWebMvc
open class DemoConfig