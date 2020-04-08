package me.jiangcai.common.wechat

import me.jiangcai.common.wechat.entity.WechatUser
import org.springframework.transaction.annotation.Transactional

/**
 * 数据模拟服务
 * @author CJ
 */
interface WechatMockDataService {

    /**
     * 模拟一个微信用户
     * 如果并不存在则创建一个新的
     * 协议上与 [WechatApiService.queryUserViaAuthorizationCode],[WechatApiService.queryUserViaMiniAuthorizationCode] 的逻辑一致
     */
    @Transactional
    fun fetchWechatUser(openId: String, appId: String = "test"): WechatUser

}