package me.jiangcai.common.jdbc

import org.springframework.context.annotation.Bean
import org.springframework.jdbc.datasource.DriverManagerDataSource
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import javax.sql.DataSource

/**
 * @author CJ
 */
open class DataSourceConfig {

    @Bean
    @Throws(IOException::class)
    open fun dataSource(): DataSource {

        val h2 = Paths.get("target", "h2test.h2.db")
        val trace = Paths.get("target", "h2test.trace.db")

        Files.deleteIfExists(h2)
        Files.deleteIfExists(trace)

        val dataSource = DriverManagerDataSource()
        dataSource.setDriverClassName("org.h2.Driver")
        dataSource.url = "jdbc:h2:./target/db"
        return dataSource
    }

}