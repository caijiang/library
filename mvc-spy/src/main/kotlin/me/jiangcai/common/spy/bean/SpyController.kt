package me.jiangcai.common.spy.bean

import me.jiangcai.common.spy.SpyFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.DependsOn
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.codec.Hex
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.security.MessageDigest

/**
 * * GET / for html page
 * * GET /targets for
 * @author CJ
 */
//@Component("spyController")
@Controller
@DependsOn("spyConfigCore")
@RequestMapping("${'$'}{me.jiangcai.common.spy.uri}")
@me.jiangcai.common.ext.annotations.AllOpenClass
class SpyController(
    @Value("${"$"}{me.jiangcai.common.spy.uri:???}")
    val uri: String,
    @Autowired
    private val spyFilter: SpyFilter
) {

    @PreAuthorize("hasAnyRole('ROOT','URI_SPY')")
    @GetMapping("/", "")
    fun well(model: Model): String {
        model.addAttribute("targetUri", "$uri/targets")
        model.addAttribute("resultUri", "$uri/results")
        return "thymeleaf:classpath:/spy/index.html"
    }

    @GetMapping("/targets")
    @ResponseBody
    fun getTargets(): List<String> {
        return spyFilter.targets.map { it.toString() }
    }

    @PreAuthorize("hasAnyRole('ROOT','URI_SPY')")
    @PostMapping("/targets")
    @ResponseStatus(HttpStatus.CREATED)
    fun addTarget(@RequestBody input: String) {
        spyFilter.targets.add(Regex(input))
    }

    @PreAuthorize("hasAnyRole('ROOT','URI_SPY')")
    @DeleteMapping("/targets/{uri}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun deleteTarget(@PathVariable uri: String) {
        val md5 = MessageDigest.getInstance("MD5")
        spyFilter.targets.removeIf {
            String(Hex.encode(md5.digest(it.toString().toByteArray()))) == uri
        }
    }

    @GetMapping("/results")
    @ResponseBody
    fun getResults(): Any {
        return spyFilter.records
    }

    @DeleteMapping("/results")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun deleteResults() {
        spyFilter.records.clear()
    }

    @DeleteMapping("/results/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun deleteResult(@PathVariable id: String) {
        spyFilter.records.removeIf {
            it.id == id
        }
    }

}