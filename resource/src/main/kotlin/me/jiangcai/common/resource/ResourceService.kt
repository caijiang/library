package me.jiangcai.common.resource

import java.io.IOException
import java.io.InputStream

/**
 * 静态资源服务
 * @author CJ
 */
interface ResourceService {

    /**
     * 上传图片资源
     * @param imageBase 图片基本资源路径，**不可以以/开头**,**也无需以.类型结尾**
     * @param data 图片数据
     * @param expectType 在需要产生新图的时候期望的图片类型
     * @param preview 生成预览图期望的宽度规格
     * @param browse 生成浏览图期望的宽度规格
     * @param origin 是否保留原图
     */
    fun uploadImage(
        imageBase: String,
        data: InputStream,
        expectType: String = "png",
        preview: Int? = null,
        browse: Int? = null,
        origin: Boolean = false
    ): ImageResource

    /**
     * 删除图片资源, 所有该图片相关的实际资源会被删除
     * @param imageBase 图片基本资源路径，**不可以以/开头**,**也无需以.类型结尾**
     */
    fun deleteImage(imageBase: String)

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