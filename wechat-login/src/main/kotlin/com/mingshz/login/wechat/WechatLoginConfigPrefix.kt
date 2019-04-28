package com.mingshz.login.wechat

import me.jiangcai.common.jpa.JpaPackageScanner
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@ComponentScan("com.mingshz.login.wechat.filter")
@Configuration
open class WechatLoginConfigPrefix : JpaPackageScanner {
    override fun addJpaPackage(prefix: String, set: MutableSet<String>) {
        set.add("me.jiangcai.wx.standard.entity")
        set.add("com.mingshz.login.wechat.entity")
    }

}