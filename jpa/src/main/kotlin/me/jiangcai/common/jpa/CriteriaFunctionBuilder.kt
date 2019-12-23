package me.jiangcai.common.jpa

import me.jiangcai.common.jpa.mysql.H2CriteriaFunction
import me.jiangcai.common.jpa.mysql.MysqlCriteriaFunction
import org.hibernate.engine.spi.SessionImplementor
import java.sql.Connection
import javax.persistence.EntityManager
import javax.persistence.criteria.CriteriaBuilder

/**
 * 构建[CriteriaFunction]的Builder
 * @author CJ
 */
class CriteriaFunctionBuilder(
    private val builder: CriteriaBuilder,
    private var platform: String? = null,
    private var timezoneDiff: String = "00:00"
) {

    /**
     * 如果jvm和数据库并非同一个时区，那么用于JPA的java8time Local时间类型时区会存在差异，这里允许做出调整
     * @param timezoneDiff 比如 08:00
     */
    fun forTimezoneDiff(timezoneDiff: String): CriteriaFunctionBuilder {
        this.timezoneDiff = timezoneDiff
        return this
    }

    /**
     * 更改数据库供应商
     * @param platform 默认mysql
     */
    fun forPlatform(platform: String): CriteriaFunctionBuilder {
        this.platform = platform
        return this
    }

    fun forEntityManager(entityManager: EntityManager): CriteriaFunctionBuilder {
        try {
            val con = entityManager.unwrap(Connection::class.java)
            platform = con.metaData.databaseProductName
            return this
        } catch (e: RuntimeException) {
            //
            if (entityManager.delegate is SessionImplementor) {
                val con = (entityManager.delegate as SessionImplementor).connection()
                platform = con.metaData.databaseProductName
                return this
            }
            throw e
        }
    }

    fun build(): CriteriaFunction {
        if (platform == null || platform?.equals("mysql", true) == true) {
            return MysqlCriteriaFunction(builder, timezoneDiff)
        }
        if (platform == null || platform?.equals("h2", true) == true) {
            return H2CriteriaFunction(builder, timezoneDiff)
        }
        throw IllegalArgumentException("not support database platform:$platform")
    }

}