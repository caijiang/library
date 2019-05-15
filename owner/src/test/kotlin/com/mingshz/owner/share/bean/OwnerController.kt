package com.mingshz.owner.share.bean

import com.mingshz.owner.entity.OwnerEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody

/**
 * @author CJ
 */
@Controller
class OwnerController {

    @GetMapping("/echoOwner")
    @ResponseBody
    fun echoOwner(owner: OwnerEntity): String? {
        return owner.name
    }
}