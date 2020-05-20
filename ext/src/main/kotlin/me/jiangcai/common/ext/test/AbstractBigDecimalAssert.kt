package me.jiangcai.common.ext.test

import org.assertj.core.api.AbstractBigDecimalAssert
import org.assertj.core.data.Offset
import java.math.BigDecimal

/**
 * 金额比较
 */
fun AbstractBigDecimalAssert<*>.isEqualMoneyTo(value: BigDecimal): AbstractBigDecimalAssert<*> {
    return isCloseTo(value, Offset.offset(BigDecimal("0.01")))
}