package me.jiangcai.crud.controller;

import me.jiangcai.crud.CrudFriendly;
import me.jiangcai.crud.event.EntityAddEvent;
import me.jiangcai.crud.event.EntityRemoveEvent;
import me.jiangcai.crud.event.EntityUpdateEvent;
import me.jiangcai.crud.exception.CrudNotFoundException;
import me.jiangcai.crud.modify.PropertyChanger;
import me.jiangcai.crud.row.FieldDefinition;
import me.jiangcai.crud.row.RowCustom;
import me.jiangcai.crud.row.RowDefinition;
import me.jiangcai.crud.row.supplier.SingleRowDramatizer;
import me.jiangcai.crud.utils.JpaUtils;
import me.jiangcai.crud.utils.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/**
 * ID可能并不都是可简单序列化的，所以MVC本身需要支撑它们的序列化，这个由客户端项目实现。
 * <b>警告：处理post时处理的逻辑需要可以重复的读取Request Body所以需要{@link me.jiangcai.crud.filter.MultiReadSupportFilter}的支持 </b>
 * 渲染整个entity？会不会出事呢……
 * TODO 安全控制
 * TODO get One 定制化方案
 * TODO PUT /id
 * TODO PUT /id/name 请求体=具体的属性内容
 * TODO PATCH /id 请求体为部分资源内容
 *
 * @author CJ
 */
public abstract class AbstractCrudController<T extends CrudFriendly<ID>, ID extends Serializable, X extends T> {

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private List<PropertyChanger> changerSet;

    @GetMapping(value = "/{id}")
    @Transactional(readOnly = true)
    @ResponseBody
    public Object getOne(@PathVariable ID id) {
        Class<T> type = currentClass();
        T entity = entityManager.find(type, id);
        if (entity == null) {
            throw new CrudNotFoundException();
        }
        return describeEntity(entity);
    }

    @GetMapping(value = "/{id}/detail")
    @Transactional(readOnly = true)
    @RowCustom(distinct = true, dramatizer = SingleRowDramatizer.class)
    public RowDefinition<T> getDetail(@PathVariable ID id) {
        return new RowDefinition<T>() {
            @Override
            public Class<T> entityClass() {
                return currentClass();
            }

            @Override
            public List<FieldDefinition<T>> fields() {
                return listFields();
            }

            @Override
            public Specification<T> specification() {
                return (root, cq, cb) -> cb.equal(root.get(JpaUtils.idFieldNameForEntity(currentClass())), id);
            }
        };
    }

    /**
     * 自定义修改的方法
     *
     * @param entity 实体
     * @param name   字段名称
     * @param data   原始数据
     * @return 是否支持修改
     */
    @SuppressWarnings("WeakerAccess")
    protected boolean customModifySupport(T entity, String name, Object data) {
        return false;
    }

