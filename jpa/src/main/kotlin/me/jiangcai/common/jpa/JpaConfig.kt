package me.jiangcai.common.jpa

import org.springframework.beans.factory.ListableBeanFactory
import org.springframework.beans.factory.config.BeanDefinitionHolder
import org.springframework.beans.factory.config.RuntimeBeanReference
import org.springframework.beans.factory.config.TypedStringValue
import org.springframework.beans.factory.support.*
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar
import org.springframework.core.Ordered
import org.springframework.core.annotation.AnnotationAttributes
import org.springframework.core.type.AnnotationMetadata
import java.sql.DriverManager
import java.util.*

/**
 * @author CJ
 */
internal class JpaConfig : ImportBeanDefinitionRegistrar, Ordered {
    override fun getOrder(): Int = Ordered.LOWEST_PRECEDENCE

    override fun registerBeanDefinitions(
        importingClassMetadata: AnnotationMetadata,
        registry: BeanDefinitionRegistry
    ) {
        val data = importingClassMetadata.getAnnotationAttributes("me.jiangcai.common.jpa.EnableJpa")
        val datasource = data["datasourceName"]!!.toString()
        val prefix = data["prefix"]!!.toString().trim()
        val provider = data["provider"] as JpaProvider
        @Suppress("UNCHECKED_CAST")
        val attributes = data["attributes"] as Array<AnnotationAttributes>
        val useH2TempDataSource = data["useH2TempDataSource"] as Boolean
        val useMysqlDatabase = data["useMysqlDatabase"]!!.toString().trim()

        val dataSourceClass = try {
            Class.forName("com.alibaba.druid.pool.DruidDataSource")
            "com.alibaba.druid.pool.DruidDataSource"
        } catch (e: ClassNotFoundException) {
            "org.springframework.jdbc.datasource.DriverManagerDataSource"
        }

        if (registry is ListableBeanFactory) {
            val ps = mutableSetOf<String>()
            registry.getBeansOfType(JpaPackageScanner::class.java)
                .values
                .forEach { it.addJpaPackage(prefix, ps) }
//            println(ps)
            // attributes 里是 [AnnotationAttributes]
            ps.add("me.jiangcai.common.jpa.type")

//            registry.registerBeanDefinition()
            val emfDefinition = RootBeanDefinition()
            emfDefinition.beanClassName = "org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean"

            when {
                useH2TempDataSource -> {
                    emfDefinition.propertyValues.add(
                        "dataSource",
                        referenceBean(
                            dataSourceClass,
                            mapOf(
                                "driverClassName" to "org.h2.Driver",
                                "url" to "jdbc:h2:mem:${UUID.randomUUID().toString().replace(
                                    "-",
                                    ""
                                )};DB_CLOSE_DELAY=-1"
                            )
                        )
                    )
                }
                useMysqlDatabase.isNotEmpty() -> {
                    val driver = "com.mysql.jdbc.Driver"
                    Class.forName(driver)

                    val host = getEnvOrProperty("mysql.host", "localhost")
                    val url =
                        "jdbc:mysql://${host}:3306/${useMysqlDatabase}?useUnicode=true&characterEncoding=utf8&useServerPrepStmts=false&autoReconnect=true&useSSL=false"

                    try {
                        DriverManager.getConnection(url, useMysqlDatabase, useMysqlDatabase).close()
                    } catch (e: Exception) {
                        throw IllegalStateException(
                            "请在本地(3306)建立一个版本何时的数据库实例，并且执行:" +
                                    "\nCREATE USER '${useMysqlDatabase}'@'%' IDENTIFIED BY '${useMysqlDatabase}';" +
                                    "\nGRANT ALL on ${useMysqlDatabase}.* to '${useMysqlDatabase}'@'%';" +
                                    "\nCREATE DATABASE IF NOT EXISTS $useMysqlDatabase CHARACTER SET = utf8;"
                        )
                    }


                    emfDefinition.propertyValues.add(
                        "dataSource",
                        referenceBean(
                            dataSourceClass,
                            mapOf(
                                "driverClassName" to driver,
                                "url" to url,
                                "username" to useMysqlDatabase,
                                "password" to useMysqlDatabase
                            )
                        )
                    )
                }
                else -> {
                    emfDefinition.propertyValues.add("dataSource", RuntimeBeanReference(datasource))
                }
            }
            // TypedStringValue
            emfDefinition.propertyValues.add(
                "persistenceUnitName",
                TypedStringValue(name(prefix, "persistenceUnitName"))
            )
            // ManagedList<TypedStringValue>
            val ml = ManagedList<TypedStringValue>(ps.size)
            ml.addAll(
                ps.map { TypedStringValue(it) }
            )
            emfDefinition.propertyValues.addPropertyValue("packagesToScan", ml)

