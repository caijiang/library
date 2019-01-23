package me.jiangcai.common.bs.entity

import javax.persistence.Entity
import javax.persistence.Id

/**
 * 只有一列
 * @author CJ
 */
@Entity
class SimpleRow {
    @Id
    var id: String = "one"
    var value: String = ""
}