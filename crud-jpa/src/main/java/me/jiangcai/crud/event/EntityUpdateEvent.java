package me.jiangcai.crud.event;

/**
 * @author CJ
 */
public class EntityUpdateEvent<T> extends EntityVariationEvent<T> {
    public EntityUpdateEvent(T target) {
        super(target);
    }

    @Override
    public VariationType getVariationType() {
        return VariationType.Update;
    }
}
