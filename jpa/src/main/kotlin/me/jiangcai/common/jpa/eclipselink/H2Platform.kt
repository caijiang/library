package me.jiangcai.common.jpa.eclipselink

import org.eclipse.persistence.expressions.ExpressionOperator

/**
 * @author CJ
 */
class H2Platform : org.eclipse.persistence.platform.database.H2Platform() {
    override fun initializePlatformOperators() {
        super.initializePlatformOperators()
        addOperator(ExpressionOperator.dateDifference())
    }
}