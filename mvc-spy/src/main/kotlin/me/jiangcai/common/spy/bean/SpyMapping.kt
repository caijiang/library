package me.jiangcai.common.spy.bean

import me.jiangcai.common.spy.SpyConfigCore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping
import javax.annotation.PostConstruct

/**
 * @author CJ
 */
@Component
class SpyMapping(
    @Autowired
    private val spyController: SpyController,
    @Autowired
    private val spyConfigCore: SpyConfigCore
) : AbstractUrlHandlerMapping() {
    @PostConstruct
    fun init() {
        registerHandler(spyConfigCore.uri, "spyController")
    }
}