@file:Suppress("unused")

package me.jiangcai.common.ext.dao

import org.apache.commons.logging.LogFactory
import org.hibernate.exception.ConstraintViolationException
import org.springframework.dao.DataIntegrityViolationException

private val log = LogFactory.getLog(DataIntegrityViolationException::class.java)

/**
 * 违反的约束名称
 * 基于不同的dao层实现，此处会有不同的处理代码。
 */
val DataIntegrityViolationException.constraintName: String?
    get() {
        // check hibernate work
        if (cause?.javaClass?.name == "org.hibernate.exception.ConstraintViolationException")
            return (cause as ConstraintViolationException).constraintName
        log.warn("unknown cause", cause)
        return null
    }
