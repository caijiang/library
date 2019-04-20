package com.mingshz.login.entity

import me.jiangcai.common.ext.Constant
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime
import javax.persistence.*

/**
 * 经典的一个身份体系
 * @author CJ
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
abstract class Login(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    /**
     * 用户名
     */
    @Column(length = 50) private var username: String? = null,
    /**
     * 密码
     */
    @Column(length = 60) private var password: String? = null,

    /**
     * 手机号
     */
    @Column(length = 20)
    var mobile: String? = null,
    /**
     * Email
     */
    @Column(length = 50)
    var email: String? = null,

    /**
     * 身份证号码
     */
    @Column(length = 20)
    var idCard: String? = null,
    /**
     * 真实姓名
     */
    @Column(length = 20)
    var name: String? = null,
    /**
     * 邀请者
     */
    @ManyToOne(fetch = FetchType.LAZY)
    var inviter: Login? = null,

    /**
     * 是否启用
     */
    var enabled: Boolean = true,

    /**
     * 是否删除
     */
    var deleted: Boolean = false,
    /**
     * 创建时间
     * never null
     */
    @Column(columnDefinition = Constant.DATE_COLUMN_DEFINITION)
    var createTime: LocalDateTime = LocalDateTime.now()
) : UserDetails {


    override fun getUsername(): String? {
        return this.username
    }

    fun setUsername(v: String?) {
        username = v
    }

    override fun getPassword(): String? {
        return this.password
    }

    fun setPassword(v: String?) {
        password = v
    }


    override fun isEnabled(): Boolean = enabled

    override fun isCredentialsNonExpired(): Boolean = enabled

    override fun isAccountNonExpired(): Boolean = enabled

    override fun isAccountNonLocked(): Boolean = enabled
}