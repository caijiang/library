package com.mingshz.owner.classic

import com.mingshz.owner.EnableClassicOwner
import com.mingshz.owner.share.ShareConfig
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

/**
 * @author CJ
 */
@Configuration
@Import(ShareConfig::class)
@EnableClassicOwner
open class Config 