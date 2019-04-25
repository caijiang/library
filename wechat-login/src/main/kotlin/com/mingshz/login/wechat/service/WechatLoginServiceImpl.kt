package com.mingshz.login.wechat.service

import com.mingshz.login.wechat.WechatLoginService
import me.jiangcai.wx.model.WeixinUserDetail
import me.jiangcai.wx.standard.entity.StandardWeixinUser
import me.jiangcai.wx.standard.entity.support.AppIdOpenID
import me.jiangcai.wx.standard.repository.StandardWeixinUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime

/**
 * @author CJ
 */
@Service
class WechatLoginServiceImpl(
    @Autowired
    private val standardWeixinUserRepository: StandardWeixinUserRepository
) : WechatLoginService {
    override fun findById(id: AppIdOpenID): StandardWeixinUser {
        return standardWeixinUserRepository.getOne(id)
    }

    override fun requireSave(detail: WeixinUserDetail) {
        val saved = standardWeixinUserRepository.getOne(AppIdOpenID(detail.appId, detail.openId))

        when {
            saved == null -> {
                val w = StandardWeixinUser()
                WeixinUserDetail.Copy(w, detail)
                w.lastRefreshDetailTime = LocalDateTime.now()
                standardWeixinUserRepository.save(w)
            }
            saved.lastRefreshDetailTime == null -> saved.lastRefreshDetailTime = LocalDateTime.now()
            saved.lastRefreshDetailTime < LocalDateTime.now().minusDays(1) -> WeixinUserDetail.Copy(saved, detail)
        }
    }
}