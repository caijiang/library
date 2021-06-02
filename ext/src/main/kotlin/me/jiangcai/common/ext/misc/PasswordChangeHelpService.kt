package me.jiangcai.common.ext.misc

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.transaction.annotation.Transactional

/**
 * 协助完成密码修改
 * @author CJ
 */
interface PasswordChangeHelpService {
    /**
     * @return 从数据层刷新特定的一个用户详情
     */
    @Transactional(readOnly = true)
    fun refreshPrincipal(details: UserDetails): UserDetails

    /**
     * 更新特定一个用户详情的加密密码，最简单的方式
     */
    fun updateEncodedPassword(details: UserDetails, encodedPassword: String)
}