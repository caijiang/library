package me.jiangcai.common.jpa.hibernate

import org.apache.commons.logging.LogFactory
import org.hibernate.dialect.Database
import org.hibernate.dialect.Dialect
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo
import org.hibernate.engine.jdbc.dialect.spi.DialectResolver

/**
 * @author CJ
 */
@Suppress("unused")
class MyDialectResolver : DialectResolver {
    private val log = LogFactory.getLog(MyDialectResolver::class.java)

    override fun resolveDialect(info: DialectResolutionInfo?): Dialect? {
        for (database in Database.values()) {
            val dialect = database.resolveDialect(info)
            if (dialect != null && dialect is org.hibernate.dialect.H2Dialect) {
                return H2Dialect()
            }
            if (dialect != null && dialect is org.hibernate.dialect.MySQLDialect) {
                if (dialect is org.hibernate.dialect.MySQL8Dialect) {
                    return MySQL8Dialect()
                }
                if (dialect is org.hibernate.dialect.MySQL57Dialect) {
                    return MySQL57Dialect()
                }
                if (dialect is org.hibernate.dialect.MySQL55Dialect) {
                    return MySQL55Dialect()
                }
                if (dialect is org.hibernate.dialect.MySQL5Dialect) {
                    return MySQL5Dialect()
                }
                log.warn("JPA is using a strange mysql dialect(${dialect.javaClass})")
                return dialect
            }
            if (dialect != null) {
                return dialect
            }
        }
        return null
    }
}