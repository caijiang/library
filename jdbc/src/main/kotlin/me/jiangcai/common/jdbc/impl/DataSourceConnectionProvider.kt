package me.jiangcai.common.jdbc.impl

import me.jiangcai.common.jdbc.CloseableConnectionProvider
import org.springframework.jdbc.datasource.DataSourceUtils
import java.sql.Connection
import javax.sql.DataSource

/**
 * @author CJ
 */
class DataSourceConnectionProvider(private val ds: DataSource) : AbstractConnectionProvider(),
    CloseableConnectionProvider {
    override fun getConnection(): Connection = DataSourceUtils.getConnection(ds)

    override fun close(connection: Connection) {
        DataSourceUtils.releaseConnection(connection, ds)
    }
}