package me.jiangcai.common.vc.entity

import java.util.*
import javax.persistence.*


/**
 * 验证码
 *
 * @author CJ
 */
@Entity
@IdClass(VerificationCodePK::class)
data class VerificationCode(
    /**
     * 接收者的手机号码
     */
    @Id
    @Column(length = 20)
    val mobile: String,

    /**
     * 验证码类型
     */
    @Id
    val type: Int,

    /**
     * 验证码内容
     */
    @Column(length = 10)
    var code: String,

    /**
     * 发送时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    var sendTime: Calendar
)
