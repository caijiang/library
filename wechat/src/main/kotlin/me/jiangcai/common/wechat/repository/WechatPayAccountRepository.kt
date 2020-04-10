package me.jiangcai.common.wechat.repository

import me.jiangcai.common.wechat.entity.WechatPayAccount
import org.springframework.data.jpa.repository.JpaRepository

/**
 * @author CJ
 */
interface WechatPayAccountRepository : JpaRepository<WechatPayAccount, String>