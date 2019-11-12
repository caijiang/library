package me.jiangcai.common.spy.demo.controller

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * @author CJ
 */
@Controller
class EchoController {
    @GetMapping("/echo")
    @ResponseBody
    fun echo() = "hello"

    @PostMapping("/echo")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun more() {

    }
}