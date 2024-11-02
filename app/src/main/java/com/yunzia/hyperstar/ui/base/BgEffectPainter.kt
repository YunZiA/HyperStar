//package com.yunzia.hyperstar.ui.base
//
//import android.content.Context
//import android.content.res.Resources
//import android.graphics.Bitmap
//import android.graphics.ColorFilter
//import android.graphics.RenderEffect
//import android.graphics.RuntimeShader
//import android.graphics.drawable.Drawable
//import android.util.Log
//import androidx.compose.foundation.Canvas
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.blur
//import androidx.compose.ui.geometry.Size
//import androidx.compose.ui.graphics.BlurEffect
//import androidx.compose.ui.graphics.Canvas
//import androidx.compose.ui.graphics.asComposeRenderEffect
//import androidx.compose.ui.graphics.asImageBitmap
//import androidx.compose.ui.graphics.graphicsLayer
//import androidx.compose.ui.graphics.nativeCanvas
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.IntSize
//import androidx.compose.ui.unit.dp
//import kotlinx.coroutines.delay
//import java.io.InputStream
//import com.yunzia.hyperstar.R
//import kotlin.math.max
//
//class CustomRenderEffectDrawable(
//    context: Context,
//    private val size: IntSize,
//    private val renderEffect: RenderEffect
//) : Drawable() {
//
//    private val bitmap: Bitmap = Bitmap.createBitmap(size.width, size.height, Bitmap.Config.ARGB_8888)
//    private val canvas: Canvas = Canvas(bitmap)
//
//    override fun draw(p0: android.graphics.Canvas) {
//        // 保存当前画布的状态
//        canvas.save()
//
//        // 绘制 RenderEffect 到临时的 bitmap 上（实际上，这里应该直接应用到传入的 canvas 上）
//        // 但是，由于 RenderEffect 不能直接应用到 Compose 的 Canvas 上，
//        // 我们需要先在一个 BitmapCanvas 上绘制它，然后将其作为 ImageBitmap 在 Compose 中使用。
//        // 注意：这种方法效率不高，因为它涉及到额外的绘制和内存分配。
//        // 更好的方法可能是找到一个将 RenderEffect 直接集成到 Compose 中的方法（如果可能的话）。
//        val tempCanvas = android.graphics.Canvas(bitmap)
//        tempCanvas.drawRenderNode()
//        //tempCanvas.drawRenderEffect(renderEffect, null) // 绘制 RenderEffect 到 bitmap 上
//
//        // 将 bitmap 绘制到传入的 canvas 上（这将是 Compose 的 Canvas）
//        // 但是，由于我们是在一个 Drawable 中，我们实际上不会直接接触到 Compose 的 Canvas。
//        // 相反，我们将这个 Drawable 作为一个 ImageBitmap 的来源，并在 Compose 中使用它。
//        // 因此，下面的绘制调用实际上在这个上下文中是不必要的，只是为了说明。
//        // 在 Compose 中，您应该直接使用这个 bitmap 作为 Image 的源。
//        canvas.drawBitmap(bitmap, 0f, 0f, null)
//
//        // 恢复画布的状态
//        canvas.restore()
//    }
//
//    override fun setAlpha(p0: Int) {
//
//    }
//
//    override fun setColorFilter(p0: ColorFilter?) {
//
//    }
//
//    override fun getOpacity(): Int {
//        TODO("Not yet implemented")
//    }
//
//    // 您可能需要重写其他方法，如 setAlpha、setBounds 等，
//    // 但对于简单的用例，这些可能不是必需的。
//
//    // 为了在 Compose 中使用这个 Drawable，您可以将其转换为 ImageBitmap：
//    fun asImageBitmap(): ImageBitmap {
//        return bitmap.asImageBitmap()
//    }
//}
//
//@Composable
//fun BgEffectPainter() {
//    val context = LocalContext.current
//    val resources = context.resources
//    val shaderText = remember { loadShader(resources, R.raw.bg_frag) }
//    val mBgRuntimeShader = remember { RuntimeShader(shaderText) }
//
//    var uAnimTime by remember { mutableFloatStateOf(((System.nanoTime() / 1_000_000_000f))) }
//    var uResolution by remember { mutableStateOf(floatArrayOf(0f, 0f)) }
//    val uTranslateY by remember { mutableFloatStateOf(0f) }
//    val uBgBound by remember { mutableStateOf(floatArrayOf(0.0f, 0.4489f, 1.0f, 0.5511f)) }
//    val uPoints by remember { mutableStateOf(floatArrayOf(0.67f, 0.42f, 1.0f, 0.69f, 0.75f, 1.0f, 0.14f, 0.71f, 0.95f, 0.14f, 0.27f, 0.8f)) }
//    val uColors by remember { mutableStateOf(floatArrayOf(0.57f, 0.76f, 0.98f, 1.0f, 0.98f, 0.85f, 0.68f, 1.0f, 0.98f, 0.75f, 0.93f, 1.0f, 0.73f, 0.7f, 0.98f, 1.0f)) }
//    val uAlphaMulti by remember { mutableFloatStateOf(1.0f) }
//    val uNoiseScale by remember { mutableFloatStateOf(1.5f) }
//    val uPointOffset by remember { mutableFloatStateOf(0.1f) }
//    val uPointRadiusMulti by remember { mutableFloatStateOf(1.0f) }
//    val uSaturateOffset by remember { mutableFloatStateOf(0.2f) }
//    val uLightOffset by remember { mutableFloatStateOf(0.1f) }
//    val uAlphaOffset by remember { mutableFloatStateOf(0.5f) }
//    val uShadowColorMulti by remember { mutableFloatStateOf(0.3f) }
//    val uShadowColorOffset by remember { mutableFloatStateOf(0.3f) }
//    val uShadowNoiseScale by remember { mutableFloatStateOf(5.0f) }
//    val uShadowOffset by remember { mutableFloatStateOf(0.01f) }
//
//    LaunchedEffect(Unit) {
//        while (true) {
//            uAnimTime = (System.nanoTime() / 1_000_000_000f).toFloat()
//            delay(16) // 60 FPS
//        }
//    }
//
//    fun getRenderEffect(size:Size): RenderEffect{
//        uResolution = floatArrayOf(size.width, size.height)
//        mBgRuntimeShader.setFloatUniform("uAnimTime", uAnimTime)
//        mBgRuntimeShader.setFloatUniform("uResolution", uResolution)
//        mBgRuntimeShader.setFloatUniform("uTranslateY", uTranslateY)
//        mBgRuntimeShader.setFloatUniform("uPoints", uPoints)
//        mBgRuntimeShader.setFloatUniform("uColors", uColors)
//        mBgRuntimeShader.setFloatUniform("uNoiseScale", uNoiseScale)
//        mBgRuntimeShader.setFloatUniform("uPointOffset", uPointOffset)
//        mBgRuntimeShader.setFloatUniform("uPointRadiusMulti", uPointRadiusMulti)
//        mBgRuntimeShader.setFloatUniform("uSaturateOffset", uSaturateOffset)
//        mBgRuntimeShader.setFloatUniform("uShadowColorMulti", uShadowColorMulti)
//        mBgRuntimeShader.setFloatUniform("uShadowColorOffset", uShadowColorOffset)
//        mBgRuntimeShader.setFloatUniform("uShadowOffset", uShadowOffset)
//        mBgRuntimeShader.setFloatUniform("uBound", uBgBound)
//        mBgRuntimeShader.setFloatUniform("uAlphaMulti", uAlphaMulti)
//        mBgRuntimeShader.setFloatUniform("uLightOffset", uLightOffset)
//        mBgRuntimeShader.setFloatUniform("uAlphaOffset", uAlphaOffset)
//        mBgRuntimeShader.setFloatUniform("uShadowNoiseScale", uShadowNoiseScale)
//        return RenderEffect.createRuntimeShaderEffect(mBgRuntimeShader, "uTex")
//    }
//
//
//    Canvas(
//        modifier = Modifier.fillMaxSize()
//            .blur(30.dp)
//            .graphicsLayer {
//                //renderEffect = getRenderEffect(size).asComposeRenderEffect()
//            }
//    ) {
//
//    }
//}
//
//private fun loadShader(resources: Resources, resourceId: Int): String {
//    return try {
//        val inputStream: InputStream = resources.openRawResource(resourceId)
//        val scanner = inputStream.bufferedReader().use { it.readText() }
//        scanner
//    } catch (e: Exception) {
//        Log.e("Error", e.toString())
//        ""
//    }
//}
//
//@Composable
//fun ShowRuntimeShader() {
//    val context = LocalContext.current
//    val bound by remember { mutableStateOf(calculateBound(context)) }
//
//    LaunchedEffect(bound) {
//        if (isNightMode(context)) {
//            setPhoneDark(bound)
//        } else {
//            setPhoneLight(bound)
//        }
//    }
//
//    BgEffectPainter()
//}
//
//private fun calculateBound(context: Context): FloatArray {
//    val actionBarHeight = context.resources.getDimensionPixelSize(R.dimen.logo_area_height).toFloat()
//    val parentHeight = max(context.resources.displayMetrics.heightPixels.toFloat(), actionBarHeight)
//    val parentWidth = context.resources.displayMetrics.widthPixels.toFloat()
//    val height2 = actionBarHeight / parentHeight
//    return if (parentWidth <= parentHeight) {
//        floatArrayOf(0.0f, 1.0f - height2, 1.0f, height2)
//    } else {
//        floatArrayOf(((parentWidth - parentHeight) / 2.0f) / parentWidth, 1.0f - height2, parentHeight / parentWidth, height2)
//    }
//}
//
//private fun isNightMode(context: Context): Boolean {
//    // 示例实现，实际应用中可能需要更复杂的逻辑
//    return false
//}
//
//private fun setPhoneLight(bound: FloatArray) {
//    // 设置 Phone Light 模式的参数
//    // 这里只是一个示例，实际调用时需要传递参数
//}
//
//private fun setPhoneDark(bound: FloatArray) {
//    // 设置 Phone Dark 模式的参数
//    // 这里只是一个示例，实际调用时需要传递参数
//}