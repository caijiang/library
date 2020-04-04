package me.jiangcai.common.vc.entity

import java.util.*
import javax.persistence.*

/**
 * @author CJ
 */
@Entity
data class VerificationCodeMultiple(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    /**
     * 接收者的手机号码
     */
    @Column(length = 20)
    val mobile: String,

    /**
     * 验证码类型
     */
    val type: Int,

    /**
     * 验证码内容
     */
    @Column(length = 10) var code: String,

    /**
     * 发送时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    var sendTime: Calendar
)