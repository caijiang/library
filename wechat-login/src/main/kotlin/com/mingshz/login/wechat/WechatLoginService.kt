package com.mingshz.login.wechat

import me.jiangcai.wx.model.WeixinUserDetail
import me.jiangcai.wx.standard.entity.StandardWeixinUser
import me.jiangcai.wx.standard.entity.support.AppIdOpenID
import org.springframework.transaction.annotation.Transactional

/**
 * 微信登录相关服务。
 * 在系统中，微信用户和身份系统都可以是1对1的关系。
 * @author CJ
 */
interface WechatLoginService {

    @Transactional(readOnly = true)
    fun findById(id: AppIdOpenID): StandardWeixinUser

    /**
     * 保存它，如果有必要的话。
     * 1. 未持久化过
     * 2. 刷新时间足够老了
     */
    @Transactional
    fun requireSave(detail: WeixinUserDetail)


}