package me.jiangcai.common.jdbc

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.metadata.TableMetaDataContext
import org.springframework.jdbc.core.metadata.TableMetaDataProviderFactory
import org.springframework.jdbc.core.metadata.TableParameterMetaData
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

/**
 * @author CJ
 */
@ContextConfiguration(classes = [JdbcSpringConfig::class, DataSourceConfig::class])
@RunWith(SpringJUnit4ClassRunner::class)
open class JdbcServiceTest {


    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    protected lateinit var jdbcService: JdbcService

    @Test
    @Throws(Exception::class)
    fun runJdbcWork() {
        // 删除一个表,再创建一个表如何?
        jdbcService.runJdbcWork { connection ->
            connection.getConnection().createStatement().use { statement ->
                statement.execute("CREATE TABLE simple (id INT NOT NULL,PRIMARY KEY (ID) )")
                // IF NOT EXISTS
            }
        }

        jdbcService.runJdbcWork { connection ->
            val context = TableMetaDataContext()
            context.tableName = "simple"
            val provider = TableMetaDataProviderFactory.createMetaDataProvider(connection.getDataSource(), context)
            assertThat<TableParameterMetaData>(provider.tableParameterMetaData)
                .hasSize(1)
            assertThat(provider.tableParameterMetaData[0].parameterName)
                .isEqualToIgnoringCase("id")
        }

        jdbcService.runJdbcWork { connection ->
            connection.getConnection().createStatement().use { statement -> statement.execute("DROP TABLE `simple`") }
        }
    }

}