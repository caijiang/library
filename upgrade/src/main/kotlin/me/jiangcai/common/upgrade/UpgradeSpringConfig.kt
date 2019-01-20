package me.jiangcai.common.upgrade

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

/**
 * @author CJ
 */
@Configuration
@ComponentScan("me.jiangcai.common.upgrade.impl")
open class UpgradeSpringConfig