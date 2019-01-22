package me.jiangcai.crud.utils;

import org.springframework.beans.BeanUtils;

import javax.persistence.Id;
import javax.persistence.IdClass;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author CJ
 * @since 3.0
 */
public class JpaUtils {
    /**
     * @param entityClass 实体类
     * @return 获取这个实体类的id class
     */
    public static Class<? extends Serializable> idClassForEntity(Class<?> entityClass) {
        IdClass idClass = entityClass.getAnnotation(IdClass.class);
        if (idClass != null)
            //noinspection unchecked
            return idClass.value();
        //noinspection unchecked
        return (Class<? extends Serializable>) Stream.of(BeanUtils.getPropertyDescriptors(entityClass))
                // to field
                // NoSuchFieldException
                .map(pd -> {
                    try {
                        return entityClass.getDeclaredField(pd.getName());
                    } catch (NoSuchFieldException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .filter(field -> field.getAnnotation(Id.class) != null)
                .map(Field::getType)
                .findAny()
                .orElseGet(() -> getIdFromMethod(entityClass));
//                .filter(propertyDescriptor -> propertyDescriptor.attributeNames())
    }

    /**
     * @param entityClass 实体类
     * @return 获取这个实体类的id 的名字
     */
    public static String idFieldNameForEntity(Class<?> entityClass) {
        IdClass idClass = entityClass.getAnnotation(IdClass.class);
        if (idClass != null)
            //noinspection unchecked
            return null;
        //noinspection unchecked
        return Stream.of(BeanUtils.getPropertyDescriptors(entityClass))
                // to field
                // NoSuchFieldException
                .map(pd -> {
                    try {
                        return entityClass.getDeclaredField(pd.getName());
                    } catch (NoSuchFieldException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .filter(field -> field.getAnnotation(Id.class) != null)
                .map(Field::getName)
                .findAny()
                .orElseGet(() -> getFieldNameFromMethod(entityClass));
    }

    private static Class getIdFromMethod(Class<?> entityClass) {
        return Stream.of(BeanUtils.getPropertyDescriptors(entityClass))
                .filter(pd -> pd.getReadMethod() != null && pd.getReadMethod().getAnnotation(Id.class) != null)
                .map(PropertyDescriptor::getPropertyType)
                .findAny()
                .orElseGet(() -> {
                    if (entityClass.getSuperclass() == null || entityClass.getSuperclass() == Object.class)
                        throw new IllegalArgumentException(entityClass + " has no @Id field.");
                    @SuppressWarnings("UnnecessaryLocalVariable")
                    Class target = idClassForEntity(entityClass.getSuperclass());
                    return target;
                });
    }

    private static String getFieldNameFromMethod(Class<?> entityClass) {
        return Stream.of(BeanUtils.getPropertyDescriptors(entityClass))
                .filter(pd -> pd.getReadMethod() != null && pd.getReadMethod().getAnnotation(Id.class) != null)
                .map(PropertyDescriptor::getName)
                .findAny()
                .orElseGet(() -> {
                    if (entityClass.getSuperclass() == null || entityClass.getSuperclass() == Object.class)
                        throw new IllegalArgumentException(entityClass + " has no @Id field.");
                    @SuppressWarnings("UnnecessaryLocalVariable")
                    String fieldName = idFieldNameForEntity(entityClass.getSuperclass());
                    return fieldName;
                });
    }
}
