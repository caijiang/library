package me.jiangcai.common.jdbc

import java.sql.Connection

/**
 * @author CJ
 */
interface CloseableConnectionProvider : ConnectionProvider {
    fun close(connection: Connection)
}