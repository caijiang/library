package me.jiangcai.common.upgrade

/**
 * @author CJ
 */
interface VersionUpgrade<T> {

    /**
     * 从最近版本升级到step版本.
     * @param version 要升级的版本
     */
    fun upgradeToVersion(version: T)
}