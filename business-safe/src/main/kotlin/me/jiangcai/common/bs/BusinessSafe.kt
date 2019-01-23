package me.jiangcai.common.bs

/**
 * 表明为业务安全的方法
 * 系统会按照参数中第一个 [MultipleBusinessLocker]或者[BusinessLocker] 作为业务锁宿主，如果一个都没有找到，那么默认第一个参数为业务锁宿主
 * @author CJ
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@MustBeDocumented
annotation class BusinessSafe