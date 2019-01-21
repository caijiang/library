package me.jiangcai.common.jdbc.impl

import me.jiangcai.common.jdbc.CloseableConnectionProvider
import me.jiangcai.common.jdbc.ConnectionProvider
import org.eclipse.persistence.platform.database.DatabasePlatform
import org.eclipse.persistence.platform.database.H2Platform
import org.eclipse.persistence.platform.database.MySQLPlatform
import org.eclipse.persistence.platform.database.SQLServerPlatform
import org.springframework.jdbc.datasource.SmartDataSource
import org.springframework.jdbc.support.JdbcUtils
import java.io.PrintWriter
import java.sql.Connection
import java.sql.SQLException
import java.sql.SQLFeatureNotSupportedException
import java.util.logging.Logger
import javax.sql.DataSource

/**
 * @author CJ
 */
abstract class AbstractConnectionProvider : ConnectionProvider {


    @Throws(SQLException::class)
    override fun profile(): DatabasePlatform {
        val connection = getConnection()
        try {
            val databaseName = JdbcUtils.commonDatabaseName(connection.metaData.databaseProductName)
            return if (databaseName.equals("MySQL", ignoreCase = true)) {
                MySQLPlatform()
            } else if (databaseName.equals("Microsoft SQL Server", ignoreCase = true)) {
                SQLServerPlatform()
            } else if (databaseName.equals("H2", ignoreCase = true)) {
                H2Platform()
            } else {
                throw InternalError("unsupported Database $databaseName")
            }
        } finally {
            if (this is CloseableConnectionProvider)
                (this as CloseableConnectionProvider).close(connection)
        }

    }

    override fun getDataSource(): DataSource {
        return object : SmartDataSource {

            override fun shouldClose(con: Connection): Boolean {
                return false
            }

            @Throws(SQLException::class)
            override fun getConnection(): Connection {
                // SmartDataSource
                return this@AbstractConnectionProvider.getConnection()
            }

            @Throws(SQLException::class)
            override fun getConnection(username: String, password: String): Connection {
                return connection
            }

            @Throws(SQLException::class)
            override fun <T> unwrap(iface: Class<T>): T? {
                return null
            }

            @Throws(SQLException::class)
            override fun isWrapperFor(iface: Class<*>): Boolean {
                return false
            }

            @Throws(SQLException::class)
            override fun getLogWriter(): PrintWriter? {
                return null
            }

            @Throws(SQLException::class)
            override fun setLogWriter(out: PrintWriter) {

            }

            @Throws(SQLException::class)
            override fun setLoginTimeout(seconds: Int) {

            }

            @Throws(SQLException::class)
            override fun getLoginTimeout(): Int {
                return 0
            }

            @Throws(SQLFeatureNotSupportedException::class)
            override fun getParentLogger(): Logger? {
                return null
            }
        }
    }
}