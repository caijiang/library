package com.mingshz.login.wechat.service

import com.mingshz.login.entity.Login
import com.mingshz.login.wechat.WechatLoginService
import com.mingshz.login.wechat.controller.WechatController
import com.mingshz.login.wechat.entity.WechatLogin
import com.mingshz.login.wechat.entity.WechatLogin_
import me.jiangcai.wx.model.WeixinUserDetail
import me.jiangcai.wx.standard.entity.StandardWeixinUser
import me.jiangcai.wx.standard.entity.support.AppIdOpenID
import me.jiangcai.wx.standard.repository.StandardWeixinUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.servlet.http.HttpServletRequest

/**
 * @author CJ
 */
@Service
class WechatLoginServiceImpl(
    @Autowired
    private val standardWeixinUserRepository: StandardWeixinUserRepository
) : WechatLoginService {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    override fun findLoginById(id: AppIdOpenID): Login? {
        return entityManager.find(WechatLogin::class.java, id)?.login
    }

    override fun assignWechat(login: Login, request: HttpServletRequest) {
        val id = request.session.getAttribute(WechatController.SessionKeyForAppIdOpenID) as AppIdOpenID
        assignWechat(login, id)
    }

    override fun assignWechat(login: Login, wechatId: AppIdOpenID) {
        val wl = entityManager.find(WechatLogin::class.java, wechatId)

        if (wl != null) {
            wl.login = login
        } else {
            entityManager.persist(WechatLogin(findById(wechatId), login))
        }

    }

    override fun findById(id: AppIdOpenID): StandardWeixinUser {
        return standardWeixinUserRepository.getOne(id)
    }

    override fun requireSave(detail: WeixinUserDetail) {
        val saved = standardWeixinUserRepository.getOne(AppIdOpenID(detail.appId, detail.openId))

        when {
            saved == null -> {
                val w = StandardWeixinUser()
                WeixinUserDetail.Copy(w, detail)
                w.lastRefreshDetailTime = LocalDateTime.now()
                standardWeixinUserRepository.save(w)
            }
            saved.lastRefreshDetailTime == null -> saved.lastRefreshDetailTime = LocalDateTime.now()
            saved.lastRefreshDetailTime < LocalDateTime.now().minusDays(1) -> WeixinUserDetail.Copy(saved, detail)
        }
    }

    override fun releaseBind(id: AppIdOpenID) {
        entityManager.find(WechatLogin::class.java, id)?.let {
            entityManager.remove(it)
        }
    }

    override fun releaseBind(login: Login) {
        val cb = entityManager.criteriaBuilder
        val cq = cb.createCriteriaDelete(WechatLogin::class.java)
        val root = cq.from(WechatLogin::class.java)

        entityManager.createQuery(
            cq.where(
                cb.equal(
                    root.get(WechatLogin_.login), login
                )
            )
        )
            .executeUpdate()
    }

    override fun findAllWechat(login: Login): List<StandardWeixinUser> {
        val cb = entityManager.criteriaBuilder
        val cq = cb.createQuery(StandardWeixinUser::class.java)
        val root = cq.from(WechatLogin::class.java)

        return entityManager.createQuery(
            cq.select(root.get(WechatLogin_.wechat))
                .where(
                    cb.equal(root.get(WechatLogin_.login), login)
                )
        ).resultList
    }
}