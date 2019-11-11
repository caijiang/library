package me.jiangcai.common.spy.bean

import me.jiangcai.common.spy.SpyFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.DependsOn
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*

/**
 * * GET / for html page
 * * GET /targets for
 * @author CJ
 */
//@Component("spyController")
@Controller
@DependsOn("spyConfigCore")
@RequestMapping("${'$'}{me.jiangcai.common.spy.uri}")
class SpyController(
    @Value("${"$"}{me.jiangcai.common.spy.uri:???}")
    val uri: String,
    @Autowired
    private val spyFilter: SpyFilter
) {

    @GetMapping("/", "")
    fun well(): String {
        return "thymeleaf:classpath:/spy/index.html"
    }

    @GetMapping("/targets")
    @ResponseBody
    fun getTargets(): List<String> {
        return spyFilter.targets.map { it.toString() }
    }

    @PostMapping("/targets")
    @ResponseStatus(HttpStatus.CREATED)
    fun addTarget(@RequestBody input: String) {
        spyFilter.targets.add(Regex(input))
    }

    @GetMapping("/results")
    @ResponseBody
    fun getResults(): Any {
        return spyFilter.records
    }

}