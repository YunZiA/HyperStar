package com.yunzia.hyperstar.ui.component.view

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.LinearInterpolator



class MarqueeTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 48f
        color = Color.BLACK
    }

    private var text = "这是一段很长的滚动文字，中间要避开一个区域"
    private var textWidth = 0f
    private var offset = 0f // 滚动偏移量
    private var gapStart = 0f // 中间空白区起点（相对中心）
    private var gapWidth = 80f// 中间空白宽度（如摄像头区域）

    private val animator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 1000
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener {
            offset += 2f // 每帧左移 2px
            if (offset > textWidth) offset = 0f
            invalidate()
        }
    }

    init {
        textWidth = paint.measureText(text)
        animator.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val tunnelLeft = centerX - gapWidth / 2f  // 洞的左边界
        val tunnelRight = centerX + gapWidth / 2f // 洞的右边界

        val y = (height / 2f) + paint.textSize / 3 // 垂直居中

        // 计算当前文本的起始位置（循环滚动）
        var x = -offset % (textWidth + width) // 确保循环
        if (x < 0) x += textWidth + width

        // 绘制两遍文本，实现无缝循环
        for (repeat in 0 until 2) {
            var currentX = x
            for (i in text.indices) {
                val char = text[i].toString()
                val charWidth = paint.measureText(char)

                val left = currentX
                val right = currentX + charWidth

                // 情况1：字符完全在洞左侧 → 正常绘制
                if (right <= tunnelLeft) {
                    canvas.drawText(char, left, y, paint)
                }
                // 情况2：字符完全在洞右侧 → 正常绘制
                else if (left >= tunnelRight) {
                    canvas.drawText(char, left, y, paint)
                }
                // 情况3：字符与洞重叠 → 传送：在洞右侧“镜像”出现
                else {
                    // 计算字符应出现在洞右侧的位置
                    val teleportX = tunnelRight + (left - tunnelLeft)
                    canvas.drawText(char, teleportX, y, paint)
                }

                currentX += charWidth
                if (currentX > width + charWidth) break
            }
            x -= textWidth // 第二次绘制用于无缝循环
        }
    }
}


class TunnelMarqueeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 60f
        color = Color.WHITE
        textAlign = Paint.Align.LEFT
    }

    private var text = "HELLO WORLD "
    private var charWidths: FloatArray = FloatArray(text.length)
    private var totalTextWidth = 0f

    private var gapWidth = 80f.dp  // 洞的宽度
    private var tunnelStart = 0f   // 洞的左边界
    private var tunnelEnd = 0f     // 洞的右边界

    private var offset = 0f // 滚动偏移量

    private val animator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 100
        repeatCount = ValueAnimator.INFINITE
        interpolator = LinearInterpolator()
        addUpdateListener {
            offset += 2f // 每帧左移 2px
            invalidate()
        }
    }

    init {
        // 测量每个字符宽度
        paint.getTextWidths(text, charWidths)
        totalTextWidth = charWidths.sum()
        animator.start()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val centerX = w / 2f
        tunnelStart = centerX - gapWidth / 2f
        tunnelEnd = centerX + gapWidth / 2f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val y = (height / 2f) + paint.textSize / 3 // 垂直居中

        var x = -offset % (width + totalTextWidth) // 循环滚动起点

        // 如果 x 太左，移到右侧循环
        if (x < 0) x += width + totalTextWidth

        // 从左到右绘制每个字符
        for (i in text.indices) {
            val cw = charWidths[i]
            val char = text[i].toString()

            // 当前字符的绘制区域
            val left = x
            val right = x + cw

            // 情况1：字符完全在洞左边 → 正常绘制
            if (right <= tunnelStart) {
                canvas.drawText(char, x, y, paint)
            }
            // 情况2：字符完全在洞右边 → 正常绘制
            else if (left >= tunnelEnd) {
                canvas.drawText(char, x, y, paint)
            }
            // 情况3：字符穿过洞 → 在洞右边“传送”出现
            else {
                // 计算字符进入洞的位置
                val overlap = tunnelStart - left
                if (overlap > 0) {
                    // 左半部分在洞左边
                    val leftPart = text.substring(i, i+1).takeWhile { paint.measureText(it.toString()) <= overlap }
                    canvas.drawText(leftPart, x, y, paint)
                }
                // 右半部分“传送”到洞右边
                val teleportX = tunnelEnd + (x - tunnelStart) // 保持相对位置
                canvas.drawText(char, teleportX, y, paint)
            }

            x += cw
            if (x > width + cw) break // 超出屏幕停止
        }
    }
}

// dp 转 px
val Float.dp
    get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics)
