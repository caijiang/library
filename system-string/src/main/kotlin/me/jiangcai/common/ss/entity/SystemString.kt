package me.jiangcai.common.ss.entity

import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

/**
 * 用于系统保存信息
 * 最多可以保存的长度为255
 * @author CJ
 */
@Entity
class SystemString {
    @Id
    @Column(length = 50)
    var id: String? = null
    var value: String? = null
    /**
     * java 全限定名称;如果为null则不支持UI更改
     */
    @Column(length = 100)
    var javaTypeName: String? = null
    /**
     * 是否支持运行时更改还是必须重新启动
     */
    var runtime: Boolean = false
    /**
     * 是否运行定制
     */
    var custom: Boolean = false
    /**
     * 更多备注
     */
    @Column(length = 50)
    var comment: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SystemString) return false
        val that = other as SystemString?
        return id == that!!.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    override fun toString(): String {
        return "SystemString{" +
                "id='" + id + '\''.toString() +
                ", value='" + value + '\''.toString() +
                '}'.toString()
    }
}