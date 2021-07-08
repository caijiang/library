package me.jiangcai.common.ext.misc

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody


/**
 * @author CJ
 */
@Controller
@RequestMapping("/echo")
class EchoController {

    @RequestMapping
    @ResponseBody
    fun greeting(): String? {
        return "Hello, World"
    }

    @GetMapping("/{message}")
    @ResponseBody
    fun echo(@PathVariable message: String): String {
        return message
    }
}