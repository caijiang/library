package com.mingshz.qr

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.EnableWebMvc

/**
 * @author CJ
 */
@Configuration
@EnableQR
@EnableWebMvc
@ComponentScan("com.mingshz.qr.test")
open class TestConfig