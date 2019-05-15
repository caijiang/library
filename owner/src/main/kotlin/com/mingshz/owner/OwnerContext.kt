package com.mingshz.owner

import com.mingshz.owner.entity.OwnerEntity
import javax.servlet.ServletRequest

/**
 * 获取owner的方法
 * 1. 最靠谱直接传递
 * 2. 通过request
 * 3. 直接获取，严重依赖于线程技术
 * @author CJ
 */
class OwnerContext {

    companion object {
        private const val OWNER_KEY = "com.mingshz.owner.key"

        private val pool = ThreadLocal<OwnerEntity>()

        internal fun updateContext(request: ServletRequest, owner: OwnerEntity) {
            request.setAttribute(OWNER_KEY, owner)
            pool.set(owner)
        }

        internal fun cleanContext(request: ServletRequest) {
            request.removeAttribute(OWNER_KEY)
            pool.remove()
        }

        /**
         * @return 获取 ownerEntity
         */
        fun getContext(request: ServletRequest? = null): OwnerEntity {
            return request?.let {
                it.getAttribute(OWNER_KEY) as OwnerEntity
            } ?: pool.get()
        }
    }

}