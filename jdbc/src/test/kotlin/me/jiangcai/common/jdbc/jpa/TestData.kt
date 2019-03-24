package me.jiangcai.common.jdbc.jpa

import me.jiangcai.common.jdbc.DataType
import javax.persistence.Entity
import javax.persistence.Id

/**
 * @author CJ
 */
@Entity
class TestData {
    @Id
    var id: String? = null
    var weight: Int = 0
    var type: DataType? = null
    var name1: String? = null
}