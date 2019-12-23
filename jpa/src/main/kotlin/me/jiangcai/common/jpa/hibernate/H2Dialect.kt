package me.jiangcai.common.jpa.hibernate

import org.hibernate.dialect.function.SQLFunctionTemplate
import org.hibernate.dialect.function.StandardSQLFunction
import org.hibernate.type.StandardBasicTypes

/**
 * @author CJ
 */
class H2Dialect : org.hibernate.dialect.H2Dialect() {
    init {
//        registerFunction("group_concat", SQLFunctionTemplate(StandardBasicTypes.STRING, "group_concat(?1)"))
        registerFunction("group_concat", StandardSQLFunction("group_concat", StandardBasicTypes.STRING))
        registerFunction("ceil", StandardSQLFunction("ceil", StandardBasicTypes.DOUBLE))
        registerFunction("date_diff_seconds", SQLFunctionTemplate(StandardBasicTypes.INTEGER, "DATEDIFF(SECOND,?1,?2)"))

        registerFunction("date_add_hour", SQLFunctionTemplate(StandardBasicTypes.DATE, "DATEADD(HOUR,?1,?2)"))
        registerFunction("date_add_minute", SQLFunctionTemplate(StandardBasicTypes.DATE, "DATEADD(MINUTE,?1,?2)"))

        registerFunction("iso_week", StandardSQLFunction("iso_week", StandardBasicTypes.INTEGER))

    }
}