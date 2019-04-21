package com.mingshz.login.entity

import me.jiangcai.common.ext.Constant
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne

/**
 * @author CJ
 */
@Entity
data class LoginToken(
    @Id
    @Column(length = 32)
    var id: String? = UUID.randomUUID().toString().replace("-", ""),
    @ManyToOne
    var target: Login? = null,
    /**
     * 创建时间
     * never null
     */
    @Column(columnDefinition = Constant.DATE_COLUMN_DEFINITION)
    var createTime: LocalDateTime = LocalDateTime.now(),
    /**
     * 到期时间
     * never null
     */
    @Column(columnDefinition = Constant.DATE_NULL_ABLE_COLUMN_DEFINITION)
    var expireTime: LocalDateTime? = null

) {
    fun getFullToken(): String {
        return "${target!!.id}_$id"
    }

    companion object {
        fun fromFullToPrincipal(value: String?): Long? {
            return value?.split("_")?.get(0)?.toLong()
        }

        fun fromFullToCredentials(value: String?): String? {
            return value?.split("_")?.get(1)
        }

    }
}