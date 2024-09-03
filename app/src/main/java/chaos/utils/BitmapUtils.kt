package chaos.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.HardwareRenderer
import android.graphics.PixelFormat
import android.graphics.RenderEffect
import android.graphics.RenderNode
import android.graphics.Shader
import android.hardware.HardwareBuffer
import android.media.ImageReader
import android.util.Log

class BitmapUtils {
    companion object {
        fun doBitmap(
            bitmap: Bitmap,
            isScale:Boolean,
            scaleFactor: Float,
            isBlur:Boolean,
            blurRadius:Float,
            isDim:Boolean,
            alpha: Int
        ):Bitmap{
            val startTime = System.nanoTime()
            var processedBitmap = bitmap

            if (isScale) {
                processedBitmap = scaleAndCropCenterBitmap(processedBitmap, scaleFactor)
            }

            if (isBlur) {
                processedBitmap = blur(processedBitmap, blurRadius)
            }

            if (isDim) {
                processedBitmap = dim(processedBitmap, alpha)
            }

            val endTime = System.nanoTime()

            Log.d("ggc", "doBitmap: "+(endTime-startTime)/ 1_000_000.0)

            return processedBitmap


        }


        private fun dim(bitmap: Bitmap,alpha: Int):Bitmap{
            val dimBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)

            val canvas = Canvas(dimBitmap)

            canvas.drawColor(Color.argb(alpha, 0, 0, 0))

            return dimBitmap
        }

        private fun scaleAndCropCenterBitmap(original: Bitmap, scaleFactor: Float): Bitmap {

            val originalWidth = original.width
            val originalHeight = original.height
            val adjustedScaleFactor = Math.min(scaleFactor, 2f)
            // 计算放大后的Bitmap尺寸
            val scaledWidth = (originalWidth * adjustedScaleFactor).toInt()
            val scaledHeight = (originalHeight * adjustedScaleFactor).toInt()
            // 创建一个新的Bitmap用于放大后的图像
            val scaledBitmap = Bitmap.createScaledBitmap(original, scaledWidth, scaledHeight, true)
            // 计算裁切区域的起始坐标（基于中心）
            val cropX = (scaledWidth - originalWidth) / 2
            val cropY = (scaledHeight - originalHeight) / 2
            // 创建一个与原始Bitmap相同尺寸的Bitmap用于存放裁切结果
            val resultBitmap = Bitmap.createBitmap(originalWidth, originalHeight, scaledBitmap.config)
            // 创建一个Canvas用于在resultBitmap上绘制
            val canvas = Canvas(resultBitmap)

            // 绘制裁切后的区域
            canvas.drawBitmap(scaledBitmap, -cropX.toFloat(), -cropY.toFloat(), null)

            return resultBitmap
         }

        private fun blur(bitmap: Bitmap, radius: Float): Bitmap {

            // 配置跟 bitmap 同样大小的 ImageReader
            val imageReader = ImageReader.newInstance(
                bitmap.width, bitmap.height,
                PixelFormat.RGBA_8888, 1,
                HardwareBuffer.USAGE_GPU_SAMPLED_IMAGE or HardwareBuffer.USAGE_GPU_COLOR_OUTPUT
            )
            val renderNode = RenderNode("RenderEffect")
            val hardwareRenderer = HardwareRenderer()
            // 将 ImageReader 的surface 设置到 HardwareRenderer 中
            hardwareRenderer.setSurface(imageReader.surface)
            hardwareRenderer.setContentRoot(renderNode)
            renderNode.setPosition(0, 0, imageReader.width, imageReader.height)
            // 使用 RenderEffect 配置模糊效果，并设置到 RenderNode 中。
            val blurRenderEffect = RenderEffect.createBlurEffect(
                radius, radius,
                Shader.TileMode.MIRROR
            )
            renderNode.setRenderEffect(blurRenderEffect)
            // 通过 RenderNode 的 RenderCanvas 绘制 Bitmap。
            val renderCanvas = renderNode.beginRecording()
            renderCanvas.drawBitmap(bitmap, 0f, 0f, null)
            renderNode.endRecording()
            // 通过 HardwareRenderer 创建 Render 异步请求。
            hardwareRenderer.createRenderRequest()
                .setWaitForPresent(true)
                .syncAndDraw()
            // 通过 ImageReader 获取模糊后的 Image 。
            val image = imageReader.acquireNextImage() ?: throw RuntimeException("No Image")
            // 将 Image 的 HardwareBuffer 包装为 Bitmap , 也就是模糊后的。
            val hardwareBuffer =
                image.hardwareBuffer ?: throw RuntimeException("No HardwareBuffer")
            val bit = Bitmap.wrapHardwareBuffer(hardwareBuffer, null)
                ?: throw RuntimeException("Create Bitmap Failed")
            hardwareBuffer.close()
            image.close()
            return bit
        }

    }


}