package me.jiangcai.crud.env.controller

import me.jiangcai.crud.controller.CrudController
import me.jiangcai.crud.controller.Right
import me.jiangcai.crud.controller.RightTable
import me.jiangcai.crud.env.entity.Item
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

/**
 * 权限定义应该相对简单些
 * 1, pre 获取全部的控制权
 * @author CJ
 */
@Controller
@RequestMapping("/items2")
class ItemController2 : CrudController<Item, Long, Item>(
    RightTable(
        read = Right.WithRoles("R")
    )
) {

}