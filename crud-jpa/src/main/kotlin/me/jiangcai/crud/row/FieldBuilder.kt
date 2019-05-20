package me.jiangcai.crud.row

import me.jiangcai.crud.row.field.fake.AbstractFake
import me.jiangcai.crud.row.field.fake.FakeCriteriaBuilder
import me.jiangcai.crud.row.field.fake.FakeCriteriaQuery
import me.jiangcai.crud.row.field.fake.FakeRoot
import org.springframework.beans.BeanUtils
import org.springframework.core.convert.ConversionService
import org.springframework.http.MediaType
import java.lang.reflect.InvocationTargetException
import java.util.function.Function
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Expression
import javax.persistence.criteria.Root
import javax.persistence.metamodel.SingularAttribute

typealias ToSelect<X, T> = (Root<T>, CriteriaBuilder, CriteriaQuery<*>) -> Expression<out X>
typealias ToFormat = (Any?, MediaType?, exportMe: Function<MutableList<Any?>, *>?) -> Any?

/**
 * 新版本构建器。
 * @author CJ
 */
class FieldBuilder<T>(
    private val type: Class<T>,
    private val conversionService: ConversionService
) {
    /**
     * @return 一个[ConversionService]友好的格式工具
     */
    @Suppress("unused")
    fun formatVia(conversionService: ConversionService = this.conversionService): ToFormat {
        return { value, _, _ ->
            if (value == null) null
            else
                conversionService.convert(value, String::class.java)
        }
    }

    /**
     * 直接用实体的字段名称构建
     * @param field 字段名称
     * @param name 如同[FieldDefinition.name],默认就是字段名称
     * @param order 是否支持排序, 更多细节遵守[RowDramatizer.order]
     * @param format 自定义格式化
     */
    inline fun <reified X> forField(
        field: String,
        name: String = field,
        order: Boolean = true,
        noinline format: ToFormat = { input, _, _ -> input }
    ): TypeFieldDefinition<X, T> {
        return MyField(
            name = name,
            resultType = X::class.java,
            order = order,
            selector = { root, _, _ -> root.get(field) },
            format = format
        )
    }

    /**
     * 支持更富有想象力的字段
     *
     * @param name 如同[FieldDefinition.name]
     * @param selector 如何从持久层中获取需要的数据
     * @param order 是否支持排序, 更多细节遵守[RowDramatizer.order]
     * @param format 自定义格式化
     */
    inline fun <reified X> forSelect(
        name: String,
        noinline selector: ToSelect<X, T>,
        order: Boolean = true,
        noinline format: ToFormat = { input, _, _ -> input }
    ): TypeFieldDefinition<X, T> {
        return MyField(
            name = name,
            resultType = X::class.java,
            order = order,
            selector = selector,
            format = format
        )
    }

    /**
     * 使用jpa metamodel 属性构建
     *
     * @param attribute metamodel 属性
     * @param name 如同[FieldDefinition.name],默认就是字段名称
     * @param order 是否支持排序, 更多细节遵守[RowDramatizer.order]
     * @param format 自定义格式化
     */
    fun <X> forAttribute(
        attribute: SingularAttribute<in T, X>,
        name: String = attribute.name,
        order: Boolean = true,
        format: ToFormat = { input, _, _ -> input }
    ): TypeFieldDefinition<X, T> {
        return MyField(
            name = name,
            resultType = attribute.javaType,
            order = order,
            selector = { root, _, _ -> root.get(attribute) },
            format = format
        )
    }

    inner class MyField<X>(
        private val name: String,
        private val resultType: Class<X>,
        private val selector: ToSelect<X, T>,
        private val order: Boolean,
        private val format: ToFormat
    ) : TypeFieldDefinition<X, T> {
        override fun getResultType(): Class<X> = resultType

        override fun select(cb: CriteriaBuilder, query: CriteriaQuery<*>, root: Root<T>): Expression<out X> {
            return selector(root, cb, query)
        }

        override fun export(origin: Any?, mediaType: MediaType?, exportMe: Function<MutableList<Any?>, *>?): Any? {
            return format(origin, mediaType, exportMe)
        }

        override fun order(
            query: CriteriaQuery<*>,
            criteriaBuilder: CriteriaBuilder,
            root: Root<T>
        ): Expression<*>? {
            if (!order)
                return null
            return select(criteriaBuilder, query, root)
        }

        override fun name(): String = name

        override fun readValue(entity: T): Any {
//        if (entityFunction != null)
//            return entityFunction.apply(entity)
            return try {
                val fake = select(FakeCriteriaBuilder(), FakeCriteriaQuery(), FakeRoot<T>()) as AbstractFake
                fake.toValue(entity)
            } catch (e: Throwable) {
                try {
                    BeanUtils.getPropertyDescriptor(type, name()).readMethod.invoke(entity)
                } catch (e: IllegalAccessException) {
                    throw IllegalStateException(e)
                } catch (e: InvocationTargetException) {
                    throw IllegalStateException(e)
                }
            }


//        return super.readValue(entity)
        }
    }
}

