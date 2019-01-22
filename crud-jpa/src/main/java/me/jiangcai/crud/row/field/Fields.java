package me.jiangcai.crud.row.field;


import me.jiangcai.crud.row.FieldDefinition;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author CJ
 */
public class Fields {
    /**
     * @param name 结果名称
     * @param <T>  Entity类型
     * @return 基础字段
     */
    public static <T> FieldDefinition<T> asBasic(String name) {
        return new BasicField<>(name);
    }

    /**
     * @param name     结果名称
     * @param function 从root中读取结果的函数
     * @param <T>      Entity类型
     * @return 特定表达式字段
     */
    public static <T> FieldDefinition<T> asFunction(String name, Function<Root<T>, Expression<?>> function) {
        return new BasicExpressionField<>(name, function);
    }

    /**
     * @param name     结果名称
     * @param function 从root和cb中读取结果的函数
     * @param <T>      Entity类型
     * @return 特定表达式字段
     */
    public static <T> FieldDefinition<T> asBiFunction(String name
            , BiFunction<Root<T>, CriteriaBuilder, Expression<?>> function) {
        return new BasicExpressionField<>(name, function);
    }

    /**
     * 是为了支持{@link me.jiangcai.crud.row.IndefiniteFieldDefinition#readValue(Object)}而添加
     *
     * @param name           结果名称
     * @param function       从root和cb中读取结果的函数
     * @param entityFunction 直接从entity中获取值的名称
     * @param <T>            Entity类型
     * @return 特定表达式字段
     */
    public static <T> FieldDefinition<T> asBiFunction(String name, BiFunction<Root<T>
            , CriteriaBuilder, Expression<?>> function, Function<T, ?> entityFunction) {
        return new BasicExpressionField<>(name, function, entityFunction);
    }
}
