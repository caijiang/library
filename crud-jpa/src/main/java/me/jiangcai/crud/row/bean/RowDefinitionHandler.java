package me.jiangcai.crud.row.bean;

import me.jiangcai.crud.row.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * @author CJ
 */
@Component
public class RowDefinitionHandler implements HandlerMethodReturnValueHandler {

    private static final Log log = LogFactory.getLog(RowDefinitionHandler.class);
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private RowService rowService;
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return RowDefinition.class.isAssignableFrom(returnType.getParameterType());
    }

    //    @SuppressWarnings("unchecked")
    @SuppressWarnings("unchecked")
    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer
            , NativeWebRequest webRequest) throws Exception {
        RowDefinition rowDefinition = (RowDefinition) returnValue;
        if (rowDefinition == null) {
            throw new IllegalStateException("null can not work for Rows.");
        }

        RowCustom rowCustom = returnType.getMethodAnnotation(RowCustom.class);
        if (rowCustom == null) {
            rowCustom = returnType.getContainingClass().getAnnotation(RowCustom.class);
//            rowCustom = returnType.getDeclaringClass().getAnnotation(RowCustom.class);
        }

        // 看看有没有
        RowDramatizer dramatizer;
        boolean distinct;
        if (rowCustom != null) {
            try {
                dramatizer = applicationContext.getBean(rowCustom.dramatizer());
            } catch (BeansException ex) {
                dramatizer = rowCustom.dramatizer().newInstance();
            }
            distinct = rowCustom.distinct();
        } else {
            dramatizer = new DefaultRowDramatizer();
            distinct = false;
        }

        final RowDramatizer rowDramatizer = dramatizer;

        if (dramatizer.supportFetch(returnType, rowDefinition)) {
            dramatizer.fetchAndWriteResponse(returnType, rowDefinition, distinct, webRequest);
            mavContainer.setRequestHandled(true);
            return;
        }

        final List<FieldDefinition> fieldDefinitions = rowDefinition.fields();

        //
        final OrderGenerator orderGenerator = (query, cb, root)
                -> rowDramatizer.order(fieldDefinitions, webRequest, query, cb, root);

        if (rowCustom != null && rowCustom.fetchAll()) {
            List<?> list = rowService.queryFields(rowDefinition, distinct, orderGenerator);
            dramatizer.writeResponse(list, fieldDefinitions, webRequest);
        } else {
            final int startPosition = dramatizer.queryOffset(webRequest);
            final int size = dramatizer.querySize(webRequest);

            Page<?> page = rowService.queryFields(rowDefinition, distinct,
                    orderGenerator
                    , new PageRequest(startPosition / size, size));
            dramatizer.writeResponse(page, fieldDefinitions, webRequest);
        }
        mavContainer.setRequestHandled(true);

    }

}
