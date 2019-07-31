package me.jiangcai.crud.env.controller

import me.jiangcai.crud.controller.CrudController
import me.jiangcai.crud.controller.Right
import me.jiangcai.crud.controller.RightTable
import me.jiangcai.crud.env.entity.Item
import me.jiangcai.crud.row.FieldBuilder
import me.jiangcai.crud.row.FieldDefinition
import me.jiangcai.crud.row.RowCustom
import me.jiangcai.crud.row.supplier.AntDesignPaginationDramatizer
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.context.request.WebRequest
import java.util.*
import javax.persistence.Tuple
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Root
import javax.persistence.criteria.Selection

/**
 * 权限定义应该相对简单些
 * 1, pre 获取全部的控制权
 * @author CJ
 */
@Controller
@RequestMapping("/items3")
@RowCustom(dramatizer = AntDesignPaginationDramatizer::class, distinct = true)
class ItemController3 : CrudController<Item, Long, Item>(
    RightTable(
        read = Right.withRoles("R"),
        create = Right.withRoles("C"),
        update = Right.withRoles("U"),
        updateProperty = mapOf(
            "int2" to Right.withRoles("U2"),
            "byte1" to null
        ),
        delete = Right.withRoles("D")
    )
) {
    override fun sampleKeywords(
        principal: Any?,
        allPathVariables: Map<String, String>,
        locale: Locale,
        request: WebRequest
    ): Map<String, (CriteriaBuilder, CriteriaQuery<Tuple>, Root<Item>) -> Selection<*>>? {
        return mapOf(
            "sumInt1" to { cb, _, root -> cb.sum(root.get("int1")) },
            "count" to { cb, _, root -> cb.count(root) }
        )
    }

    override fun listFields(
        principal: Any?,
        locale: Locale,
        builder: FieldBuilder<Item, Item>
    ): List<FieldDefinition<Item>> {
        return listOf(
            builder.forBuilder { it }
                .forSelect("name2", { it, _, _ -> it.get<String>("name") }),
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