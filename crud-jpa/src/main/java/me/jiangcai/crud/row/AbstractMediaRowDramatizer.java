package me.jiangcai.crud.row;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.util.NumberUtils;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 直接以一种正文响应写入
 *
 * @author CJ
 */
public abstract class AbstractMediaRowDramatizer implements RowDramatizer {
    protected int readAsInt(NativeWebRequest webRequest, String name, int defaultValue) {
        try {
            return NumberUtils.parseNumber(webRequest.getParameter(name), Integer.class);
        } catch (Exception ignored) {

            return defaultValue;
        }
    }


    @Override
    public int queryOffset(NativeWebRequest webRequest) {
        return readAsInt(webRequest, getOffsetParameterName(), 0);
    }

    public abstract String getOffsetParameterName();

    public abstract int getDefaultSize();

    public abstract String getSizeParameterName();

    public abstract MediaType toMediaType();

    @Override
    public int querySize(NativeWebRequest webRequest) {
        int size = readAsInt(webRequest, getSizeParameterName(), getDefaultSize());
        if (size <= 0)
            size = 10000;
        return size;
    }

    @Override
    public void writeResponse(Page<?> page, List<? extends IndefiniteFieldDefinition> fields, NativeWebRequest webRequest) throws IOException {
        final HttpServletResponse nativeResponse = webRequest.getNativeResponse(HttpServletResponse.class);

        nativeResponse.setHeader("Content-Type", toMediaType().toString());

        List<Object> rows = RowService.drawToRows(page.getContent(), fields, toMediaType());

        writeData(page, rows, webRequest);
    }

    protected abstract void writeData(Page<?> page, List<Object> rows, NativeWebRequest webRequest) throws IOException;


}
