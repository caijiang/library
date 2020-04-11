package me.jiangcai.common.ext.tools

import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

/**
 * 将环境变量替换输出到java properties file
 * 1. 文件路径
 * 1. 追加a还是替代r，默认追加a
 * 1. 前缀, 默认 e2p_
 */
fun main(args: Array<String>) {
    val targetFile = args[0]
    val mode =
        if (args.getOrElse(1) { "a" } == "a") StandardOpenOption.APPEND
        else StandardOpenOption.TRUNCATE_EXISTING
    val prefix = args.getOrElse(2) { "e2p_" }

    val list = System.getenv()
        .filter { it.key.startsWith(prefix) }
        // 整理成Pair
        .map {
            Pair(
                it.key.removePrefix(prefix).replace("_", "."),
                it.value
            )
        }.map {
            "${it.first}=${it.second}"
        }

    val targetFilePath = Paths.get(targetFile)

    Files.write(
        targetFilePath,
        list,
        Charset.forName("UTF-8"),
        StandardOpenOption.WRITE,
        StandardOpenOption.CREATE,
        mode
    )
}