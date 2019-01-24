package me.jiangcai.common.jpa

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.sql.DataSource

/**
 * @author CJ
 */
@RunWith(SpringJUnit4ClassRunner::class)
class EnableJpaTest {

    @PersistenceContext(unitName = "persistenceUnitName")
    private lateinit var entityManager: EntityManager
    @PersistenceContext(unitName = "wellPersistenceUnitName")
    private lateinit var entityManager2: EntityManager

    @Test
    fun go() {
        assertThat(entityManager).isNotNull
        assertThat(entityManager2).isNotNull
    }

    @Configuration
    open class Config1 : JpaPackageScanner {
        override fun addJpaPackage(prefix: String, set: MutableSet<String>) {
            set.add("org.springframework.data.jpa.convert.threeten")
        }
    }

    @EnableJpa(prefix = "well", useH2TempDataSource = true)
    @Configuration
    open class Config2 : JpaPackageScanner {
        override fun addJpaPackage(prefix: String, set: MutableSet<String>) {
            if ("well" == prefix) {
                set.add("me.jiangcai.common.jpa.entity")
            }
        }

    }

    @EnableJpa
    @Configuration
    open class Config {
        /**
         * 我们需要一个域名为 localhost 端口为 3306 用户名为 library 密码为 library 的mysql 数据库 测试数据库为 library
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
         * 建议使用mysql 5.6 若本地支持docker 则可以采用跟CI一致的mysql image
         */
        @Bean
        open fun dataSource(): DataSource {
            val ds = DriverManagerDataSource()
            val name = "library"
            ds.setDriverClassName("com.mysql.jdbc.Driver")
            ds.url =
                    "jdbc:mysql://localhost:3306/$name?useUnicode=true&characterEncoding=utf8&useServerPrepStmts=false&autoReconnect=true&useSSL=false"
            ds.username = name
            ds.password = name
            return ds
        }
    }

//    @EnableJpa(datasourceName = "what")
//    @Configuration
//    open class Config2


}