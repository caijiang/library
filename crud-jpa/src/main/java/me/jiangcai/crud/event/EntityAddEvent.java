package me.jiangcai.crud.event;

/**
 * @author CJ
 */
public class EntityAddEvent<T> extends EntityVariationEvent<T> {
    public EntityAddEvent(T target) {
        super(target);
    }

    @Override
    public VariationType getVariationType() {
        return VariationType.Add;
    }
}
