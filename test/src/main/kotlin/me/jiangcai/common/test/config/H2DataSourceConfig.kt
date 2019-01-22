package me.jiangcai.common.test.config

import org.apache.commons.lang3.StringUtils
import org.springframework.jdbc.datasource.DriverManagerDataSource
import java.io.IOException
import javax.sql.DataSource

/**
 * @author CJ
 */
@Suppress("MemberVisibilityCanBePrivate")
open class H2DataSourceConfig {

    /**
     * @param name 名字
     * @return 以文件保存的数据源
     * @since 3.0
     */
    fun fileDataSource(name: String): DataSource {
        val dataSource = DriverManagerDataSource()
        dataSource.setDriverClassName("org.h2.Driver")
        dataSource.url = "jdbc:h2:target/$name"
        return dataSource
    }

    /**
     * @param name 名字
     * @return 内存形式的数据源
     * @since 3.0
     */
    fun memDataSource(name: String): DataSource {
        val dataSource = DriverManagerDataSource()
        dataSource.setDriverClassName("org.h2.Driver")
        dataSource.url = "jdbc:h2:mem:$name;DB_CLOSE_DELAY=-1"
        return dataSource
    }

    /**
     *
     * @param name 名字
     * @param mode 兼容模式 该值可以为：DB2、Derby、HSQLDB、MSSQLServer、MySQL、Oracle、PostgreSQL
     * @return
     */
    fun memDataSource(name: String, mode: String): DataSource {
        if (StringUtils.isEmpty(mode)) {
            return memDataSource(name)
        }
        val dataSource = DriverManagerDataSource()
        dataSource.setDriverClassName("org.h2.Driver")
        dataSource.url = "jdbc:h2:mem:$name;DB_CLOSE_DELAY=-1;MODE=$mode"
        return dataSource
    }


    /**
     * @param name 名字
     * @return 数据源
     * @throws IOException never
     */
    @Deprecated("请使用 {@link #fileDataSource(String)} 代替", ReplaceWith("fileDataSource(name)"))
    @Throws(IOException::class)
    fun dataSource(name: String): DataSource {
        return fileDataSource(name)
    }

}