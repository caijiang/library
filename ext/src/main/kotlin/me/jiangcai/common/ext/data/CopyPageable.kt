package me.jiangcai.common.ext.data

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

/**
 * @author CJ
 */
class CopyPageable(init: Pageable, private val overrideSort: Sort) : Pageable by init {
    override fun getSort(): Sort = overrideSort
}