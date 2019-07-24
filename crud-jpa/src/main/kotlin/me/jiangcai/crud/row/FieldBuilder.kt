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
import javax.persistence.criteria.*
import javax.persistence.metamodel.SingularAttribute

typealias ToSelect<X, T> = (From<*, T>, CriteriaBuilder, CriteriaQuery<*>) -> Expression<out X>
typealias ToFormat = (Any?, MediaType?, exportMe: Function<MutableList<Any?>, *>?) -> Any?

/**
 * 新版本构建器。
 * @param T 当前build类型
 * @param S 原始Entity类型
 * @author CJ
 */
class FieldBuilder<T, S>(
    private val type: Class<S>,
    private val conversionService: ConversionService,
    val toReadFrom: (From<*, S>) -> From<*, T>
) {
    fun <Y> forBuilder(from: (From<*, T>) -> From<*, Y>): FieldBuilder<Y, S> {
        return FieldBuilder(
            type, conversionService
        ) { from(toReadFrom(it)) }
    }

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
    ): TypeFieldDefinition<X, S> {
        return MyField(
            name = name,
            resultType = X::class.java,
            order = order,
            selector = { root, _, _ -> toReadFrom(root).get(field) },
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
    ): TypeFieldDefinition<X, S> {
        return MyField(
            name = name,
            resultType = X::class.java,
            order = order,
            selector = { from, cb, cq ->
                selector(toReadFrom(from), cb, cq)
            },
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
    ): TypeFieldDefinition<X, S> {
        return MyField(
            name = name,
            resultType = attribute.javaType,
            order = order,
            selector = { root, _, _ -> toReadFrom(root).get(attribute) },
            format = format
        )
    }

    inner class MyField<X>(
        private val name: String,
        private val resultType: Class<X>,
        private val selector: ToSelect<X, S>,
        private val order: Boolean,
        private val format: ToFormat
    ) : TypeFieldDefinition<X, S> {
        override fun getResultType(): Class<X> = resultType

        override fun select(cb: CriteriaBuilder, query: CriteriaQuery<*>, root: Root<S>): Expression<out X> {
            return selector(root, cb, query)
        }

        override fun export(origin: Any?, mediaType: MediaType?, exportMe: Function<MutableList<Any?>, *>?): Any? {
            return format(origin, mediaType, exportMe)
        }

        override fun order(
            query: CriteriaQuery<*>,
            criteriaBuilder: CriteriaBuilder,
            root: Root<S>
        ): Expression<*>? {
            if (!order)
                return null
            return select(criteriaBuilder, query, root)
        }

        override fun name(): String = name

        override fun readValue(entity: S): Any {
//        if (entityFunction != null)
//            return entityFunction.apply(entity)
            return try {
                val fake = select(FakeCriteriaBuilder(), FakeCriteriaQuery(), FakeRoot<S>()) as AbstractFake
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

