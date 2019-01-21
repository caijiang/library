package me.jiangcai.common.jdbc.jpa

import me.jiangcai.common.jdbc.JdbcServiceTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.springframework.jdbc.core.metadata.TableMetaDataContext
import org.springframework.jdbc.core.metadata.TableMetaDataProvider
import org.springframework.jdbc.core.metadata.TableMetaDataProviderFactory
import org.springframework.jdbc.core.metadata.TableParameterMetaData
import org.springframework.test.context.ContextConfiguration
import java.sql.Types

/**
 * @author CJ
 */
@ContextConfiguration(classes = [JpaConfig::class])
class JpaJdbcServiceTest : JdbcServiceTest() {


    @Test
    @Throws(Exception::class)
    fun tableAlertAddColumn() {
        // 首先先删除那个表的玩意儿……
        jdbcService.runJdbcWork { connection ->
            val context = TableMetaDataContext()
            context.tableName = "TestData"
            var provider = TableMetaDataProviderFactory.createMetaDataProvider(connection.getDataSource(), context)
            if (provider.tableParameterMetaData.size > 1) {
                //移除掉多的
                connection.getConnection().createStatement()
                    .use { statement -> statement.execute("ALTER TABLE TestData DROP column name1") }
            }

            provider = TableMetaDataProviderFactory.createMetaDataProvider(connection.getDataSource(), context)
            assertThat<TableParameterMetaData>(provider.tableParameterMetaData)
                .hasSize(1)
        }


        jdbcService.tableAlterAddColumn(TestData::class.java, "name1", null)

        jdbcService.runJdbcWork { connection ->
            val context = TableMetaDataContext()
            context.tableName = "TestData"
            val provider = TableMetaDataProviderFactory.createMetaDataProvider(connection.getDataSource(), context)
            assertThat<TableParameterMetaData>(provider.tableParameterMetaData)
                .hasSize(2)
        }

    }

    private fun columnByName(name: String, provider: TableMetaDataProvider): TableParameterMetaData? {
        for (metaData in provider.tableParameterMetaData) {
            if (name.equals(metaData.parameterName, ignoreCase = true))
                return metaData
        }
        return null
    }

    @Test
    @Throws(Exception::class)
    fun tableAlertModifyColumn() {
        jdbcService.runJdbcWork { connection ->
            val context = TableMetaDataContext()
            context.tableName = "TestData"
            var provider = TableMetaDataProviderFactory.createMetaDataProvider(connection.getDataSource(), context)

            var name1 = columnByName("name1", provider)

            assertThat(name1)
                .isNotNull
            assertThat(name1!!.sqlType)
                .isEqualTo(Types.VARCHAR)


            connection.getConnection().createStatement()
                .use { statement -> statement.execute("ALTER TABLE TestData ALTER name1 INT ") }

            provider = TableMetaDataProviderFactory.createMetaDataProvider(connection.getDataSource(), context)

            name1 = columnByName("name1", provider)

            assertThat(name1)
                .isNotNull
            assertThat(name1!!.sqlType)
                .isEqualTo(Types.INTEGER)

        }

        jdbcService.tableAlterModifyColumn(TestData::class.java, "name1", null)


        jdbcService.runJdbcWork { connection ->
            val context = TableMetaDataContext()
            context.tableName = "TestData"
            val provider = TableMetaDataProviderFactory.createMetaDataProvider(connection.getDataSource(), context)

            val name1 = columnByName("name1", provider)

            assertThat(name1)
                .isNotNull
            assertThat(name1!!.sqlType)
                .isEqualTo(Types.VARCHAR)

        }
    }
}