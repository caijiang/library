package me.jiangcai.crud.controller

import me.jiangcai.common.ext.annotations.AllOpenClass
import me.jiangcai.crud.CrudFriendly
import me.jiangcai.crud.event.EntityAddEvent
import me.jiangcai.crud.event.EntityRemoveEvent
import me.jiangcai.crud.event.EntityUpdateEvent
import me.jiangcai.crud.exception.CrudNotFoundException
import me.jiangcai.crud.modify.PropertyChanger
import me.jiangcai.crud.row.FieldBuilder
import me.jiangcai.crud.row.FieldDefinition
import me.jiangcai.crud.row.RowDefinition
import org.springframework.beans.BeanUtils
import org.springframework.context.ApplicationEventPublisher
import org.springframework.core.convert.ConversionService
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.domain.Specifications
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.WebRequest
import java.io.Serializable
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.ParameterizedType
import java.net.URI
import java.net.URISyntaxException
import java.util.*
import javax.annotation.Resource
import javax.persistence.EntityManager
import javax.persistence.NoResultException
import javax.persistence.PersistenceContext
import javax.persistence.criteria.*

/**
 * [AbstractCrudController]的代替者
 * ## 安全篇
 * 1. 最高级也是定制程度可以最高的是复写开放出来的 pre-post 方法
 * 2. 略微轻微程度的定制方法是 命名操作并且设计可复写的权限表
 *
 * ## 过滤器篇
 * 基本实现参考[me.jiangcai.crud.row.RowDramatizer.queryFilters]
 *
 * @author CJ
 */
