package me.jiangcai.common.wechat.service

import me.jiangcai.common.ext.help.findOptionalOne
import me.jiangcai.common.wechat.WechatAccountAuthorization
import me.jiangcai.common.wechat.WechatApiService
import me.jiangcai.common.wechat.entity.WechatAccount
import me.jiangcai.common.wechat.entity.WechatUser
import me.jiangcai.common.wechat.entity.WechatUserPK
import me.jiangcai.common.wechat.repository.WechatAccountRepository
import me.jiangcai.common.wechat.repository.WechatUserRepository
import me.jiangcai.common.wechat.util.WechatApiResponseHandler
import org.apache.commons.logging.LogFactory
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.codec.Hex
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.time.LocalDateTime
import java.util.*

/**
 * @author CJ
 */
@Service
class WechatApiServiceImpl(
    @Autowired
    private val wechatUserRepository: WechatUserRepository,
    @Autowired
    private val wechatAccountRepository: WechatAccountRepository
) : WechatApiService {

    private val log = LogFactory.getLog(WechatApiServiceImpl::class.java)

    private fun newClient(): CloseableHttpClient {
        return HttpClientBuilder.create()
            .setDefaultRequestConfig(
                RequestConfig.custom()
                    .setSocketTimeout(15000)
                    .setConnectionRequestTimeout(15000)
                    .setConnectTimeout(15000)
                    .build()
            )
            .build()
    }

    override fun queryUserViaMiniAuthorizationCode(
        authorization: WechatAccountAuthorization,
        code: String
    ): WechatUser {
        if (authorization.miniAppId == null || authorization.miniAppSecret == null)
            throw IllegalStateException("非法的 WechatAccountAuthorization 缺少公众号")

        return newClient().use {
            val rs = it.execute(
                HttpGet("https://api.weixin.qq.com/sns/jscode2session?appid=${authorization.miniAppId}&secret=${authorization.miniAppSecret}&js_code=${code}&grant_type=authorization_code"),
                WechatApiResponseHandler()
            )
//            unionid	string	用户在开放平台的唯一标识符，在满足 UnionID 下发条件的情况下会返回，详见 UnionID 机制说明。
            val openid = rs.getStringOrError("openid")
            val sessionKey = rs.getStringOrError("session_key")

            val usr = wechatUserRepository.findOptionalOne(
                WechatUserPK(
                    authorization.miniAppId, openid
                )
            ) ?: WechatUser(
                appId = authorization.miniAppId,
                openId = openid
            )
            usr.miniSessionKey = sessionKey
            wechatUserRepository.save(usr)
        }
    }

    override fun queryUserViaAuthorizationCode(authorization: WechatAccountAuthorization, code: String): WechatUser {
        if (authorization.accountAppId == null || authorization.accountAppSecret == null)
            throw IllegalStateException("非法的 WechatAccountAuthorization 缺少公众号")
        return newClient().use { client1 ->
            val rs = client1.execute(
                HttpGet("https://api.weixin.qq.com/sns/oauth2/access_token?appid=${authorization.accountAppId}&secret=${authorization.accountAppSecret}&code=${code}&grant_type=authorization_code"),
                WechatApiResponseHandler()
            )

            val access = rs.getStringOrError("access_token")
//            "refresh_token":"REFRESH_TOKEN",
            val openid = rs.getStringOrError("openid")

            newClient().use {
                val rs2 = it.execute(
                    HttpGet("https://api.weixin.qq.com/sns/userinfo?access_token=${access}&openid=${openid}&lang=zh_CN")
                    , WechatApiResponseHandler()
                )

                // 有则改之，无则加勉
                val user = wechatUserRepository.findOptionalOne(WechatUserPK(authorization.accountAppId, openid))
                    ?: WechatUser(
                        appId = authorization.accountAppId,
                        openId = openid
                    )

                user.nickname = rs2.getStringOrError("nickname")
                user.sex = rs.getIntOrError("sex")
                user.province = rs.getStringOrError("province")
                user.city = rs.getStringOrError("city")
                user.country = rs.getStringOrError("country")
                user.avatarUrl = rs.getStringOrError("headimgurl")
                user.unionId = rs.getOptionalString("unionid")

                wechatUserRepository.save(user)
            }
        }
    }

    override fun signature(authorization: WechatAccountAuthorization, url: String): Map<String, Any> {
        if (authorization.accountAppId == null || authorization.accountAppSecret == null)
            throw IllegalStateException("非法的 WechatAccountAuthorization 缺少公众号")

        val token = javascriptToken(authorization)

        val timestamp = (System.currentTimeMillis() / 1000).toString()
        val nonceStr = UUID.randomUUID().toString().replace("-", "")

        log.debug("JS SDK Sign using url:$url")

        val toSign = StringBuilder("jsapi_ticket=")
        toSign.append(token).append("&noncestr=")
        toSign.append(nonceStr).append("&timestamp=")
        toSign.append(timestamp).append("&url=")
        toSign.append(url)

        val toSignBytes = toSign.toString().toByteArray(charset("UTF-8"))
        val messageDigest = MessageDigest.getInstance("sha1")
        messageDigest.update(toSignBytes)
        val signature = String(Hex.encode(messageDigest.digest()))

        return mapOf(
            "appId" to authorization.accountAppId,
            "timestamp" to timestamp,
            "nonceStr" to nonceStr,
            "signature" to signature
        )
    }

    private fun javascriptToken(authorization: WechatAccountAuthorization): String {
        if (authorization.accountAppId == null || authorization.accountAppSecret == null)
            throw IllegalStateException("非法的 WechatAccountAuthorization 缺少公众号")

        val account = wechatAccountRepository.findOptionalOne(authorization.accountAppId)
            ?: WechatAccount((authorization.accountAppId))

        if (account.javascriptTicket != null && account.javascriptTimeToExpire != null && account.javascriptTimeToExpire!!.isBefore(
                LocalDateTime.now()
            )
        ) {
            return account.javascriptTicket!!
        }

        return accessWithToken(account, authorization.accountAppSecret) {
            val method = HttpGet("https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=${it}&type=jsapi")
            newClient().use {
                val root = it.execute(method, WechatApiResponseHandler())
                val ticket = root.getStringOrError("ticket")
                val seconds = root.getIntOrError("expires_in")
                account.javascriptTicket = ticket
                account.javascriptTimeToExpire = LocalDateTime.now().plusSeconds(seconds.toLong())
                wechatAccountRepository.save(account)
                ticket
            }
        }
//        accessToken(account,wechatAppSecret)
    }

    private fun <T> accessWithToken(account: WechatAccount, wechatAppSecret: String, block: (String) -> T): T {
        return block.invoke(getAccessToken(account, wechatAppSecret))

    }

    private fun getAccessToken(account: WechatAccount, wechatAppSecret: String): String {
        if (account.accessToken != null && account.accessTimeToExpire != null && account.accessTimeToExpire!!.isBefore(
                LocalDateTime.now()
            )
        ) {
            return account.accessToken!!
        }

        return newClient().use {
            val method =
                HttpGet("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=${account.appId}&secret=$wechatAppSecret")
            val root = it.execute(method, WechatApiResponseHandler())
            val ticket = root.getStringOrError("access_token")
            val seconds = root.getIntOrError("expires_in")
            account.accessToken = ticket
            account.accessTimeToExpire = LocalDateTime.now().plusSeconds(seconds.toLong())
            wechatAccountRepository.save(account)
            ticket
        }
    }
}
