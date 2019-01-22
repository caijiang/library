package me.jiangcai.crud.row.supplier;

import me.jiangcai.crud.exception.CrudNotFoundException;
import me.jiangcai.crud.row.AbstractMediaRowDramatizer;
import me.jiangcai.crud.row.FieldDefinition;
import me.jiangcai.crud.row.RowDramatizer;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.NativeWebRequest;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

/**
 * 获取一条数据
 *
 * @author helloztt
 */
public class SingleRowDramatizer extends AbstractMediaRowDramatizer implements RowDramatizer {
    @Override
    public List<Order> order(List<FieldDefinition> fields, NativeWebRequest webRequest, CriteriaQuery query, CriteriaBuilder criteriaBuilder, Root root) {
        return Collections.emptyList();
    }

    @Override
    public String getOffsetParameterName() {
        return "offset";
    }

    @Override
    public int getDefaultSize() {
        return 1;
    }

    @Override
    public String getSizeParameterName() {
        return "size";
    }

    @Override
    public MediaType toMediaType() {
        return MediaType.APPLICATION_JSON_UTF8;
    }

    @Override
    protected void writeData(Page<?> page, List<Object> rows, NativeWebRequest webRequest) throws IOException {
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        try (OutputStream stream = response.getOutputStream()) {
            if (CollectionUtils.isEmpty(rows)) {
                throw new CrudNotFoundException();
            } else {
                objectMapper.writeValue(stream, rows.get(0));
            }
            stream.flush();
        }
    }
}
