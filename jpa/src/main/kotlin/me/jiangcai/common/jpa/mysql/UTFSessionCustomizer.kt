package me.jiangcai.common.jpa.mysql

import org.eclipse.persistence.config.SessionCustomizer
import org.eclipse.persistence.sessions.Session
import org.eclipse.persistence.sessions.SessionEvent
import org.eclipse.persistence.sessions.SessionEventAdapter
import org.eclipse.persistence.sessions.UnitOfWork

/**
 * eclipse-link 的 mysql utf8mb4 支持
 * @author CJ
 */
@Suppress("unused")
class UTFSessionCustomizer : SessionCustomizer {

    private val log = org.apache.commons.logging.LogFactory.getLog(UTFSessionCustomizer::class.java)

    override fun customize(session: Session) {
        session.eventManager.addListener(object : SessionEventAdapter() {
            override fun preBeginTransaction(event: SessionEvent?) {
                try {
                    var work: UnitOfWork? = event!!.session.acquireUnitOfWork()
                    try {
                        work!!.executeNonSelectingSQL("set names utf8mb4")
                        work.commit()
                        work = null
                    } finally {
                        work?.release()
                    }
                } catch (ex: Exception) {
                    log.error("UTF8MB4", ex)
                }

            }
        })
    }
}