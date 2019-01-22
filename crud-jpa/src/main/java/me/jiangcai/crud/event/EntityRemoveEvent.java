package me.jiangcai.crud.event;

/**
 * @author CJ
 */
public class EntityRemoveEvent<T> extends EntityVariationEvent<T> {
    public EntityRemoveEvent(T target) {
        super(target);
    }

    @Override
    public VariationType getVariationType() {
        return VariationType.Remove;
    }
}
