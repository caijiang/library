package me.jiangcai.common.wechat

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

/**
 * boot 中还需要通过 EntityScan 加入需要的entity包
 * @author CJ
 */
@Configuration
@EnableJpaRepositories("me.jiangcai.common.wechat.repository")
@ComponentScan("me.jiangcai.common.wechat.controller", "me.jiangcai.common.wechat.service")
class WechatSpringConfig