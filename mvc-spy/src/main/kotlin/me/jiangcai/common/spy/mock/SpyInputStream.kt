package me.jiangcai.common.spy.mock

import javax.servlet.ReadListener
import javax.servlet.ServletInputStream

/**
 * @author CJ
 */
class SpyInputStream(private val data: ByteArray) : ServletInputStream() {
    private var pos: Int = 0

    override fun isReady(): Boolean = true

    override fun available(): Int {
        return data.size - pos
    }

    override fun isFinished(): Boolean {
        return pos >= data.size
    }

    override fun read(): Int {
        return data[pos++].toInt()
    }

    override fun setReadListener(readListener: ReadListener?) {
    }
}