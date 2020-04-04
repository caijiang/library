package me.jiangcai.common.vc

import me.jiangcai.common.ss.SystemStringConfig
import me.jiangcai.common.vc.repository.VerificationCodeMultipleRepository
import me.jiangcai.common.vc.repository.VerificationCodeRepository
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * @author CJ
 */
@Configuration
@Import(SystemStringConfig::class)
@EnableJpaRepositories("me.jiangcai.common.vc.repository")
@ComponentScan("me.jiangcai.common.vc.service")
class VerificationCodeSpringConfig(
    @Autowired
    private val verificationCodeMultipleRepository: VerificationCodeMultipleRepository,
    @Autowired
    private val verificationCodeRepository: VerificationCodeRepository
) {
    private val log = LogFactory.getLog(VerificationCodeSpringConfig::class.java)

    /**
     * 把发送时间超过一天的记录清楚掉
     */
    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    @Transactional
    fun autoDelete() {
        log.debug("auto delete vc.")
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -1)

        verificationCodeRepository.findAll { root, query, cb ->
            cb.lessThan(
                root.get("sendTime"),
                calendar
            )
        }.forEach {
            verificationCodeRepository.delete(it)
        }

        verificationCodeMultipleRepository.findAll { root, query, cb ->
            cb.lessThan(
                root.get("sendTime"),
                calendar
            )
        }.forEach {
            verificationCodeMultipleRepository.delete(it)
        }
    }
}