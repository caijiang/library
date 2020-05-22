package me.jiangcai.common.wechat.repository

import me.jiangcai.common.wechat.entity.WechatRefundOrder
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.security.access.prepost.PreAuthorize

/**
 * @author CJ
 */
interface WechatRefundOrderRepository : JpaRepository<WechatRefundOrder, String> {
    @PreAuthorize("hasAnyRole('ROOT','EDIT_WECHAT_REFUND_ORDER','VIEW_WECHAT_REFUND_ORDER')")
    override fun findAll(): MutableList<WechatRefundOrder>

    @PreAuthorize("hasAnyRole('ROOT','EDIT_WECHAT_REFUND_ORDER','VIEW_WECHAT_REFUND_ORDER')")
    override fun findAll(sort: Sort): MutableList<WechatRefundOrder>

    @PreAuthorize("hasAnyRole('ROOT','EDIT_WECHAT_REFUND_ORDER','VIEW_WECHAT_REFUND_ORDER')")
    override fun findAll(pageable: Pageable): Page<WechatRefundOrder>

    @PreAuthorize("hasAnyRole('ROOT','EDIT_WECHAT_REFUND_ORDER')")
    override fun deleteById(id: String)

    @PreAuthorize("hasAnyRole('ROOT','EDIT_WECHAT_REFUND_ORDER')")
    override fun <S : WechatRefundOrder?> save(entity: S): S
}