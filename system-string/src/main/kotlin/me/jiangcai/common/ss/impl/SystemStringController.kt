package me.jiangcai.common.ss.impl

import me.jiangcai.common.ss.SystemStringConfig
import me.jiangcai.common.ss.SystemStringService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.transaction.annotation.Transactional
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

/**
 * @author CJ
 */
@Controller
@PreAuthorize("hasAnyRole('ROOT','" + SystemStringConfig.MANAGER_ROLE + "')")
open class SystemStringController(
    @Autowired
    private val environment: Environment,
    @Autowired
    private val systemStringService: SystemStringService
) {


    @GetMapping("\${ss.uri}")
    @Transactional(readOnly = true)
    open fun index(model: Model): String {
        model.addAttribute("uri", environment.getRequiredProperty("ss.uri"))
        model.addAttribute("list", systemStringService.listCustom())
        return "thymeleaf:classpath:/me/jiangcai/common/ss"
    }

    @DeleteMapping("\${ss.uri}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    open fun delete(id: String) {
        systemStringService.delete(id)
    }

    @PutMapping("\${ss.uri}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    open fun put(id: String, @RequestBody value: String) {
        systemStringService.updateSystemString(id, value)
    }
}