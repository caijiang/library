package me.jiangcai.common.jdbc

import org.eclipse.persistence.platform.database.DatabasePlatform
import java.sql.Connection
import java.sql.SQLException
import javax.sql.DataSource

/**
 * @author CJ
 */
interface ConnectionProvider {

    fun getConnection(): Connection

    fun getDataSource(): DataSource

    @Throws(SQLException::class)
    fun profile(): DatabasePlatform
}