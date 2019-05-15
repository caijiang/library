package com.mingshz.owner.entity

import me.jiangcai.common.jpa.type.JSONStoring
import javax.persistence.*

/**
 * @author CJ
 */
@Entity
open class OwnerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: Long? = null
    @ManyToOne
    var owner: OwnerEntity? = null
    /**
     * 唯一别名
     */
    @Column(length = 40)
    var alias: String? = null
    /**
     * 名称
     */
    @Column(length = 40)
    var name: String? = null

    /**
     * 工作域
     */
    @Column(length = 40)
    var domain: String? = null

    @Column(columnDefinition = "text")
    var wechat: JSONStoring? = null

    fun copy(): OwnerEntity {
        val owner = OwnerEntity()
        owner.owner = this.owner
        owner.alias = alias
        owner.name = name
        owner.domain = domain
        owner.wechat = wechat
        return owner
    }

}