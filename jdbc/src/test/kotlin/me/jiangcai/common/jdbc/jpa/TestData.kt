package me.jiangcai.common.jdbc.jpa

import javax.persistence.Entity
import javax.persistence.Id

/**
 * @author CJ
 */
@Entity
class TestData {
    @Id
    var id: String? = null
    var name1: String? = null
}