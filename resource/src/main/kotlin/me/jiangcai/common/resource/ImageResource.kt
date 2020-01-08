package me.jiangcai.common.resource

/**
 * @author CJ
 */
data class ImageResource(
    val imageBase: String,
    val previewUrl: String? = null,
    val browseUrl: String? = null,
    val originUrl: String? = null
)