@AllOpenClass
abstract class CrudController<T : CrudFriendly<ID>, ID : Serializable, X : T>(
    /**
     * 权利表
     */
    protected val rightTable: RightTable = RightTable()
) {
    // 可开放接口

    // 一些可覆盖的动作。

    /**
     * 执行删除动作，可自定义
     * @param entity 要被删除的实体
     */
    protected fun customDelete(entity: T) {
        entityManager.remove(entity)
    }

    /**
     * 默认没有排序，可通过自定义修改默认排序
     * @return 默认的排序方式
     */
    protected fun listOrder(cb: CriteriaBuilder, root: Root<T>): List<Order>? {
        return null
    }

    // 可定制视图渲染

    /**
     * @param principal 操作者身份
     * @param builder 用于构建字段的工具对象
     * @param locale 来访者当时的语言环境
     * @return 字段定义表
     */
    protected abstract fun listFields(
        principal: Any?,
        locale: Locale,
        builder: FieldBuilder<T>
    ): List<FieldDefinition<T>>

    /**
     * @param origin entity对象，切勿改变原始entity对象
     * @param principal 操作者身份
     * @param allPathVariables 当时的可用[PathVariable]
     * @param locale 来访者当时的语言环境
     * @return 描述这个对象
     */
    protected fun describeEntity(
        principal: Any?,
        allPathVariables: Map<String, String>,
        locale: Locale,
        origin: T
    ): Any {
        return origin
    }

    // 特定的行为模式

    /**
     * 自定义修改的方法
     * 如果采用了自定义的修改，相关事件会继续发布，但是[postUpdate]则不会因此而得到运行。
     *
     * @param principal 操作者身份
     * @param allPathVariables 当时的可用[PathVariable]
     * @param entity 实体
     * @param name   字段名称
     * @param data   原始数据
     * @return 是否支持自定义修改,如果支持的话，默认操作则不会继续进行
     */
    protected fun customUpdateSupport(
        principal: Any?,
        allPathVariables: Map<String, String>,
        entity: T,
        name: String,
        data: Any?
    ): Boolean = false

    /**
     * @param principal 操作者身份
     * @param allPathVariables 当时的可用[PathVariable]
     * @param request   实际请求
     * @return 查询规格
     * @see RowDefinition.specification
     */
    protected fun listSpecification(
        principal: Any?,
        allPathVariables: Map<String, String>,
        request: WebRequest
    ): Specification<T>? = null

    /**
     * 准备持久化
     *
     * @param principal 操作者身份
     * @param allPathVariables 当时的可用[PathVariable]
     * @param data    准备持久化的数据
     * @param request 其他提交的数据
     * @return 最终要提交的数据
     */
    protected fun prepareCreate(
        principal: Any?,
        allPathVariables: Map<String, String>,
        data: X,
        request: WebRequest
    ): T {
        return data
    }

    /**
     * 删除的钩子，**并非事务被提交之后**
     *
     * @param principal 操作者身份
     * @param allPathVariables 当时的可用[PathVariable]
     * @param entity 实体
     */
    protected fun prepareDelete(
        principal: Any?,
        allPathVariables: Map<String, String>,
        entity: T
    ) {

    }


    /**
     * 任何操作(除了新增)，都必须确保符合这个谓语。
     *
     * @param principal 操作者身份
     * @param allPathVariables 当时的可用[PathVariable]
     *
     * @return 可读性谓语, null 表示都可以
     */
    protected fun readablePredicate(
        principal: Any?,
        allPathVariables: Map<String, String>,
        cb: CriteriaBuilder,
        cq: CriteriaQuery<*>,
        root: Root<T>
    ): Predicate? = null

    // 事件钩子

    /**
     * 在完成数据更新后的调用钩子，**并非事务被提交之后**
     * @param entity 刚更新号的实体
     */
    protected fun postUpdate(entity: T) {
    }

    /**
     * 在完成持久化之后的调用钩子，**并非事务被提交之后**
     *
     * @param entity 完成持久化的实体
     */
    protected fun postCreate(entity: T) {
    }

    // 可开放接口

    @PersistenceContext
    private lateinit var entityManager: EntityManager
    @Resource
    private lateinit var applicationEventPublisher: ApplicationEventPublisher
    @Resource
    private lateinit var changerSet: List<PropertyChanger>
    @Resource
    private lateinit var conversionService: ConversionService

    @GetMapping(value = ["/{id}"])
    @Transactional(readOnly = true)
    @ResponseBody
    fun getOne(
        @AuthenticationPrincipal principal: Any?
        , locale: Locale
        , @PathVariable id: ID
        , @PathVariable allPathVariables: Map<String, String>
    ): Any {
        val right = if (rightTable.readRights != null) rightTable.readRights?.second
        else rightTable.read

        right?.check(SecurityContextHolder.getContext().authentication, principal, id, allPathVariables, null)
        val type = currentClass()

        val entity = entityManager.find<T>(type, id) ?: throw CrudNotFoundException()

        val filteredEntity = readReadableEntity(principal, allPathVariables, entity)

        return describeEntity(principal, allPathVariables, locale, filteredEntity)
    }

    private fun readReadableEntity(
        principal: Any?,
        allPathVariables: Map<String, String>,
        entity: T
    ): T {
        val type = currentClass()
        val cb = entityManager.criteriaBuilder
        val cq = cb.createQuery(type)
        val root = cq.from(type)

        val predicate = readablePredicate(principal, allPathVariables, cb, cq, root)
        return if (predicate == null) entity
        else {
            try {
                entityManager
                    .createQuery(
                        cq.select(root)
                            .where(cb.equal(root, entity), predicate)
                    )
                    .singleResult
            } catch (e: NoResultException) {
                throw CrudNotFoundException()
            }
        }
    }

    //增加一个数据
    @PostMapping
    @Transactional
    @Throws(URISyntaxException::class)
    fun addOne(
        @AuthenticationPrincipal principal: Any?
        , @RequestBody postData: X, @PathVariable allPathVariables: Map<String, String>
        , request: WebRequest
    ): ResponseEntity<*> {
        val result = prepareCreate(principal, allPathVariables, postData, request)
        rightTable.create?.check(
            SecurityContextHolder.getContext().authentication,
            principal,
            null,
            allPathVariables,
            result
        )
        entityManager.persist(result)
        entityManager.flush()
        postCreate(result)
        val id = result.id
        applicationEventPublisher.publishEvent(EntityAddEvent<T>(result))
        return ResponseEntity
            .created(URI(homeUri() + "/" + id))
            .build<Any>()
    }

    @PutMapping("/{id}/{name}")
    @Transactional
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun modifyOne(
        @AuthenticationPrincipal principal: Any?, @PathVariable id: ID, @PathVariable name: String
        , @PathVariable allPathVariables: Map<String, String>
        , @RequestBody(
            required = false
        ) data: Any?
    ) {
        val e = entityManager.find(currentClass(), id) ?: throw CrudNotFoundException()
        val entity = readReadableEntity(principal, allPathVariables, e)
        (if (rightTable.updateProperty.containsKey(name)) rightTable.updateProperty[name] else rightTable.update)
            ?.check(SecurityContextHolder.getContext().authentication, principal, id, allPathVariables, entity)
// 允许自定义修改
        if (customUpdateSupport(principal, allPathVariables, entity, name, data)) {
            applicationEventPublisher.publishEvent(EntityUpdateEvent(entity))
            return
        }
        // 允许注册更多修改器
        // 获取数据类型
        val pd = BeanUtils.getPropertyDescriptor(entity.javaClass, name) ?: throw CrudNotFoundException()

        val newValue = changerSet.stream()
            .filter { propertyChanger -> propertyChanger.support(pd.propertyType) }
            .findFirst()
            .orElseThrow { IllegalStateException("not supported field:$name for modify.") }
            .change(pd.propertyType, data)

        try {
            pd.writeMethod.invoke(entity, newValue)
        } catch (e: IllegalAccessException) {
            throw IllegalStateException("not supported field:$name for modify.", e)
        } catch (e: InvocationTargetException) {
            throw IllegalStateException("not supported field:$name for modify.", e)
        }
        postUpdate(entity)

        // 发布事件
        applicationEventPublisher.publishEvent(EntityUpdateEvent(entity))
    }


    @GetMapping
    fun list(
        @AuthenticationPrincipal principal: Any?, @PathVariable allPathVariables: Map<String, String>
        , locale: Locale
        , request: WebRequest
    ): RowDefinition<T> {
        val right = if (rightTable.readRights != null) rightTable.readRights?.first
        else rightTable.read

        right?.check(
            SecurityContextHolder.getContext().authentication,
            principal,
            null,
            allPathVariables,
            null
        )
        val builder = FieldBuilder(currentClass(), conversionService)
        return object : RowDefinition<T> {
            override fun entityClass(): Class<T> {
                return currentClass()
            }

            override fun fields(): List<FieldDefinition<T>> {
                return listFields(principal, locale, builder)
            }

            override fun specification(): Specification<T>? {
                val spec = listSpecification(principal, allPathVariables, request)
                val read = Specification<T> { root, query, cb ->
                    readablePredicate(principal, allPathVariables, cb, query, root) ?: cb.conjunction()
                }
                return spec?.let {
                    Specifications.where(it).and(read)
                } ?: read
            }

            override fun defaultOrder(criteriaBuilder: CriteriaBuilder, root: Root<T>): List<Order>? {
                return listOrder(criteriaBuilder, root)
            }

//            override fun dataGroup(cb: CriteriaBuilder, query: CriteriaQuery<T>, root: Root<T>): CriteriaQuery<T> {
//                return listGroup(cb, query, root)
//            }
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    fun deleteOne(
        @AuthenticationPrincipal principal: Any?, @PathVariable id: ID
        , @PathVariable allPathVariables: Map<String, String>
    ) {
        val e = entityManager.find(currentClass(), id) ?: throw CrudNotFoundException()
        val entity = readReadableEntity(principal, allPathVariables, e)
        rightTable.delete?.check(
            SecurityContextHolder.getContext().authentication,
            principal,
            id,
            allPathVariables,
            entity
        )
        prepareDelete(principal, allPathVariables, entity)
        customDelete(entity)
        applicationEventPublisher.publishEvent(EntityRemoveEvent(entity))
    }


    @Suppress("UNCHECKED_CAST")
    private fun currentClass(): Class<T> {
        val type = javaClass.genericSuperclass as ParameterizedType
        return type.actualTypeArguments[0] as Class<T>
    }

    private fun homeUri(): String {
        return javaClass.getAnnotation(RequestMapping::class.java).value[0]
    }
}