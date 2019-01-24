package me.jiangcai.common.jpa

/**
 * 实现该方法可以新增包路径到jpa去
 * @author CJ
 */
interface JpaPackageScanner {
    /**
     * @param prefix 前置名称 [EnableJpa.prefix]
     * @param set 集合
     */
    fun addJpaPackage(prefix: String, set: MutableSet<String>)
}