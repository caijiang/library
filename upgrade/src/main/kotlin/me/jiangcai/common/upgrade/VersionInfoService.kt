package me.jiangcai.common.upgrade

import org.springframework.transaction.annotation.Transactional

/**
 * 版本信息服务
 * @author CJ
 */
interface VersionInfoService {


    /**
     * @param <T>  参考[me.jiangcai.common.upgrade.UpgradeService]中的范型
     * @param type 要求返回的类型
     * @return 当前数据库版本, 如果没有就返回null
     */
    fun <T : Enum<*>> currentVersion(type: Class<T>): T?

    /**
     * 保存输入的版本为数据库版本
     *
     * @param currentVersion 输入版本
     * @param <T>            参考[me.jiangcai.common.upgrade.UpgradeService]中的范型
    </T> */
    @Transactional
    fun <T : Enum<*>> updateVersion(currentVersion: T)
}