package me.jiangcai.crud.row;

import java.lang.annotation.*;

/**
 * 可定制Row
 *
 * @author CJ
 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RowCustom {
    /**
     * @return 装饰器
     */
    Class<? extends RowDramatizer> dramatizer() default DefaultRowDramatizer.class;

    /**
     * @return 是否排序重复结果
     */
    boolean distinct();

    /**
     * 在被定义为true只有，支持分页结果的装饰器工作会较为奇怪，具体将表现在缺席渲染分页信息上。
     *
     * @return 不会分页直接返回可以获得的所有结果
     */
    boolean fetchAll() default false;
}
