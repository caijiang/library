package me.jiangcai.common.jpa.entity

import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.*

/**
 * @author CJ
 */
@Entity
class Foo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @Column(precision = 12, scale = 10)
    var value: BigDecimal? = null
    @Column(columnDefinition = "datetime")
    var created: LocalDateTime = LocalDateTime.now()
}