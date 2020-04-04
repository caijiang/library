package me.jiangcai.common.vc.repository

import me.jiangcai.common.vc.entity.VerificationCode
import me.jiangcai.common.vc.entity.VerificationCodePK
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

/**
 * @author CJ
 */
interface VerificationCodeRepository :
    JpaRepository<VerificationCode, VerificationCodePK>,
    JpaSpecificationExecutor<VerificationCode>
