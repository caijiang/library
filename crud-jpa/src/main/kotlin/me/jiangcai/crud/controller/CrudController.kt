package me.jiangcai.crud.controller

import me.jiangcai.common.ext.annotations.AllOpenClass
import me.jiangcai.crud.CrudFriendly
import me.jiangcai.crud.exception.CrudNotFoundException
import me.jiangcai.crud.modify.PropertyChanger
import org.springframework.context.ApplicationEventPublisher
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.ResponseBody
import java.io.Serializable
import java.lang.reflect.ParameterizedType
import javax.annotation.Resource
import javax.persistence.EntityManager
import javax.persistence.NoResultException
import javax.persistence.PersistenceContext
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

/**
 * [AbstractCrudController]的代替者
 * ## 安全篇
 * 1. 最高级也是定制程度可以最高的是复写开放出来的 pre-post 方法
 * 2. 略微轻微程度的定制方法是 命名操作并且设计可复写的权限表
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

    /**
     * @param origin entity对象，切勿改变原始entity对象
     * @param principal 操作者身份
     * @param allPathVariables 当时的可用[PathVariable]
     * @return 描述这个对象
     */
    protected fun describeEntity(
        principal: Any?,
        allPathVariables: Map<String, String>,
        origin: T
    ): Any {
        return origin
    }

    /**
     * 无论是用于获取单个资源或者是资源列表，都必须确保符合这个谓语。
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

    // 可开放接口

    @PersistenceContext
    private lateinit var entityManager: EntityManager
    @Resource
    private lateinit var applicationEventPublisher: ApplicationEventPublisher
    @Resource
    private lateinit var changerSet: List<PropertyChanger>


    @GetMapping(value = ["/{id}"])
    @Transactional(readOnly = true)
    @ResponseBody
    fun getOne(
        @AuthenticationPrincipal principal: Any?, @PathVariable id: ID
        , @PathVariable allPathVariables: Map<String, String>
    ): Any {
        rightTable.read?.check(SecurityContextHolder.getContext().authentication, principal, id, allPathVariables, null)
        val type = currentClass()

        val entity = entityManager.find<T>(type, id) ?: throw CrudNotFoundException()

        val cb = entityManager.criteriaBuilder
        val cq = cb.createQuery(type)
        val root = cq.from(type)

        val predicate = readablePredicate(principal, allPathVariables, cb, cq, root)
        val filteredEntity = if (predicate == null) entity
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

        return describeEntity(principal, allPathVariables, filteredEntity)
    }


    @Suppress("UNCHECKED_CAST")
    private fun currentClass(): Class<T> {
        val type = javaClass.genericSuperclass as ParameterizedType
        return type.actualTypeArguments[0] as Class<T>
    }
}