package me.jiangcai.common.wechat.repository

import me.jiangcai.common.wechat.entity.WechatPayOrder
import org.springframework.data.jpa.repository.JpaRepository

/**
 * @author CJ
 */
interface WechatPayOrderRepository : JpaRepository<WechatPayOrder, String>