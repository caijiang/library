package me.jiangcai.crud.env.controller

import me.jiangcai.crud.controller.CrudController
import me.jiangcai.crud.controller.Right
import me.jiangcai.crud.controller.RightTable
import me.jiangcai.crud.env.entity.Item
import me.jiangcai.crud.row.FieldDefinition
import me.jiangcai.crud.row.field.Fields
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
        read = Right.WithRoles("R"),
        create = Right.WithRoles("C"),
        update = Right.WithRoles("U"),
        updateProperty = mapOf(
            "int2" to Right.WithRoles("U2"),
            "byte1" to null
        )
    )
) {
    override fun listFields(): List<FieldDefinition<Item>> {
        return listOf(Fields.asBasic("id"), Fields.asBasic("name"))
    }

}