            if (provider == JpaProvider.EclipseLink) {
                emfDefinition.propertyValues.addPropertyValue(
                    "persistenceProvider",
                    referenceBean("org.eclipse.persistence.jpa.PersistenceProvider")
                )
                emfDefinition.propertyValues.addPropertyValue(
                    "jpaDialect",
                    referenceBean("org.springframework.orm.jpa.vendor.EclipseLinkJpaDialect")
                )
            } else if (provider == JpaProvider.Hibernate) {
                emfDefinition.propertyValues.addPropertyValue(
                    "persistenceProvider",
                    referenceBean("org.hibernate.jpa.HibernatePersistenceProvider")
//                    referenceBean("org.springframework.orm.jpa.vendor.SpringHibernateJpaPersistenceProvider")
                )
                // prepareConnection
                emfDefinition.propertyValues.addPropertyValue(
                    "jpaDialect",
                    referenceBean(
                        "org.springframework.orm.jpa.vendor.HibernateJpaDialect",
                        mapOf("prepareConnection" to "false")
                    )
                )
            }

            val mp = ManagedMap<String, TypedStringValue>()
            attributes.forEach {
                mp[it.getString("name")] = TypedStringValue(it.getString("value"))
            }

            // 这里设置一些应该固定的属性。
            if (provider == JpaProvider.Hibernate) {
                mp["hibernate.dialect_resolvers"] =
                    TypedStringValue("me.jiangcai.common.jpa.hibernate.MyDialectResolver")
            }

            emfDefinition.propertyValues.addPropertyValue("jpaPropertyMap", mp)

            registry.registerBeanDefinition(name(prefix, "entityManagerFactory"), emfDefinition)

            val txDefinition = RootBeanDefinition()
            txDefinition.beanClassName = "org.springframework.orm.jpa.JpaTransactionManager"
            txDefinition.propertyValues.add(
                "entityManagerFactory",
                RuntimeBeanReference(name(prefix, "entityManagerFactory"))
            )
            registry.registerBeanDefinition(name(prefix, "transactionManager"), txDefinition)

        } else {
            throw IllegalStateException("can not find any JpaPackageScanner.")
        }
    }

    private fun getEnvOrProperty(name: String, default: String): String {
        val env = System.getenv(name)
        if (env != null)
            return env
        return System.getProperty(name, default)
    }

    private fun referenceBean(className: String, stringProperties: Map<String, String> = emptyMap()): Any {
        val bean = GenericBeanDefinition()
        bean.beanClassName = className
        if (stringProperties.isNotEmpty()) {
            stringProperties.forEach { t, u ->
                bean.propertyValues.add(t, TypedStringValue(u))
            }
        }

//        bean.propertyValues.addPropertyValue(PropertyValue(""))
        return BeanDefinitionHolder(bean, className + "#" + Integer.toHexString(bean.hashCode()))
    }

    private fun name(prefix: String, name: String): String {
        if (prefix.isEmpty())
            return (name)
        return (prefix + name.capitalize())
    }
}