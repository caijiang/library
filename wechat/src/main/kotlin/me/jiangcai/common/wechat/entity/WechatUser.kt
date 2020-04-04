package me.jiangcai.common.wechat.entity

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass

/**
 * @author CJ
 */
@Entity
@IdClass(WechatUserPK::class)
data class WechatUser(
    @Id
    val appId: String,
    @Id
    val openId: String,
    var nickname: String? = null,
    var sex: Int? = null,
    var province: String? = null,
    var city: String? = null,
    var country: String? = null,
    var avatarUrl: String? = null,
    var unionId: String? = null,
    var miniSessionKey: String? = null
)