package me.jiangcai.crud.modify;

/**
 * 属性修改器
 * 从请求中获取属性值,{@link org.springframework.http.converter.HttpMessageConverter}，并且转变成目标可接受的属性
 *
 * @author CJ
 */
public interface PropertyChanger {
    boolean support(Class<?> type);

    Object change(Class type, Object origin);
}
