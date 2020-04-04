package me.jiangcai.common.wechat.repository

import me.jiangcai.common.wechat.entity.WechatUser
import me.jiangcai.common.wechat.entity.WechatUserPK
import org.springframework.data.jpa.repository.JpaRepository

/**
 * @author CJ
 */
interface WechatUserRepository : JpaRepository<WechatUser, WechatUserPK>