package me.jiangcai.common.upgrade

/**
 * @author CJ
 */
interface UpgradableBean {

    /**
     * @return 是否支持这个枚举类型
     */
    fun <T : Enum<*>> supportVersionEnum(versionEnumType: Class<T>): Boolean

    /**
     * 升级到特定版本
     */
    fun <T : Enum<*>> upgradeTo(version: T)
}