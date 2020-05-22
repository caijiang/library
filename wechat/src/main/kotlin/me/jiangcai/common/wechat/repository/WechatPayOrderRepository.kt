package me.jiangcai.common.wechat.repository

import me.jiangcai.common.wechat.entity.WechatPayOrder
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.security.access.prepost.PreAuthorize

/**
 * @author CJ
 */
interface WechatPayOrderRepository : JpaRepository<WechatPayOrder, String> {
    fun findByPrepayId(prepayId: String): WechatPayOrder?

    @PreAuthorize("hasAnyRole('ROOT','EDIT_WECHAT_PAY_ORDER','VIEW_WECHAT_PAY_ORDER')")
    override fun findAll(): MutableList<WechatPayOrder>

    @PreAuthorize("hasAnyRole('ROOT','EDIT_WECHAT_PAY_ORDER','VIEW_WECHAT_PAY_ORDER')")
    override fun findAll(sort: Sort): MutableList<WechatPayOrder>

    @PreAuthorize("hasAnyRole('ROOT','EDIT_WECHAT_PAY_ORDER','VIEW_WECHAT_PAY_ORDER')")
    override fun findAll(pageable: Pageable): Page<WechatPayOrder>

    @PreAuthorize("hasAnyRole('ROOT','EDIT_WECHAT_PAY_ORDER')")
    override fun deleteById(id: String)

    @PreAuthorize("hasAnyRole('ROOT','EDIT_WECHAT_PAY_ORDER')")
    override fun <S : WechatPayOrder?> save(entity: S): S
}