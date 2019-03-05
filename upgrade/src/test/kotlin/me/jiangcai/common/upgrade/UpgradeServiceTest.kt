package me.jiangcai.common.upgrade

import org.apache.commons.logging.LogFactory
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

/**
 * @author CJ
 */
@ContextConfiguration(classes = [UpgradeServiceTest.Config::class])
@RunWith(SpringJUnit4ClassRunner::class)
class UpgradeServiceTest {

    @Import(UpgradeSpringConfig::class)
    class Config {
        private val log = LogFactory.getLog(Config::class.java)

        @Bean
        fun versionInfoService(): VersionInfoService {
            return object : VersionInfoService {

                private var current = FemaleVersion.Girl

                override fun <T : Enum<*>> currentVersion(type: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return current as T
                }

                override fun <T : Enum<*>> updateVersion(currentVersion: T) {
                    current = currentVersion as FemaleVersion
                    log.info("sub-upgrade to $currentVersion")
                }
            }
        }

        @Bean
        fun abc(): UpgradableBean {
            return object : UpgradableBean {
                override fun <T : Enum<*>> supportVersionEnum(versionEnumType: Class<T>): Boolean {
                    return versionEnumType == FemaleVersion::class.java
                }

                override fun <T : Enum<*>> upgradeTo(version: T) {
                    log.info("try to upgrade to $version")
                }

            }
        }

    }

    private val log = LogFactory.getLog(UpgradeServiceTest::class.java)


    @Autowired
    private val upgradeService: UpgradeService? = null

    @Test
    @Throws(Exception::class)
    fun systemUpgrade() {

        upgradeService!!.systemUpgrade(FemaleVersion::class.java)

    }

    @Test
    @Throws(Exception::class)
    fun oldSystemUpgrade() {
        upgradeService!!.systemUpgrade(object : VersionUpgrade<FemaleVersion> {
            @Throws(Exception::class)
            override fun upgradeToVersion(version: FemaleVersion) {
                log.info("try to upgrade to $version")
            }
        })
    }

}