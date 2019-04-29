package com.mingshz.login.wechat

import com.mingshz.login.entity.Login
import me.jiangcai.wx.model.WeixinUserDetail
import me.jiangcai.wx.standard.entity.StandardWeixinUser
import me.jiangcai.wx.standard.entity.support.AppIdOpenID
import org.springframework.transaction.annotation.Transactional
import javax.servlet.http.HttpServletRequest

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

    /**
     * @param id 微信概要信息
     * @return 具体身份，如果可能的话
     */
    @Transactional(readOnly = true)
    fun findLoginById(id: AppIdOpenID): Login?

    @Transactional(readOnly = true)
    fun findAllWechat(login: Login): List<StandardWeixinUser>

    /**
     * 解除绑定
     */
    @Transactional
    fun releaseBind(id: AppIdOpenID)

    /**
     * 解除绑定
     */
    @Transactional
    fun releaseBind(login: Login)

    /**
     * 将特定微信用户绑定到特定身份
     */
    @Transactional
    fun assignWechat(login: Login, wechatId: AppIdOpenID)

    /**
     * 将当前微信用户绑定到特定身份
     */
    @Transactional
    fun assignWechat(login: Login, request: HttpServletRequest)

}