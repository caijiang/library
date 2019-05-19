package me.jiangcai.crud.env.controller

import me.jiangcai.crud.controller.CrudController
import me.jiangcai.crud.controller.Right
import me.jiangcai.crud.controller.RightTable
import me.jiangcai.crud.env.entity.Item
import me.jiangcai.crud.row.FieldBuilder
import me.jiangcai.crud.row.FieldDefinition
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
        ),
        delete = Right.WithRoles("D")
    )
) {
    override fun listFields(builder: FieldBuilder<Item>): List<FieldDefinition<Item>> {
        return listOf(
//            Fields.asBasic("id")
//            , Fields.asBasic("name")
            builder.forField<String>("name")
            , builder.forSelect("nameSize", { root, cb, _ ->
                cb.length(root.get("name"))
            })
            , builder.forField<Int>("int1")
            , builder.forField<Int>("int2", format = { data, _, _ ->
                data?.toString()
            })
//        , BuildField<Item>().build()
        )
    }

}