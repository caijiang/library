package me.jiangcai.crud.row.bean

import me.jiangcai.crud.row.*
import org.apache.commons.logging.LogFactory
import org.springframework.beans.BeansException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.core.MethodParameter
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodReturnValueHandler
import org.springframework.web.method.support.ModelAndViewContainer
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Root

/**
 * @author CJ
 */
@Component
class RowDefinitionHandler(
    @Autowired
    private val applicationContext: ApplicationContext,
    @Autowired
    private val rowService: RowService
) : HandlerMethodReturnValueHandler {


    private val log = LogFactory.getLog(RowDefinitionHandler::class.java)

    override fun supportsReturnType(returnType: MethodParameter): Boolean {
        return RowDefinition::class.java.isAssignableFrom(returnType.parameterType)
    }

    //    @SuppressWarnings("unchecked")
    @Throws(Exception::class)
    override fun handleReturnValue(
        returnValue: Any?,
        returnType: MethodParameter,
        mavContainer: ModelAndViewContainer,
        webRequest: NativeWebRequest
    ) {
        val rowDefinition =
            returnValue as? RowDefinition<*> ?: throw IllegalStateException("null can not work for Rows.")

        var rowCustom: RowCustom? = returnType.getMethodAnnotation(RowCustom::class.java)
        if (rowCustom == null) {
            rowCustom = returnType.containingClass.getAnnotation(RowCustom::class.java)
            //            rowCustom = returnType.getDeclaringClass().getAnnotation(RowCustom.class);
        }

        // 看看有没有
        val dramatizer: RowDramatizer
        val distinct: Boolean
        if (rowCustom != null) {
            dramatizer = try {
                applicationContext.getBean(rowCustom.dramatizer.java)
            } catch (ex: BeansException) {
                rowCustom.dramatizer.java.newInstance()
            }

            distinct = rowCustom.distinct
        } else {
            dramatizer = DefaultRowDramatizer()
            distinct = false
        }

        if (dramatizer.supportFetch(returnType, rowDefinition)) {
            dramatizer.fetchAndWriteResponse(returnType, rowDefinition, distinct, webRequest)
            mavContainer.isRequestHandled = true
            return
        }

        val fieldDefinitions = rowDefinition.fields()

        //
        val orderGenerator = { query: CriteriaQuery<*>
                               , cb: CriteriaBuilder, root: Root<*>
            ->
            dramatizer.order(fieldDefinitions, webRequest, query, cb, root)
        }

        if (rowCustom != null && rowCustom.fetchAll) {
            val list = rowService.queryFields(rowDefinition, distinct, orderGenerator)
            dramatizer.writeResponse(list.first, fieldDefinitions, webRequest, list.second)

        } else {
            val startPosition = dramatizer.queryOffset(webRequest)
            val size = dramatizer.querySize(webRequest)

            val filters = dramatizer.queryFilters(webRequest)

            val page = rowService.queryFields(
                rowDefinition, distinct,
                orderGenerator, PageRequest(startPosition / size, size), filters
            )
            dramatizer.writeResponse(page.first, fieldDefinitions, webRequest, page.second)
        }
        mavContainer.isRequestHandled = true

    }
}