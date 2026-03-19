package com.yunzia.hyperstar.ui.blend

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.util.Log
import androidx.collection.mutableIntListOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.unit.IntSize

class BlendedBlurPainter {

    val shader = RuntimeShader(blurShaderCode)
    private val layers = mutableListOf<Pair<Color, BlendMode>>()

    private val blendModeUniforms = IntArray(8)
    private val layerColorUniforms = FloatArray(32)

    fun renderEffect() = RenderEffect.createRuntimeShaderEffect(shader, "inputImage").asComposeRenderEffect()

    fun disableBlur() = shader.setIntUniform("enableBlur", 0)
    fun enableBlur() = shader.setIntUniform("enableBlur", 1)

    fun setResolution(size: IntSize) = shader.setFloatUniform("resolution", size.width.toFloat(), size.height.toFloat())


    fun setBlurRadius(blurRadius: Float) = shader.setFloatUniform("blurRadius", blurRadius) // 模糊半径


    fun removeAllLayer() {
        layers.clear()
        blendModeUniforms.fill(0)
        layerColorUniforms.fill(0f)
        updateLayers()
    }
    fun addLayer(color: Color, blendMode: BlendMode){
        layers.add(Pair(color, blendMode))

        updateLayers()
    }
    fun setLayers(newLayers: List<Pair<Color, BlendMode>>){
        layers.clear()
        layers.addAll(newLayers)
        layers.forEachIndexed { index, pair ->
            blendModeUniforms[index] = pair.second.value
            val base = index * 4
            layerColorUniforms[base] = pair.first.red
            layerColorUniforms[base + 1] = pair.first.green
            layerColorUniforms[base + 2] = pair.first.blue
            layerColorUniforms[base + 3] = pair.first.alpha
        }
        updateLayers()
    }
    fun setLayer(newLayer: Pair<Color, BlendMode>){
        layers.clear()
        layers.add(newLayer)
        updateLayers()
    }
    private fun updateLayers() {
        shader.setIntUniform("layerCount", layers.size)
        if (layers.isEmpty()){
            shader.setIntUniform("blendModes", 0) // 0 表示无效模式，你的 AGSL 会 fallback 到 blend
            shader.setColorUniform("layerColors", Color.Transparent.toColorLong())
            return
        }
        Log.d("ggc", "updateLayers: ${blendModeUniforms}")
        shader.setIntUniform("blendModes",blendModeUniforms)
        shader.setFloatUniform("layerColors", layerColorUniforms)
        layers.forEachIndexed { index, (color, blendMode) ->
           // shader.setIntUniform("blendModes[0]", blendMode.value) // 注意：BlendMode 枚举的 ordinal 是否等于你的模式值？可能需要映射
//            shader.setColorUniform("layerColors[$index]", color.toColorLong())
        }
    }

}