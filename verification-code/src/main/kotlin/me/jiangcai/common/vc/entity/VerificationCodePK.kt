package me.jiangcai.common.vc.entity

import me.jiangcai.common.vc.VerificationType
import java.io.Serializable


/**
 * @author CJ
 */
data class VerificationCodePK(
    var mobile: String?,
    var type: Int?
) : Serializable {

    companion object {
        private const val serialVersionUID = -6663341165809474810L
    }

    constructor(mobile: String, type: VerificationType) : this(mobile, type.id())

    @Suppress("unused")
    constructor() : this(null, null)

}
