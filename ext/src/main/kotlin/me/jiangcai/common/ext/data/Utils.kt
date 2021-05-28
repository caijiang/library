package me.jiangcai.common.ext.data

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

/**
 * 如果没有指定排序，则采用特定排序。
 */
@Suppress("unused")
fun Pageable.defaultSortBy(sort: Sort): Pageable {
    // 没有任何一个有效排序
    if (this.sort.none { true }) {
        return CopyPageable(this, sort)
    }
    return this
}