package me.jiangcai.common.wechat.entity

import javax.persistence.Column
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
    @Column(length = 30)
    val appId: String,
    @Id
    @Column(length = 30)
    val openId: String,
    @Column(length = 100)
    var nickname: String? = null,
    var sex: Int? = null,
    @Column(length = 100)
    var province: String? = null,
    @Column(length = 100)
    var city: String? = null,
    @Column(length = 100)
    var country: String? = null,
    @Column(length = 200)
    var avatarUrl: String? = null,
    @Column(length = 32)
    var unionId: String? = null,
    @Column(length = 30)
    var miniSessionKey: String? = null
)