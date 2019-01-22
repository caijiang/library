package me.jiangcai.crud.row;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
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
 * @author CJ
 */
public class DefaultRowDramatizer extends AbstractMediaRowDramatizer implements RowDramatizer {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<Order> order(List<FieldDefinition> fields, NativeWebRequest webRequest, CriteriaQuery query, CriteriaBuilder criteriaBuilder, Root root) {
        return Collections.emptyList();
    }

    public String getOffsetParameterName() {
        return "offset";
    }

    public int getDefaultSize() {
        return 10;
    }

    public String getSizeParameterName() {
        return "size";
    }

    @Override
    public MediaType toMediaType() {
        return MediaType.APPLICATION_JSON_UTF8;
    }

    @Override
    protected void writeData(Page<?> page, List<Object> rows, NativeWebRequest webRequest) throws IOException {
// i do not know
        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
        try (OutputStream stream = response.getOutputStream()) {
            objectMapper.writeValue(stream, rows);
            stream.flush();
        }
    }

}
