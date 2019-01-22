package me.jiangcai.crud.event;

import lombok.Getter;

/**
 * 实体变化事件
 * 具体类型会有删除，更新，添加
 * 事件发布流程会被锁定在同一个事务中，若事件的处理被意外中断，那么相关事务也会被终止，相当于事件处理具备一定的反向控制权。
 *
 * @author CJ
 */
@SuppressWarnings("WeakerAccess")
public abstract class EntityVariationEvent<T> {

    @Getter
    private final T target;

    protected EntityVariationEvent(T target) {
        this.target = target;
    }

    /**
     * @return 变化类型
     */
    public abstract VariationType getVariationType();
}
