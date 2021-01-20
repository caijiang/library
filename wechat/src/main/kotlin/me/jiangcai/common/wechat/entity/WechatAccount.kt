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
    /**
     * 支付支持关联的商户号
     */
    @Column(length = 30)
    var merchantId: String? = null,
//    /**
//     * 微信支付的API KEY
//     */
//    var payApiKey: String? = null,
//    /**
//     * 调用微信支付的通知地址前缀
//     * 比如 http://for.bar.com
//     */
//    @Column(length = 100)
//    var paymentNotifyUrlPrefix: String? = null,
    @Column(length = 100)
    var javascriptTicket: String? = null,
    var javascriptTimeToExpire: LocalDateTime? = null,
    @Column(length = 200)
    var accessToken: String? = null,
    var accessTimeToExpire: LocalDateTime? = null
)