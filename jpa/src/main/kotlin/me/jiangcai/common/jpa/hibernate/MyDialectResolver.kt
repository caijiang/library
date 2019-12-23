package me.jiangcai.common.jpa.hibernate

import org.hibernate.dialect.Database
import org.hibernate.dialect.Dialect
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo
import org.hibernate.engine.jdbc.dialect.spi.DialectResolver

/**
 * @author CJ
 */
@Suppress("unused")
class MyDialectResolver : DialectResolver {
    override fun resolveDialect(info: DialectResolutionInfo?): Dialect? {
        for (database in Database.values()) {
            val dialect = database.resolveDialect(info)
            if (dialect != null && dialect is org.hibernate.dialect.H2Dialect) {
                return H2Dialect()
            }
            if (dialect != null) {
                return dialect
            }
        }
        return null
    }
}