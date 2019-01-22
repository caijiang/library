package me.jiangcai.crud.modify;

/**
 * @author CJ
 */
public abstract class NullablePC implements PropertyChanger {
    @Override
    public Object change(Class type, Object origin) {
        if (origin == null)
            return null;
        return nonNullChange(type, origin);
    }

    @SuppressWarnings("WeakerAccess")
    protected abstract Object nonNullChange(Class type, Object origin);
}
