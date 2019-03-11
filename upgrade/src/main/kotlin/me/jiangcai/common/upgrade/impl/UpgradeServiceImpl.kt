package me.jiangcai.common.upgrade.impl

import me.jiangcai.common.upgrade.UpgradableBean
import me.jiangcai.common.upgrade.UpgradeService
import me.jiangcai.common.upgrade.VersionInfoService
import me.jiangcai.common.upgrade.VersionUpgrade
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service
import java.lang.reflect.ParameterizedType

/**
 * @author CJ
 */
@Suppress("UNCHECKED_CAST")
@Service
class UpgradeServiceImpl(
    @Autowired
    private val applicationContext: ApplicationContext,
    @Autowired
    private val versionInfoService: VersionInfoService
) : UpgradeService {
    private val log = LogFactory.getLog(UpgradeServiceImpl::class.java)

    override fun <T : Enum<*>> systemUpgrade(upgrade: VersionUpgrade<T>) {
        val type = upgrade.javaClass.genericInterfaces[0] as ParameterizedType

        val versionType = type.actualTypeArguments[0] as Class<T>

        core(versionType, upgrade)

    }

    private fun <T : Enum<*>> core(versionType: Class<T>, upgrade: VersionUpgrade<T>?) {
        val currentVersion = versionType.enumConstants[versionType.enumConstants.size - 1]

        log.debug("Subsystem should upgrade to $currentVersion")

        val databaseVersion = versionInfoService.currentVersion(versionType)

        try {
            if (databaseVersion == null) {
                versionInfoService.updateVersion(currentVersion)
            } else {
                //比较下等级
                if (databaseVersion !== currentVersion) {
                    upgrade(versionType, databaseVersion, currentVersion, upgrade)
                }
            }
        } catch (ex: Exception) {
            throw InternalError("Failed Upgrade Database", ex)
        }
    }

    @Throws(Exception::class)
    private fun <T : Enum<*>> upgrade(clazz: Class<T>, origin: T?, target: T, upgrade: VersionUpgrade<T>?) {
        log.debug("Subsystem prepare to upgrade to $target")
        var started = false
        for (step in clazz.enumConstants) {
            if (origin == null || origin.ordinal < step.ordinal) {
                started = true
            }

            if (started) {
                log.debug("Subsystem upgrade step: to $target")
                if (upgrade == null) {
                    applicationContext.getBeansOfType(UpgradableBean::class.java)
                        .values
                        .filter { it.supportVersionEnum(clazz) }
                        .forEach {
                            try {
                                it.upgradeTo(step)
                            } catch (e: Throwable) {
                                log.warn("exception on upgrade:", e)
                            }
                        }
                } else
                    upgrade.upgradeToVersion(step)
                log.debug("Subsystem upgrade step done")
            }

            if (step === target)
                break
        }

        versionInfoService.updateVersion(target)
    }

    override fun <T : Enum<*>> systemUpgrade(versionEnumType: Class<T>) {
        core(versionEnumType, null)
    }

}