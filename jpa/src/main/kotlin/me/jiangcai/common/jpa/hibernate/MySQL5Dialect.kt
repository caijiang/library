package me.jiangcai.common.jpa.hibernate

import org.hibernate.dialect.function.StandardSQLFunction
import org.hibernate.type.StandardBasicTypes


/**
 * @author CJ
 */
class MySQL5Dialect : org.hibernate.dialect.MySQL5Dialect() {
    init {
        registerFunction("group_concat", StandardSQLFunction("group_concat", StandardBasicTypes.STRING))
    }
}