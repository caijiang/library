package com.mingshz.login.wechat.test

import me.jiangcai.wx.PublicAccountSupplier
import me.jiangcai.wx.TokenType
import me.jiangcai.wx.model.PublicAccount
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import java.time.LocalDateTime
import java.util.*
import javax.annotation.PostConstruct

/**
 * @author helloztt
 * @since 2018-05-22 20:12
 */
class MyWeixinPublicAccount : PublicAccount(), PublicAccountSupplier
//    , SingletonBusinessOwner, BusinessOwner
{


    private val channelCredential = HashMap<String, Any>()
    //
//    override fun supportNoticeChannel(channel: NoticeChannel?): Boolean {
//        return channel === WechatNoticeChannel.templateMessage
//    }
//
//    override fun id(): String {
//        return "taocai1232323"
//    }
//
//    override fun channelCredential(channel: NoticeChannel?): MutableMap<String, Any> {
//        return channelCredential
//    }
//
//    override fun globalBusinessOwner(): BusinessOwner {
//        return this
//    }
//
//    @Autowired
//    lateinit var sysStringService: SystemStringService
    @Autowired
    lateinit var env: Environment

    @PostConstruct
    fun init() {
        appID = env.getProperty(WeixinProperty.appID, "wx59b0162cdf0967af")
        appSecret = env.getProperty(WeixinProperty.appSecret, "ffcf655fce7c4175bbddae7b594c4e27")
        mchID = env.getProperty(WeixinProperty.mchID, "11473623")
        apiKey = env.getProperty(WeixinProperty.apiKey, "2ab9071b06b9f739b950ddb41db2690d")
        interfaceURL = env.getProperty(WeixinProperty.interfaceURL, "http://localhost/weixin/")
        interfaceToken = env.getProperty(WeixinProperty.interfaceToken, "none")

//        accessToken = sysStringService.getSystemString(WeixinProperty.accessToken, String::class.java, null)
//        javascriptTicket = sysStringService.getSystemString(WeixinProperty.javascriptTicket, String::class.java, null)
//        timeToExpire = sysStringService.getSystemString(WeixinProperty.timeToExpire, LocalDateTime::class.java, null)
//        javascriptTimeToExpire = sysStringService.getSystemString(WeixinProperty.javascriptTimeToExpire, LocalDateTime::class.java, null)
//        channelCredential[WechatNoticeChannel.PublicAccountCredentialFrom] = this
    }

    override fun getAccounts(): List<PublicAccount> {
        return listOf<PublicAccount>(this)
    }

    override fun findByIdentifier(identifier: String?): PublicAccount = this

    override fun updateToken(account: PublicAccount?, type: TokenType?, token: String?, timeToExpire: LocalDateTime?) {
//        when (type) {
//            TokenType.access -> {
//                sysStringService.updateSystemString(WeixinProperty.accessToken, token)
//                sysStringService.updateSystemString(WeixinProperty.timeToExpire, timeToExpire)
//            }
//            TokenType.javascript -> {
//                sysStringService.updateSystemString(WeixinProperty.javascriptTicket, token)
//                sysStringService.updateSystemString(WeixinProperty.javascriptTimeToExpire, timeToExpire)
//            }
//        }
    }

    override fun findByHost(host: String?): PublicAccount = this

    override fun getTokens(account: PublicAccount?) {}

    override fun getSupplier(): PublicAccountSupplier = this
}

private object WeixinProperty {
    const val appID = "huotao.weixin.appId"
    const val appSecret = "huotao.weixin.appSecret"
    const val mchID = "huotao.weixin.mchId"
    const val apiKey = "huotao.weixin.apiKey"
    const val interfaceURL = "huotao.weixin.url"
    const val interfaceToken = "huotao.weixin.token"
    const val accessToken = "huotao.weixin.accessToken"
    const val javascriptTicket = "huotao.weixin.javascriptTicket"
    const val timeToExpire = "huotao.weixin.timeToExpire"
    const val javascriptTimeToExpire = "huotao.weixin.javascriptTimeToExpire"

}
