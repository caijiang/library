package me.jiangcai.common.jpa.type

/**
 * 表示一个可以在数据表中以json描述的类型
 * 通常可以一个[Map]的方式进行使用
 * @author CJ
 */
class JSONStoring(
    var data: Map<*, *>
) {
    @Suppress("UNCHECKED_CAST")
    fun <X, Y> readAs(): Map<X, Y> {
        return data as Map<X, Y>
    }
}