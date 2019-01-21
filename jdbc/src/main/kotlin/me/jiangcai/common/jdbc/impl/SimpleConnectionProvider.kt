package me.jiangcai.common.jdbc.impl

import java.sql.Connection

/**
 * @author CJ
 */
class SimpleConnectionProvider(private val connection: Connection) : AbstractConnectionProvider() {
    override fun getConnection(): Connection = connection
}