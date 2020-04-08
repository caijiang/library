package me.jiangcai.common.wechat.service

import me.jiangcai.common.wechat.WechatMockDataService
import me.jiangcai.common.wechat.entity.WechatUser
import me.jiangcai.common.wechat.entity.WechatUserPK
import me.jiangcai.common.wechat.repository.WechatUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

/**
 * @author CJ
 */
@Service
class WechatMockDataServiceImpl(
    @Autowired
    private val wechatUserRepository: WechatUserRepository
) : WechatMockDataService {
    override fun fetchWechatUser(openId: String, appId: String): WechatUser {
        return wechatUserRepository.findByIdOrNull(WechatUserPK(appId, openId))
            ?: wechatUserRepository.save(
                WechatUser(
                    appId = appId,
                    openId = openId,
                    nickname = openId,
                    country = "中国",
                    province = "浙江",
                    city = "杭州",
                    sex = 1,
                    avatarUrl = "https://gw.alipayobjects.com/zos/antfincdn/XAosXuNZyF/BiazfanxmamNRoxxVxka.png"
                )
            )
    }
}