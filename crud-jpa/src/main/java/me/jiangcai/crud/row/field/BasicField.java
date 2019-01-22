package me.jiangcai.crud.row.field;

/**
 * 最基本的字段；一般都是来自一个实体的简单字段；可以排序
 *
 * @author CJ
 */
public class BasicField<T> extends BasicExpressionField<T> {

    BasicField(String name) {
        super(name, root -> root.get(name));
    }

}
