package com.mingshz.owner.single

import com.mingshz.owner.entity.OwnerEntity

/**
 * @author CJ
 */
class SingleOwner : OwnerEntity() {
    init {
        alias = "test"
        name = "test-owner"
        domain = "localhost"
    }
}