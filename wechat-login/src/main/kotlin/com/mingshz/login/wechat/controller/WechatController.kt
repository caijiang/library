package com.mingshz.login.wechat.controller

import com.mingshz.login.entity.Login
import com.mingshz.login.wechat.WechatLoginService
import me.jiangcai.wx.model.WeixinUserDetail
import me.jiangcai.wx.standard.entity.StandardWeixinUser
import me.jiangcai.wx.standard.entity.support.AppIdOpenID
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.SessionAttribute
import org.springframework.web.servlet.view.RedirectView
import java.net.URLEncoder
import javax.servlet.http.HttpSession

/**
 * 要让微信跟原登录系统进行融合
 * 1. /wechat/authCore
 * 1. /wechat/authLogin
 *
 * @author CJ
 */
@Controller
@RequestMapping("/wechat")
//@PreAuthorize("permitAll()")
open class WechatController(
    @Autowired
    private val wechatLoginService: WechatLoginService
) {
//
//    @Autowired
//    private lateinit var wechatLoginService: WechatLoginService

    companion object {
        /**
         * [AppIdOpenID]
         */
        const val SessionKeyForAppIdOpenID = "wechat.SessionKeyForAppIdOpenID"
    }

    @GetMapping("/authLogin")
    fun authLogin(url: String): RedirectView {
        return RedirectView(url)
    }

    //    @PreAuthorize("!isAnonymous()")
    @GetMapping("/auth")
    fun auth(
        @AuthenticationPrincipal
        login: Login?,
        @SessionAttribute(
            required = false,
            value = SessionKeyForAppIdOpenID
        ) id: AppIdOpenID?, url: String
    ): RedirectView {
        return when {
            id == null -> RedirectView("/wechat/authCore?url=${URLEncoder.encode(url, "UTF-8")}")
            login == null -> RedirectView("/wechat/authLogin?url=${URLEncoder.encode(url, "UTF-8")}")
            else -> RedirectView(url)
        }
    }

    //    @PreAuthorize("permitAll()")
    @GetMapping("/authCore")
    fun authCore(
        @AuthenticationPrincipal
        login: Login?, detail: WeixinUserDetail, url: String, session: HttpSession
    ): RedirectView {
        session.setAttribute(SessionKeyForAppIdOpenID, AppIdOpenID(detail.appId, detail.openId))

        wechatLoginService.requireSave(detail)

        if (login == null)
            return RedirectView("/wechat/authLogin?url=${URLEncoder.encode(url, "UTF-8")}")
        return RedirectView(url)
    }

    @GetMapping("/detail")
    @ResponseBody
    fun detail(
        @SessionAttribute(
            required = false,
            value = SessionKeyForAppIdOpenID
        ) id: AppIdOpenID
    ): StandardWeixinUser {
        return wechatLoginService.findById(id)
    }

}