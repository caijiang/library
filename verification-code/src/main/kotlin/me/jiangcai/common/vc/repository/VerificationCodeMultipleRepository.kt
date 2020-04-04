package me.jiangcai.common.vc.repository

import me.jiangcai.common.vc.entity.VerificationCodeMultiple
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

/**
 * @author CJ
 */
interface VerificationCodeMultipleRepository :
    JpaRepository<VerificationCodeMultiple, Long?>,
    JpaSpecificationExecutor<VerificationCodeMultiple?> {
    fun findByMobileAndType(mobile: String?, type: Int): List<VerificationCodeMultiple>
}
