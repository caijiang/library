package me.jiangcai.common.wechat.entity

import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

/**
 * @author CJ
 */
@Entity
data class WechatAccount(
    @Id
    @Column(length = 30)
    val appId: String,
    var javascriptTicket: String? = null,
    var javascriptTimeToExpire: LocalDateTime? = null,
    var accessToken: String? = null,
    var accessTimeToExpire: LocalDateTime? = null
)