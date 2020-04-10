package me.jiangcai.common.wechat.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id

/**
 * 微信支付商户
 * @author CJ
 */
@Entity
data class WechatPayAccount(
    /**
     * 支付支持关联的商户号
     */
    @Id
    @Column(length = 30)
    val merchantId: String,
    /**
     * 微信支付的API KEY
     */
    var payApiKey: String? = null,
    /**
     * 调用微信支付的通知地址前缀
     * 比如 http://for.bar.com
     */
    @Column(length = 100)
    var paymentNotifyUrlPrefix: String? = null
)