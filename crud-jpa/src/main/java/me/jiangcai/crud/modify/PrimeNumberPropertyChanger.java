package me.jiangcai.crud.modify;

import org.springframework.stereotype.Component;
import org.springframework.util.NumberUtils;

/**
 * @author CJ
 */
@Component
public class PrimeNumberPropertyChanger implements PropertyChanger {
    @Override
    public boolean support(Class<?> type) {
        return type.isPrimitive();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object change(Class type, Object origin) {
        // prime type dose not support null!
        if (origin == null)
            throw new IllegalStateException("primitive type dose not support null!");
//        Boolean.TYPE, Character.TYPE, Byte.TYPE, Short.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, Void.TYPE
        if (type == Boolean.TYPE) {
            if (origin instanceof Boolean)
                return origin;
            return Boolean.parseBoolean(origin.toString());
        } else if (type == Character.TYPE) {
            if (origin instanceof Character)
                return origin;
            return origin.toString().charAt(0);
        } else if (type == Byte.TYPE) {
            if (origin instanceof Number)
                return ((Number) origin).byteValue();
            return NumberUtils.parseNumber(origin.toString(), Byte.class);
        } else if (type == Short.TYPE) {
            if (origin instanceof Number)
                return ((Number) origin).shortValue();
            return NumberUtils.parseNumber(origin.toString(), Short.class);
        } else if (type == Integer.TYPE) {
            if (origin instanceof Number)
                return ((Number) origin).intValue();
            return NumberUtils.parseNumber(origin.toString(), Integer.class);
        } else if (type == Long.TYPE) {
            if (origin instanceof Number)
                return ((Number) origin).longValue();
            return NumberUtils.parseNumber(origin.toString(), Long.class);
        } else if (type == Float.TYPE) {
            if (origin instanceof Number)
                return ((Number) origin).floatValue();
            return NumberUtils.parseNumber(origin.toString(), Float.class);
        } else if (type == Double.TYPE) {
            if (origin instanceof Number)
                return ((Number) origin).doubleValue();
            return NumberUtils.parseNumber(origin.toString(), Double.class);
        }
        throw new IllegalStateException("unknown of type:" + type);
    }
}
