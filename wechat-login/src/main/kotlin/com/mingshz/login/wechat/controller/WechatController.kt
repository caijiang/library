package com.mingshz.login.wechat.controller

import com.mingshz.login.wechat.WechatLoginService
import me.jiangcai.wx.model.WeixinUserDetail
import me.jiangcai.wx.standard.entity.StandardWeixinUser
import me.jiangcai.wx.standard.entity.support.AppIdOpenID
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.SessionAttribute
import org.springframework.web.servlet.view.RedirectView
import java.net.URLEncoder
import javax.servlet.http.HttpSession

/**
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


    //    @PreAuthorize("!isAnonymous()")
    @GetMapping("/auth")
    fun auth(
        @SessionAttribute(
            required = false,
            value = SessionKeyForAppIdOpenID
        ) id: AppIdOpenID?, url: String
    ): RedirectView {
        return if (id == null) {
            RedirectView("/wechat/authCore?url=${URLEncoder.encode(url, "UTF-8")}")
        } else
            RedirectView(url)
    }

    //    @PreAuthorize("permitAll()")
    @GetMapping("/authCore")
    fun authCore(detail: WeixinUserDetail, url: String, session: HttpSession): RedirectView {
        session.setAttribute(SessionKeyForAppIdOpenID, AppIdOpenID(detail.appId, detail.openId))

        wechatLoginService.requireSave(detail)

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