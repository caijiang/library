package me.jiangcai.common.jpa.hibernate

import org.hibernate.dialect.function.StandardSQLFunction
import org.hibernate.type.StandardBasicTypes


/**
 * @author CJ
 */
class MySQL8Dialect : org.hibernate.dialect.MySQL8Dialect() {
    init {
        registerFunction("group_concat", StandardSQLFunction("group_concat", StandardBasicTypes.STRING))
    }
}