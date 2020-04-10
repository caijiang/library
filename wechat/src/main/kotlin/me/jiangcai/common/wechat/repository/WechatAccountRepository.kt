package me.jiangcai.common.wechat.repository

import me.jiangcai.common.wechat.entity.WechatAccount
import org.springframework.data.jpa.repository.JpaRepository

/**
 * @author CJ
 */
interface WechatAccountRepository : JpaRepository<WechatAccount, String> {
    fun findByMerchantId(merchantId: String): List<WechatAccount>
}