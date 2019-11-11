package me.jiangcai.common.spy.demo.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

/**
 * @author CJ
 */
@Controller
class EchoController {
    @GetMapping("/echo")
    @ResponseBody
    fun echo() = "hello"
}