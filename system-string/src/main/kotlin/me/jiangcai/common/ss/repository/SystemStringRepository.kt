package me.jiangcai.common.ss.repository

import me.jiangcai.common.ss.entity.SystemString
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

/**
 * @author CJ
 */
interface SystemStringRepository : JpaRepository<SystemString, String>, JpaSpecificationExecutor<SystemString>