package me.jiangcai.crud.row;

import com.fasterxml.jackson.databind.ObjectMapper;
import kotlin.Pair;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.context.request.NativeWebRequest;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 数据重新定制者
 *
 * @author CJ
 */
public interface RowDramatizer {

    /**
     * 算是一个福利吧，免得经常需要用到
     */
    ObjectMapper objectMapper = new ObjectMapper();

    /**
     * @param fields          要显示的字段
     * @param webRequest      请求
     * @param query           实际的请求
     * @param criteriaBuilder cb
     * @param root            root
     * @return 排序规则;可以返回null表示不支持排序
     */
    List<Order> order(List<FieldDefinition> fields, NativeWebRequest webRequest, CriteriaQuery query, CriteriaBuilder criteriaBuilder
            , Root root);

    /**
     * @param webRequest 请求
     * @return 开始查询位；默认0
     */
    int queryOffset(NativeWebRequest webRequest);

    /**
     * @param webRequest 请求
     * @return 查询长度
     */
    int querySize(NativeWebRequest webRequest);

    /**
     * 写入部分响应
     *
     * @param page       结果集
     * @param fields     字段
     * @param webRequest 请求
     * @param initMap  初始的map，这里的数据都会直接写入响应
     * @throws IOException 写入时出现的
     */
    void writeResponse(Page<?> page, List<? extends IndefiniteFieldDefinition> fields, NativeWebRequest webRequest, Map<String, Object> initMap) throws IOException;

    /**
     * 写入所有结果响应
     * 默认中会将list伪装成一个Page以方便实现者只需实现{@link #writeResponse(Page, List, NativeWebRequest, Map)}但依然提供覆盖的可能。
     *
     * @param list       结果集
     * @param fields     字段
     * @param webRequest 请求
     * @throws IOException 写入时出现的
     */
    default void writeResponse(List<?> list, List<? extends IndefiniteFieldDefinition> fields, NativeWebRequest webRequest, Map<String, Object> initMap) throws IOException {
        writeResponse(new Page() {

            @SuppressWarnings("NullableProblems")
            @Override
            public Iterator iterator() {
                return list.iterator();
            }

            @Override
            public int getTotalPages() {
                return 1;
            }

            @Override
            public long getTotalElements() {
                return list.size();
            }

            @Override
            public Page map(Converter converter) {
                throw new IllegalStateException("不应该调用");
            }

            @Override
            public int getNumber() {
                return 0;
            }

            @Override
            public int getSize() {
                return list.size();
            }

            @Override
            public int getNumberOfElements() {
                return list.size();
            }

            @Override
            public List getContent() {
                return list;
            }

            @Override
            public boolean hasContent() {
                return true;
            }

            @Override
            public Sort getSort() {
                throw new IllegalStateException("不应该调用");
            }

            @Override
            public boolean isFirst() {
                return true;
            }

            @Override
            public boolean isLast() {
                return false;
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public Pageable nextPageable() {
                throw new IllegalStateException("不应该调用");
            }

            @Override
            public Pageable previousPageable() {
                throw new IllegalStateException("不应该调用");
            }

        }, fields, webRequest, initMap);
    }

    /**
     * @param type          MVC参数类型
     * @param rowDefinition 相关定义
     * @return 是否支持自行获取数据
     */
    default boolean supportFetch(MethodParameter type, RowDefinition rowDefinition) {
        return false;
    }

    /**
     * 完成数据获取并且写入到响应流
     *
     * @param type          MVC参数类型
     * @param rowDefinition 相关定义
     * @param distinct      distinct
     * @param webRequest    请求
     * @throws IOException 写入时出现的
     */
    @SuppressWarnings("RedundantThrows")
    default void fetchAndWriteResponse(MethodParameter type, RowDefinition rowDefinition, boolean distinct
            , NativeWebRequest webRequest) throws IOException {

    }

    /**
     * 默认实现是通过提交请求参数filter进行(可提交多个filter参数)
     * filter参数的值格式为 <pre>[name]{[_value]}(n)</pre>
     * 比如 <pre>status_1_2</pre> 表示只要status 为 1或者2；仅仅只有name的filter是会被无视的
     *
     * @param request 请求
     * @return 当前请求的过滤器
     */
    default List<Pair<String, List<String>>> queryFilters(NativeWebRequest request) {
        String[] filters = request.getParameterValues("filter");
        if (filters == null)
            return null;
        return Stream.of(filters)
                // 用_切分
                .map(s -> Stream.of(s.split("_"))
                        .filter(s1 -> s1 != null && s1.length() > 0)
                        .toArray(String[]::new))
                // 必须得有2个，第一个是参数，其他的则是选项
                .filter(strings -> strings.length > 1)
                // 转换为所需的Pair
                .map(strings -> new Pair<>(strings[0]
                        , Arrays.asList(Arrays.copyOfRange(strings, 1, strings.length))))
                .collect(Collectors.toList());
    }
}
