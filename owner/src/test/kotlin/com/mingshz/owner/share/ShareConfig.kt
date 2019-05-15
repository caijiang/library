package com.mingshz.owner.share

import me.jiangcai.common.jpa.EnableJpa
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

/**
 * @author CJ
 */
@EnableJpa
//@EnableTransactionManagement(mode = AdviceMode.PROXY)
//@EnableAspectJAutoProxy
@Configuration
@ComponentScan("com.mingshz.owner.share.bean")
open class ShareConfig(
    @Autowired
    private val environment: Environment
) {

    /**
     * 我们需要一个域名为 127.0.0.1 端口为 3307 用户名为 library 密码为 library 的mysql 数据库 测试数据库为 library
     * 可以通过以下语句创建这么一个用户
     * ```sql
     * CREATE USER 'library'@'%' IDENTIFIED BY 'library';
     * GRANT ALL on library.* to 'library'@'%';
     * CREATE DATABASE IF NOT EXISTS library CHARACTER SET = utf8;
     * ```
     * 如果发现无法登录建议删除匿名用户
     * ```sql
     * delete from mysql.user where user='' or user is null;
     * flush privileges;
     * ```
     * 使用mysql 5.7 若本地支持docker 则可以采用跟CI一致的mysql image
     */
    @Bean
    open fun dataSource(): DataSource {
        val ds = DriverManagerDataSource()
        val name = "library"
        ds.setDriverClassName("com.mysql.jdbc.Driver")
        ds.url = "jdbc:mysql://${environment.getProperty(
            "mysql.host",
            "127.0.0.1"
        )}:${environment.getProperty(
            "mysql.port",
            "3307"
        )}/$name?useUnicode=true&characterEncoding=utf8&useServerPrepStmts=false&autoReconnect=true&useSSL=false"
        ds.username = name
        ds.password = name
        return ds
    }

}