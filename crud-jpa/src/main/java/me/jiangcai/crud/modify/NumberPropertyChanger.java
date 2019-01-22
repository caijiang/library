package me.jiangcai.crud.modify;

import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author CJ
 */
@Component
public class NumberPropertyChanger extends NullablePC {
    @Override
    public boolean support(Class<?> type) {
        return Number.class.isAssignableFrom(type) || type == Boolean.class || type == Character.class;
    }

    @Override
    public Object nonNullChange(Class type, Object origin) {
        if (type == Boolean.class) {
            if (origin instanceof Boolean)
                return origin;
            return Boolean.parseBoolean(origin.toString());
        } else if (type == Character.class) {
            if (origin instanceof Character)
                return origin;
            return origin.toString().charAt(0);
        } else if (type == Byte.class) {
            if (origin instanceof Number)
                return ((Number) origin).byteValue();
            return NumberUtils.parseNumber(origin.toString(), Byte.class);
        } else if (type == Short.class) {
            if (origin instanceof Number)
                return ((Number) origin).shortValue();
            return NumberUtils.parseNumber(origin.toString(), Short.class);
        } else if (type == Integer.class) {
            if (origin instanceof Number)
                return ((Number) origin).intValue();
            return NumberUtils.parseNumber(origin.toString(), Integer.class);
        } else if (type == Long.class) {
            if (origin instanceof Number)
                return ((Number) origin).longValue();
            return NumberUtils.parseNumber(origin.toString(), Long.class);
        } else if (type == Float.class) {
            if (origin instanceof Number)
                return ((Number) origin).floatValue();
            return NumberUtils.parseNumber(origin.toString(), Float.class);
        } else if (type == Double.class) {
            if (origin instanceof Number)
                return ((Number) origin).doubleValue();
            return NumberUtils.parseNumber(origin.toString(), Double.class);
        } else if (type == BigInteger.class) {
            if (origin instanceof BigInteger)
                return origin;
            if (origin instanceof Number)
                return BigInteger.valueOf(((Number) origin).longValue());
            return BigInteger.valueOf(NumberUtils.parseNumber(origin.toString(), Long.class));
        } else if (type == BigDecimal.class) {
            if (origin instanceof BigDecimal)
                return origin;
            return new BigDecimal(origin.toString());
//            if (origin instanceof Number)
//                return BigDecimal.valueOf(((Number) origin).doubleValue());
//            return BigInteger.valueOf(NumberUtils.parseNumber(origin.toString(),Long.class));
        }
        throw new IllegalStateException("unknown of type:" + type);
    }
}
