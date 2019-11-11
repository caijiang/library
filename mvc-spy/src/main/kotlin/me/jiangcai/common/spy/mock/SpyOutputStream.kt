package me.jiangcai.common.spy.mock

import javax.servlet.ServletOutputStream
import javax.servlet.WriteListener

/**
 * @author CJ
 */
class SpyOutputStream(private val core: ServletOutputStream) : ServletOutputStream() {
    val data = mutableListOf<Byte>()
    override fun isReady(): Boolean {
        return core.isReady
    }

    override fun write(b: Int) {
        core.write(b)
        data.add(b.toByte())
    }

    override fun setWriteListener(writeListener: WriteListener?) {
        core.setWriteListener(writeListener)
    }

}