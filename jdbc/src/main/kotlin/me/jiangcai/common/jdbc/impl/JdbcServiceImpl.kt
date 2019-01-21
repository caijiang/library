package me.jiangcai.common.jdbc.impl

import com.google.common.primitives.Primitives
import me.jiangcai.common.jdbc.CloseableConnectionProvider
import me.jiangcai.common.jdbc.ConnectionProvider
import me.jiangcai.common.jdbc.JdbcService
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.lang.reflect.Field
import java.sql.Connection
import java.sql.SQLException
import java.util.*
import javax.persistence.Column
import javax.persistence.EntityManager
import javax.persistence.Table
import javax.sql.DataSource

/**
 * @author CJ
 */
@Service
class JdbcServiceImpl : JdbcService {

    @Autowired(required = false)
    private var entityManagerSet: Set<EntityManager>? = null
    @Autowired(required = false)
    private var dataSource: DataSource? = null

    private val log = LogFactory.getLog(JdbcServiceImpl::class.java)


    @Throws(SQLException::class, NoSuchFieldException::class)
    override fun tableAlterAddColumn(entityClass: Class<*>, field: String, defaultValue: String?) {
        tableAlertColumn(entityClass, field, defaultValue, true)
    }

    @Throws(SQLException::class, NoSuchFieldException::class)
    override fun tableAlterModifyColumn(entityClass: Class<*>, field: String, defaultValue: String?) {
        tableAlertColumn(entityClass, field, defaultValue, false)
    }

    @Throws(SQLException::class, NoSuchFieldException::class)
    private fun tableAlertColumn(entityClass: Class<*>, fieldName: String, defaultValue: String?, creation: Boolean) {
        val connectionProvider = getConnectionProvider(entityClass)
        val sql = StringBuilder("ALTER TABLE ")
        val table = entityClass.getAnnotation(Table::class.java)
        if (table != null && table.name.isNotEmpty())
            sql.append(table.name)
        else
            sql.append(entityClass.simpleName.toUpperCase(Locale.ENGLISH))

        if (creation)
            sql.append(" ADD")
        else {
            var alter = "ALTER"
            if (connectionProvider.profile().isMySQL) {
                alter = "MODIFY"
            }
            sql.append(' ')
            sql.append(alter).append(" COLUMN")
        }

        val field: Field = try {
            entityClass.getDeclaredField(fieldName)
        } catch (ex: NoSuchFieldException) {
            entityClass.getField(fieldName)
        }

        // 字段名未必是准确的 我们最好先找下这个表 找出这个最准确的字段名

        val column = field.getAnnotation(Column::class.java)
        if (column != null && column.name.isNotEmpty())
            sql.append(' ').append(column.name)
        else
            sql.append(' ').append(fieldName.toUpperCase(Locale.ENGLISH))

        //类型
        if (column != null && column.columnDefinition.isNotEmpty()) {
            sql.append(' ').append(column.columnDefinition)
        } else {
            var type = field.type
            var primitive = false
            if (type.isPrimitive) {
                primitive = true
                type = Primitives.wrap(type)
                // 如果是基本类型 那么先
            }
            val fieldTypeDefinition = connectionProvider.profile().getFieldTypeDefinition(type)
            sql.append(' ').append(fieldTypeDefinition.name)
            if (fieldTypeDefinition.isSizeAllowed && fieldTypeDefinition.isSizeRequired) {
                if (column != null && fieldTypeDefinition.name.equals("DECIMAL", ignoreCase = true)) {
                    sql.append('(')
                    sql.append(column.precision)
                    sql.append(',')
                    sql.append(column.scale)
                    sql.append(')')
                } else {
                    sql.append('(')
                    if (column != null)
                        sql.append(column.length)
                    else
                        sql.append(fieldTypeDefinition.defaultSize)
                    sql.append(')')
                }
            }

            var nullable: Boolean
            if (column != null) {
                nullable = column.nullable
                if (nullable) {
                    nullable = fieldTypeDefinition.shouldAllowNull()
                }
            } else {
                nullable = !primitive && fieldTypeDefinition.shouldAllowNull()
            }


            if (nullable)
                sql.append(" NULL")
            else {
                sql.append(" NOT NULL")
                if (defaultValue != null)
                    sql.append(" DEFAULT ").append(defaultValue)
                else
                    sql.append(" DEFAULT 0")
            }
        }
        log.debug("Prepare to $sql")

        val connection = connectionProvider.getConnection()
        try {
            connection.createStatement().use { it -> it.executeUpdate(sql.toString()) }
        } finally {
            if (connectionProvider is CloseableConnectionProvider)
                connectionProvider.close(connection)
        }

    }

    private fun getConnectionProvider(entityClass: Class<*>?): ConnectionProvider {
        val entityManager: EntityManager?
        if (entityClass != null) {
            var matchedEntityManager: EntityManager? = null
            if (entityManagerSet != null) {
                for (em in entityManagerSet!!) {
                    try {
                        @Suppress("UNCHECKED_CAST")
                        em.entityManagerFactory.metamodel.managedType(entityClass as Class<Any>?)
                        matchedEntityManager = em
                        break
                    } catch (ignored: IllegalArgumentException) {
                    }

                }
                entityManager = matchedEntityManager
            } else
                entityManager = null
        } else

            entityManager = entityManagerSet?.find { true }

        //TODO JpaDialect 是一个很好的获取连接的方式,但目前定位上存在困难
        if (entityManager != null) {
            val connection = entityManager.unwrap(Connection::class.java)
                ?: throw IllegalStateException("@Transactional did not work check DataSupportConfig for details.")
            return SimpleConnectionProvider(connection)
        }

        if (dataSource != null)
            return DataSourceConnectionProvider(dataSource!!)

        throw IllegalStateException("there is no connection provide.")
    }

    @Throws(SQLException::class)
    override fun runJdbcWork(connectionConsumer: (ConnectionProvider) -> Unit) {
        log.debug("Prepare to run jdbc")
        val provider = getConnectionProvider(null)
//        if (provider is CloseableConnectionProvider) {
//            connectionConsumer = connectionConsumer.andThen(provider as CloseableConnectionProvider)
//        }

        connectionConsumer(provider)
//        connectionConsumer.accept(provider)
        log.debug("End jdbc")
    }

    @Throws(SQLException::class)
    override fun runStandaloneJdbcWork(connectionConsumer: (ConnectionProvider) -> Unit) {
        runJdbcWork(connectionConsumer)
    }


}