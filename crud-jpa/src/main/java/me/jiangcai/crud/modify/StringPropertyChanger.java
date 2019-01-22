package me.jiangcai.crud.modify;

import org.springframework.stereotype.Component;

/**
 * @author CJ
 */
@Component
public class StringPropertyChanger extends NullablePC {
    @Override
    protected Object nonNullChange(Class type, Object origin) {
        if (origin instanceof String)
            return origin;
        return origin.toString();
    }

    @Override
    public boolean support(Class<?> type) {
        return type == String.class;
    }
}
