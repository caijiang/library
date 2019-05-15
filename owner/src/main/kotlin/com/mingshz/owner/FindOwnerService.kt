package com.mingshz.owner

import com.mingshz.owner.entity.OwnerEntity
import org.springframework.transaction.annotation.Transactional
import javax.servlet.http.HttpServletRequest

/**
 * @author CJ
 */
interface FindOwnerService {

    @Transactional(readOnly = true)
    fun findOwner(request: HttpServletRequest): OwnerEntity
}