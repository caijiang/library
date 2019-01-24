package me.jiangcai.common.jpa

import org.springframework.context.annotation.Import
import java.lang.annotation.Inherited


/**
 * 将会在spring中配置一个[javax.persistence.EntityManagerFactory]和[org.springframework.orm.jpa.JpaTransactionManager]
 *
 * 推荐跟[org.springframework.transaction.annotation.EnableTransactionManagement]一起用
 * @author CJ
 */
@Suppress("unused")
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention
@MustBeDocumented
@Inherited
@Import(JpaConfig::class)
@Repeatable
annotation class EnableJpa(
    /**
     * 是否使用临时数据库？
     * 不过我们并没有在类路径里准备好h2。
     */
    val useH2TempDataSource: Boolean = false,
    val datasourceName: String = "dataSource",
    /**
     * bean名称的前置
     *
     *
     * 没有设置[prefix]或者[prefix]保持为空,那么
     * * [javax.persistence.EntityManagerFactory] bean name 为 entityManagerFactory
     * * [org.springframework.orm.jpa.JpaTransactionManager] bean name 为 transactionManager
     * * unitName 为 persistenceUnitName
     *
     * 反之
     * * [javax.persistence.EntityManagerFactory] bean name 为 [prefix]+EntityManagerFactory
     * * [org.springframework.orm.jpa.JpaTransactionManager] bean name 为 [prefix]+TransactionManager
     * * unitName 为 [prefix]+PersistenceUnitName
     */
    val prefix: String = "",
    val provider: JpaProvider = JpaProvider.EclipseLink,
    val attributes: Array<JpaAttribute> = [
        // 支持 emoji
//        JpaAttribute("eclipselink.session.customizer","me.jiangcai.common.jpa.mysql.UTFSessionCustomizer"),
        JpaAttribute("eclipselink.cache.shared.default", "false"),
        JpaAttribute("javax.persistence.schema-generation.database.action", "create"),
        JpaAttribute("eclipselink.weaving", "false"),
        JpaAttribute("eclipselink.logging.level", "FINE")
    ]
)