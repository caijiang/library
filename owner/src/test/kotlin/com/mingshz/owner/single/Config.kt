package com.mingshz.owner.single

import com.mingshz.owner.EnableSingleOwner
import com.mingshz.owner.share.ShareConfig
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * @author CJ
 */
@Configuration
@Import(ShareConfig::class)
@EnableSingleOwner(ownerClass = SingleOwner::class)
open class Config