package me.jiangcai.common.wechat

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException
import java.io.Serializable
import javax.persistence.EntityNotFoundException

fun <T, ID : Serializable> JpaRepository<T, ID>.findOptionalOne(id: ID): T? {
    return try {
        getOne(id)
    } catch (e: EntityNotFoundException) {
        null
    } catch (e: JpaObjectRetrievalFailureException) {
        null
    }
}
