package me.jiangcai.common.resource.bean

import me.jiangcai.common.resource.ImageResource
import me.jiangcai.common.resource.ResourceService
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.imageio.ImageIO

/**
 * @author CJ
 */
abstract class AbstractResourceService : ResourceService {

    override fun deleteImage(imageBase: String) {
        ImageIO.getReaderFormatNames().forEach { type ->
            deleteResource("${imageBase}_preview.${type}")
            deleteResource("${imageBase}_browse.${type}")
            deleteResource("${imageBase}.${type}")
        }
    }

    override fun uploadImage(
        imageBase: String,
        data: InputStream,
        expectType: String,
        preview: Int?,
        browse: Int?,
        origin: Boolean
    ): ImageResource {

        val type = expectType.toLowerCase()
        //  如果需要保存原图，那么原来的数据得留着
        val readableData = if (origin) {
            val buffer = data.readBytes()
            ByteArrayInputStream(buffer)
        } else
            data
        val stream = ImageIO.createImageInputStream(readableData)


        val readers = ImageIO.getImageReaders(stream)

        while (readers.hasNext()) {
            val reader = readers.next()
            reader.input = stream
            val image = reader.read(reader.minIndex)

            val previewUrl = preview?.let { maxWidth(image, it, type) }
                ?.let { uploadResource("${imageBase}_preview.$type", it) }?.httpUrl()?.toString()
            val browseUrl = browse?.let { maxWidth(image, it, type) }
                ?.let { uploadResource("${imageBase}_browse.$type", it) }?.httpUrl()?.toString()
            val originUrl = if (origin) {
                readableData.reset()
                uploadResource("${imageBase}.${reader.formatName.toLowerCase()}", readableData).httpUrl().toString()
            } else null

            if (previewUrl == null && browseUrl == null && originUrl == null)
                throw IllegalArgumentException("none of 'preview','browse','origin' is enable.")

            return ImageResource(
                imageBase, previewUrl, browseUrl, originUrl
            )
        }

        throw IllegalArgumentException("unknown image type.")
    }

    private fun maxWidth(image: BufferedImage, width: Int, type: String): InputStream {
        val current = image.width
        if (current <= width)
            return storeImageAsType(image, type)

        val rate = width.toDouble() / current.toDouble()
        val height = (image.height * rate).toInt()

        val bufferedImage = BufferedImage(width, height, image.type)
        val graphics = bufferedImage.graphics

        graphics.drawImage(image, 0, 0, width, height, null)

        return storeImageAsType(bufferedImage, type)
    }

    private fun storeImageAsType(image: BufferedImage, type: String): InputStream {
        val buffer = ByteArrayOutputStream()
        ImageIO.write(image, type, buffer)
        return ByteArrayInputStream(buffer.toByteArray())
    }

}