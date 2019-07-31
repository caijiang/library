package me.jiangcai.crud.row.supplier;

import me.jiangcai.crud.row.AbstractMediaRowDramatizer;
import me.jiangcai.crud.row.FieldDefinition;
import me.jiangcai.crud.row.RowDramatizer;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.NativeWebRequest;

import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * https://ant.design/components/pagination-cn/
 * 数据将渲染在list,pagination中
 * order的标准暂不明确
 *
 * @author CJ
 */
public class AntDesignPaginationDramatizer extends AbstractMediaRowDramatizer implements RowDramatizer {
    @Override
    public String getOffsetParameterName() {
        throw new IllegalStateException("never!!");
    }

    @Override
    public int getDefaultSize() {
        return 10;
    }

    @Override
    public int queryOffset(NativeWebRequest webRequest) {
        int page = readAsInt(webRequest, "current", 1);
        return (page - 1) * querySize(webRequest);
    }

    @Override
    public String getSizeParameterName() {
        return "pageSize";
    }

    @Override
    public MediaType toMediaType() {
        return MediaType.APPLICATION_JSON_UTF8;
    }

    @Override
    protected void writeData(Page<?> page, List<Object> rows, NativeWebRequest webRequest, Map<String, Object> initMap) throws IOException {
        Map<String, Object> pagination = new HashMap<>();
        pagination.put("current", page.getNumber() + 1);
        pagination.put("pageSize", page.getSize());
        pagination.put("total", page.getTotalElements());

        Map<String, Object> json = initMap == null ? new HashMap<>() : initMap;
        json.put("pagination", pagination);
        json.put("list", rows);
        objectMapper.writeValue(webRequest.getNativeResponse(HttpServletResponse.class).getOutputStream(), json);
    }

    @Override
    public List<Order> order(List<FieldDefinition> fields, NativeWebRequest webRequest, CriteriaQuery query
            , CriteriaBuilder criteriaBuilder
            , Root root) {
        String str = webRequest.getParameter("sorter");
        if (StringUtils.isEmpty(str))
            return null;
        // sorter=username_descend
        // ascend // descend
        try {
            int x = str.lastIndexOf("_");
            String str1 = str.substring(0, x);
            String str2 = str.substring(x + 1);

            final Optional<FieldDefinition> optional = fields.stream().filter(fieldDefinition
                    -> fieldDefinition.name().equals(str1)).findAny();
            if (optional.isPresent()) {
                @SuppressWarnings("unchecked") final Expression order = optional.get().order(query, criteriaBuilder, root);
                if (order == null)
                    return null;
                if (str2.equalsIgnoreCase("ascend"))
                    return Collections.singletonList(criteriaBuilder.asc(order));
                return Collections.singletonList(criteriaBuilder.desc(order));
            }
            return null;
        } catch (Exception ignored) {
            return null;
        }
    }
}
