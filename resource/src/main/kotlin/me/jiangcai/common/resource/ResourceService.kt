package me.jiangcai.common.resource

import java.io.IOException
import java.io.InputStream

/**
 * 静态资源服务
 * @author CJ
 */
interface ResourceService {

    /**
     * 上传资源
     *
     * @param path 资源路径（相对）,**不可以以/开头**
     * @param data 数据
     * @return 新资源的资源定位符
     * @throws IOException 保存时出错
     */
    @Throws(IOException::class)
    fun uploadResource(path: String, data: InputStream): Resource

    /**
     * 移动资源
     *
     * @param path     资源路径（相对）,**不可以以/开头**
     * @param fromPath 原资源路径
     * @return 新资源的资源定位符
     * @throws IOException 保存时出错
     */
    @Throws(IOException::class)
    fun moveResource(path: String, fromPath: String): Resource

    /**
     * 获取指定资源的资源定位符
     *
     * @param path 资源路径（相对）,**不可以以/开头**
     * @return 资源实体
     */
    fun getResource(path: String): Resource

    /**
     * 删除资源
     *
     * @param path 资源路径（相对）,**不可以以/开头**
     * @throws IOException 删除时错误
     */
    @Throws(IOException::class)
    fun deleteResource(path: String)
}