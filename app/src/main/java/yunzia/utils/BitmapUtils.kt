package yunzia.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.HardwareRenderer
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RenderEffect
import android.graphics.RenderNode
import android.graphics.Shader
import android.hardware.HardwareBuffer
import android.media.ImageReader
import android.util.Log
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette
import com.yunzia.hyperstar.hook.tool.starLog

class BitmapUtils {
    companion object {
        fun doBitmap(
            bitmap: Bitmap,
            isScale:Boolean,
            scaleFactor: Float,
            isBlur:Boolean,
            blurRadius:Float,
            isDim:Boolean,
            alpha: Float
        ):Bitmap{
            val startTime = System.nanoTime()
            var processedBitmap = bitmap

            if (isScale) {
                processedBitmap = scaleAndCropCenterBitmap(processedBitmap, scaleFactor)
            }


            if (isDim) {
                processedBitmap = reduceBrightness(processedBitmap,alpha)
                //processedBitmap = dim(processedBitmap, alpha)
            }


            if (isBlur) {
                processedBitmap = blur(processedBitmap, blurRadius)
            }
            val endTime = System.nanoTime()

            Log.d("ggc", "doBitmap: ${(endTime-startTime)/ 1_000_000.0}ms")

            return processedBitmap


        }


        fun auto(bitmap: Bitmap):Bitmap{
            val result = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
            val canvas = Canvas(result)
            val palette = Palette.from(bitmap).generate()

            val dominantColor = palette.getDominantColor(Color.TRANSPARENT)
            if (dominantColor == Color.TRANSPARENT){
                starLog.log("backgroundColor is null")
                return bitmap
            }

            val outHsl = FloatArray(3)
            ColorUtils.colorToHSL(dominantColor,outHsl)
            //starLog.log("1 Hue: ${outHsl[0]}, Saturation: ${outHsl[1]}, Lightness: ${outHsl[2]}")
            outHsl[2] = if (outHsl[2] > 0.5f){
                0.4f
            }else if (outHsl[2] < 0.11f){
                0.2f
            } else if (outHsl[2] < 0.2f){
                0.25f
            }else{
                outHsl[2]
            }
            outHsl[1] = if (outHsl[1] < 0.45f){
                0.5f
            } else if (outHsl[1] > 0.85f) {
                0.5f
            }else outHsl[1]

            //starLog.log("2 Hue: ${outHsl[0]}, Saturation: ${outHsl[1]}, Lightness: ${outHsl[2]}")

            val backgroundColor = ColorUtils.HSLToColor(outHsl)

            canvas.drawColor(backgroundColor)

            return result
        }

        fun mixColorsMultiplicative(color1: Int, color2: Int): Int {
            val r1 = Color.red(color1)
            val g1 = Color.green(color1)
            val b1 = Color.blue(color1)
            val a1 = Color.alpha(color1)

            val r2 = Color.red(color2)
            val g2 = Color.green(color2)
            val b2 = Color.blue(color2)
            val a2 = Color.alpha(color2)

            val r = (r1 * r2 / 255).toInt()
            val g = (g1 * g2 / 255).toInt()
            val b = (b1 * b2 / 255).toInt()
            val a = (a1 * a2 / 255).toInt()

            return Color.argb(a, r, g, b)
        }

        fun mixColorsLinear(color1: Int, color2: Int, ratio: Float): Int {
            val r1 = Color.red(color1)
            val g1 = Color.green(color1)
            val b1 = Color.blue(color1)
            val a1 = Color.alpha(color1)

            val r2 = Color.red(color2)
            val g2 = Color.green(color2)
            val b2 = Color.blue(color2)
            val a2 = Color.alpha(color2)

            val r = (r1 * (1 - ratio) + r2 * ratio).toInt()
            val g = (g1 * (1 - ratio) + g2 * ratio).toInt()
            val b = (b1 * (1 - ratio) + b2 * ratio).toInt()
            val a = (a1 * (1 - ratio) + a2 * ratio).toInt()

            return Color.argb(a, r, g, b)
        }

        fun getColorBrightness(color: Int): Int {
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)

            // 使用加权和计算亮度
            val brightness = (0.299 * red + 0.587 * green + 0.114 * blue).toInt()

            return brightness
        }

        fun adjustColorBrightness(color: Int, brightness: Float): Int {
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)

            val alpha = Color.alpha(color)

            // 计算新的 RGB 值
            val newRed = (red + (255 - red) * brightness).toInt().coerceIn(0, 255)
            val newGreen = (green + (255 - green) * brightness).toInt().coerceIn(0, 255)
            val newBlue = (blue + (255 - blue) * brightness).toInt().coerceIn(0, 255)

            // 返回新的颜色值
            return Color.argb(alpha, newRed, newGreen, newBlue)
        }

        private fun reduceBrightness(bitmap: Bitmap, factor: Float): Bitmap {

            val result = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)

            val canvas = Canvas(result)
            val paint = Paint()
            paint.isFilterBitmap = true
            paint.isDither = true

            val colorMatrix = ColorMatrix()

            val lum = factor * 255 * 0.01f
            val brightnessArray = floatArrayOf(
                1f, 0f, 0f, 0f, lum,
                0f, 1f, 0f, 0f, lum,
                0f, 0f, 1f, 0f, lum,
                0f, 0f, 0f, 1f, 0f
            )
            colorMatrix.set(brightnessArray)
            //colorMatrix.setSaturation(saturation) // 降低饱和度来达到降低亮度的效果
            paint.setColorFilter(ColorMatrixColorFilter(colorMatrix))
            canvas.drawBitmap(bitmap, 0f, 0f, paint)
            return result
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