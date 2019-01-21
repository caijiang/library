package me.jiangcai.common.jdbc

import org.springframework.transaction.annotation.Transactional
import java.sql.SQLException

/**
 * @author CJ
 */
interface JdbcService {


    /**
     * 执行一些jdbc操作.
     *
     *
     * tip:操作数据表比如create,drop,alert将直接提交事务.
     *
     *
     * @param connectionConsumer 操作者
     * @throws SQLException
     */
    @Transactional
    @Throws(SQLException::class)
    fun runJdbcWork(connectionConsumer: (ConnectionProvider) -> Unit)

    /**
     * 修改一个现有表,并且增加一个字段
     *
     *
     * tip:操作数据表比如create,drop,alert将直接提交事务.
     *
     *
     * @param entityClass  目标JPA类
     * @param field        字段名称
     * @param defaultValue 默认值,可以为null
     * @throws SQLException         执行时发生SQL问题
     * @throws NoSuchFieldException 没有找到相关的字段
     * @since 1.4
     */
    @Transactional
    @Throws(SQLException::class, NoSuchFieldException::class)
    fun tableAlterAddColumn(entityClass: Class<*>, field: String, defaultValue: String?)

    /**
     * 修改一个现有表,并且修改一个字段
     *
     *
     * tip:操作数据表比如create,drop,alert将直接提交事务.
     *
     *
     * @param entityClass  目标JPA类
     * @param field        字段名称
     * @param defaultValue 默认值,可以为null
     * @throws SQLException         执行时发生SQL问题
     * @throws NoSuchFieldException 没有找到相关的字段
     * @since 1.4
     */
    @Transactional
    @Throws(SQLException::class, NoSuchFieldException::class)
    fun tableAlterModifyColumn(entityClass: Class<*>, field: String, defaultValue: String?)

    /**
     * 执行一些jdbc操作.
     *
     *
     * tip:操作数据表比如create,drop,alert将直接提交事务.
     *
     * 跟[.runJdbcWork]不同的是,该方法并没有声明使用事务,也就是它将依赖当前线程已开启的事务
     *
     * @param connectionConsumer 操作者
     * @throws SQLException
     */
    @Throws(SQLException::class)
    fun runStandaloneJdbcWork(connectionConsumer: (ConnectionProvider) -> Unit)

}