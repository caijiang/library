package me.jiangcai.common.jpa

import me.jiangcai.common.jpa.mysql.MysqlCriteriaFunction
import javax.persistence.criteria.CriteriaBuilder

/**
 * 构建[CriteriaFunction]的Builder
 * @author CJ
 */
class CriteriaFunctionBuilder(
    private val builder: CriteriaBuilder,
    private var platform: String? = null,
    private var timezoneDiff: String? = null
) {

    /**
     * 如果jvm和数据库并非同一个时区，那么用于JPA的java8time Local时间类型时区会存在差异，这里允许做出调整
     * @param timezoneDiff 比如 8:00
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

    fun build(): CriteriaFunction {
        if (platform == null || platform?.equals("mysql", true) == true) {
            return MysqlCriteriaFunction(builder, timezoneDiff)
        }
        throw IllegalArgumentException("not support database platform:$platform")
    }

}