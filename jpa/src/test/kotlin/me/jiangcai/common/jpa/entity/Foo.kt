package me.jiangcai.common.jpa.entity

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
    @Column(columnDefinition = "datetime")
    var created: LocalDateTime = LocalDateTime.now()
}