    // 修改一个数据
    @PutMapping("/{id}/{name}")
    @Transactional
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void modifyOne(@PathVariable ID id, @PathVariable String name, @RequestBody Object data) {
        T entity = entityManager.find(currentClass(), id);
        if (entity == null)
            throw new CrudNotFoundException();
        // 允许自定义修改
        if (customModifySupport(entity, name, data)) {
            applicationEventPublisher.publishEvent(new EntityUpdateEvent<>(entity));
            return;
        }
        // 允许注册更多修改器
        // 获取数据类型
        PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(entity.getClass(), name);
        if (pd == null)
            throw new CrudNotFoundException();

        Object newValue = changerSet.stream()
                .filter(propertyChanger -> propertyChanger.support(pd.getPropertyType()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("not supported."))
                .change(pd.getPropertyType(), data);

        try {
            pd.getWriteMethod().invoke(entity, newValue);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("not supported.", e);
        }
        // 发布事件
        applicationEventPublisher.publishEvent(new EntityUpdateEvent<>(entity));
    }


    //增加一个数据
    @PostMapping
    @Transactional
    public ResponseEntity addOne(@RequestBody X postData, WebRequest request) throws URISyntaxException {
        T result = preparePersist(postData, request);
        entityManager.persist(result);
        entityManager.flush();
        postPersist(result);
        ID id = result.getId();
        applicationEventPublisher.publishEvent(new EntityAddEvent<>(result));
        // TODO 串化讲道理应该是通过MVC配置获取，这里先简单点来
        return ResponseEntity
                .created(new URI(homeUri() + "/" + id))
                .build();
    }

    /**
     * 在完成持久化之后的调用钩子，<b>并非事务被提交之后</b>
     *
     * @param entity 完成持久化的实体
     */
    @SuppressWarnings("WeakerAccess")
    protected void postPersist(T entity) {

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Transactional
    public void deleteOne(@PathVariable ID id) {
        T entity = entityManager.find(currentClass(), id);
        if (entity == null)
            throw new CrudNotFoundException();
        prepareRemove(entity);
        doRemove(entity);
        applicationEventPublisher.publishEvent(new EntityRemoveEvent<>(entity));
    }

    /**
     * 删除的钩子
     *
     * @param entity 实体
     */
    @SuppressWarnings("WeakerAccess")
    protected void prepareRemove(T entity) {

    }

    /**
     * 执行删除动作，可自定义
     *
     * @param entity
     */
    @SuppressWarnings("WeakerAccess")
    protected void doRemove(T entity) {
        entityManager.remove(entity);
    }

    // 获取数据
    @GetMapping
    public RowDefinition<T> list(HttpServletRequest request) {
        Map<String, Object> queryData = MapUtils.changeIt(request.getParameterMap());
        return new RowDefinition<T>() {
            @Override
            public Class<T> entityClass() {
                return currentClass();
            }

            @Override
            public List<FieldDefinition<T>> fields() {
                return listFields();
            }

            @Override
            public Specification<T> specification() {
                return listSpecification(request, queryData);
            }

            @Override
            public List<Order> defaultOrder(CriteriaBuilder criteriaBuilder, Root<T> root) {
                return listOrder(criteriaBuilder, root);
            }

            @Override
            public CriteriaQuery<T> dataGroup(CriteriaBuilder cb, CriteriaQuery<T> query, Root<T> root) {
                return listGroup(cb, query, root);
            }
        };
    }


    /**
     * @return 展示用的
     */
    protected abstract List<FieldDefinition<T>> listFields();

    /**
     * 优先级上比{@link #listSpecification(Map)}更高
     *
     * @param request   实际请求
     * @param queryData 查询时提交的数据
     * @return 查询规格
     * @see RowDefinition#specification()
     */
    @SuppressWarnings("WeakerAccess")
    protected Specification<T> listSpecification(HttpServletRequest request, Map<String, Object> queryData) {
        return listSpecification(queryData);
    }

    /**
     * @param queryData 查询时提交的数据
     * @return 查询规格
     * @see RowDefinition#specification()
     */
    protected abstract Specification<T> listSpecification(Map<String, Object> queryData);

    /**
     * 排序字段，默认没有排序
     *
     * @param criteriaBuilder
     * @param root
     * @return
     */
    protected List<Order> listOrder(CriteriaBuilder criteriaBuilder, Root<T> root) {
        return null;
    }

    /**
     * 是否分组
     *
     * @see RowDefinition#dataGroup(CriteriaBuilder, CriteriaQuery, Root)
     */
    protected CriteriaQuery<T> listGroup(CriteriaBuilder cb, CriteriaQuery<T> query, Root<T> root) {
        return query;
    }

    /**
     * 准备持久化
     *
     * @param data    准备持久化的数据
     * @param request 其他提交的数据
     * @return 最终要提交的数据
     */
    protected T preparePersist(X data, WebRequest request) {
        return data;
    }

    private String homeUri() {
        return getClass().getAnnotation(RequestMapping.class).value()[0];
    }

    /**
     * @param origin entity对象，切勿改变原始entity对象
     * @return 描述这个对象
     */
    protected Object describeEntity(T origin) {
        return origin;
    }

    @SuppressWarnings("unchecked")
    private Class<T> currentClass() {
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<T>) type.getActualTypeArguments()[0];
    }
}
