package me.jiangcai.common.wechat.entity

import me.jiangcai.common.ext.annotations.NoArgsConstructor
import java.io.Serializable

/**
 * @author CJ
 */
@NoArgsConstructor
data class WechatUserPK(
    val appId: String,
    val openId: String
) : Serializable