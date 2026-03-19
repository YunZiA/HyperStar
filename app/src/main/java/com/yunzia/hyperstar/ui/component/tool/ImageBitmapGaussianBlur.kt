package com.yunzia.hyperstar.ui.component.tool

import android.graphics.RuntimeShader
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.unit.IntSize

class ImageBitmapGaussianBlur {

    companion object {
        // Static GLSL code for the separable Gaussian blur fragment shader
        private const val FRAGMENT_SHADER_CODE = """
            #version 300 es
            precision mediump float;

            uniform shader image;
            uniform vec2 direction; // (1,0) for horizontal, (0,1) for vertical
            uniform vec2 texSize;
            uniform float weights[15]; // Precomputed Gaussian weights for a max 15-sample kernel

            out vec4 outColor;

            void main() {
                vec2 fragCoord = gl_FragCoord.xy; // Get the current fragment coordinate
                vec4 accumColor = vec4(0.0);
                float totalWeight = 0.0;
                vec4 sampleColor;

                // Unrolled loop for a 15-sample kernel (supports blur radii up to ~7)
                // Only non-zero weights contribute significantly.
                for (int i = 0; i < 15; i++) {
                    int sampleOffset = i - 7; // Calculate offset from center (-7 to +7)
                    vec2 offset = (vec2(float(sampleOffset), 0.0) * direction) / texSize;
                    sampleColor = image.eval(fragCoord + offset);
                    float weight = weights[i];
                    accumColor += sampleColor * weight;
                    totalWeight += weight;
                }

                outColor = accumColor / totalWeight;
            }
        """
    }

    private val runtimeShader: RuntimeShader

    init {
        try {
            runtimeShader = RuntimeShader(FRAGMENT_SHADER_CODE)
        } catch (e: Exception) {
            throw RuntimeException("Failed to initialize RuntimeShader for blur: ${e.message}", e)
        }
    }

    /**
     * Applies a Gaussian blur to the given [inputImageBitmap].
     *
     * @param inputImageBitmap The source ImageBitmap to be blurred.
     * @param blurRadius The radius of the blur effect. Higher values mean more blur.
     * @return A new ImageBitmap containing the blurred image, or null if an error occurs during processing.
     */

    fun getBlurBrush(inputImageBitmap: ImageBitmap, blurRadius: Float,intSize: IntSize): ShaderBrush {
        runtimeShader.setFloatUniform("texSize", intSize.width.toFloat(), intSize.height.toFloat())
        runtimeShader.setFloatUniform("blurRadius", blurRadius)
        runtimeShader.setFloatUniform("direction", 1.0f, 0.0f) // Horizontal
        runtimeShader.setInputShader("image", ImageShader(inputImageBitmap))

        return ShaderBrush(runtimeShader)

    }

}