package me.jiangcai.crud.row;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author CJ
 */
public interface RowService {

    /**
     * @param list      查询结果(完整)
     * @param fields    字段定义
     * @param mediaType 期望的媒体类型
     * @return 将查询结果改变成键值对(Map) 列表
     */
    static List<Object> drawEntityToRows(List<?> list, List<? extends IndefiniteFieldDefinition> fields, MediaType mediaType) {
        return list.stream()
                .map((input) -> drawEntityToRow(input, fields, mediaType))
                .collect(Collectors.toList());
    }

    /**
     * @param entity    要进入查询的完整数据
     * @param fields    字段定义
     * @param mediaType 期望的媒体类型
     * @return Map结果
     */
    @SuppressWarnings("unchecked")
    static Map<String, Object> drawEntityToRow(Object entity, List<? extends IndefiniteFieldDefinition> fields, MediaType mediaType) {
        HashMap<String, Object> outData = new HashMap<>();
        Function<List, ?> function = (input) -> drawEntityToRows(input, fields, mediaType);
        for (IndefiniteFieldDefinition fieldDefinition : fields) {
            Object value = fieldDefinition.readValue(entity);
            outData.put(fieldDefinition.name(), fieldDefinition.export(value, mediaType, function));
        }
        return outData;
    }

    /**
     * @param list      查询结果(非完整)
     * @param fields    字段定义
     * @param mediaType 期望的媒体类型
     * @return 将部分查询结果改变成键值对(Map) 列表
     */
    @SuppressWarnings("unchecked")
    static List<Object> drawToRows(List<?> list, List<? extends IndefiniteFieldDefinition> fields, MediaType mediaType) {
        List<Object> rows = new ArrayList<>();
        Function<List, ?> function = (input) -> drawToRows(input, fields, mediaType);
        for (Object data : list) {
// data 通常为一个Object[] 然后fields逐个描述它
            HashMap<String, Object> outData = new HashMap<>();
            if (data.getClass().isArray()) {
                assert Array.getLength(data) == fields.size();
                for (int i = 0; i < fields.size(); i++) {
                    IndefiniteFieldDefinition fieldDefinition = fields.get(i);
                    outData.put(fieldDefinition.name(), fieldDefinition.export(Array.get(data, i), mediaType, function));
                }
            } else {
                // 只有一个结果？
                for (IndefiniteFieldDefinition fieldDefinition : fields) {
                    outData.put(fieldDefinition.name(), fieldDefinition.export(data, mediaType, function));
                }
            }

            rows.add(outData);
        }
        return rows;
    }

    /**
     * @param definition 数据定义
     * @param <T>        实体类型
     * @return 根据查询定义，获取所有的实体
     */
    @Transactional(readOnly = true)
    <T> List<T> queryAllEntity(RowDefinition<T> definition);

    /**
     * @param definition 数据定义
     * @param <T>        实体类型
     * @return 根据查询定义，获取所有的实体
     */
    @Transactional(readOnly = true)
    <T> Page<T> queryEntity(RowDefinition<T> definition, Pageable pageable);

    /**
     * @param rowDefinition       数据定义
     * @param distinct            是否唯一
     * @param customOrderFunction 可选的自定义排序
     * @param pageable            分页
     * @return 获取相关的字段
     */
    Page<?> queryFields(RowDefinition rowDefinition, boolean distinct
            , OrderGenerator customOrderFunction, Pageable pageable);

    /**
     * @param rowDefinition       数据定义
     * @param distinct            是否唯一
     * @param customOrderFunction 可选的自定义排序
     * @return 获取相关的字段
     */
    List<?> queryFields(RowDefinition rowDefinition, boolean distinct
            , OrderGenerator customOrderFunction);
}
