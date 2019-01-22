package me.jiangcai.common.upgrade

import org.springframework.transaction.annotation.Transactional

/**
 * @author CJ
 */
interface UpgradeService {

    /**
     * 尝试系统升级,在发现需要升级以后将调用升级者,可以通过JDBC操作数据表.
     *
     *
     * 需要注意的是,版本升级采用的是逐步升级策略,比如数据库标记版本为1.0 然后更新到3.0 中间还存在2.0(这也是为什么版本标记是用枚举保
     * 存的原因),那么会让升级者升级到2.0再到3.0
     *
     *
     * 如果没有发现数据库版本标记 那么就默认为已经是当前版本了.
     *
     *
     * @param upgrade 负责提供系统升级业务的升级者
     * @param <T>     维护版本信息的枚举类,最新的值将作为最新版本进行升级
     */
    @Transactional
    fun <T : Enum<*>> systemUpgrade(upgrade: VersionUpgrade<T>)
}