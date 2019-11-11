package me.jiangcai.common.spy.bean

import me.jiangcai.common.spy.SpyFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

/**
 * * GET / for html page
 * * GET /targets for
 * @author CJ
 */
@Component("spyController")
class SpyController(
    @Autowired
    private val spyFilter: SpyFilter
) {
    @GetMapping("/")
    fun well(): String {
        return "thymeleaf:classpath:/spy/index.html"
    }

    @GetMapping("/targets")
    @ResponseBody
    fun getTargets(): List<String> {
        return spyFilter.targets.map { it.toString() }
    }
}