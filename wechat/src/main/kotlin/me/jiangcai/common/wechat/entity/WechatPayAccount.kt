package me.jiangcai.common.wechat.entity

import java.io.ByteArrayInputStream
import java.io.File
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Lob


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
    @Column(length = 100)
    val p12FileName: String? = null,
    @Lob
    val p12Data: ByteArray = ByteArray(0),
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
) {
    fun toKeyStore(): java.security.KeyStore {
        val keyStore = java.security.KeyStore.getInstance("PKCS12")

        val stream = if (p12FileName != null) File(p12FileName).inputStream()
        else ByteArrayInputStream(p12Data)

        stream
            .use {
                keyStore.load(it, merchantId.toCharArray())
            }

        return keyStore
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WechatPayAccount) return false

        if (merchantId != other.merchantId) return false

        return true
    }

    override fun hashCode(): Int {
        return merchantId.hashCode()
    }

    override fun toString(): String {
        return "WechatPayAccount(merchantId='$merchantId')"
    }


}