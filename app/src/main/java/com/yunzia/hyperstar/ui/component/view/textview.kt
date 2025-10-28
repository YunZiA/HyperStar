package com.yunzia.hyperstar.ui.component.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import kotlin.math.max
import kotlin.math.min

class textview {
}
class HoleMarqueeTextView1 @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    var text: String = ""
    var textColor: Int = Color.WHITE
    var textSize: Float = 48f
    var holeWidthPx: Float = 200f // 洞宽度
    var holeCenterRatio: Float = 0.5f // 洞中心位置(0~1)
    var marqueeSpeed: Float = 2f // 每帧移动px
    private var scrollOffset: Float = 0f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = textColor
        textSize = this@HoleMarqueeTextView1.textSize
    }

    private val scrollRunnable = object : Runnable {
        override fun run() {
            scrollOffset += marqueeSpeed
            invalidate()
            postDelayed(this, 16)
        }
    }

    fun startMarquee() {
        removeCallbacks(scrollRunnable)
        post(scrollRunnable)
    }

    fun stopMarquee() {
        removeCallbacks(scrollRunnable)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopMarquee()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = textColor
        paint.textSize = textSize

        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()
        val textY = viewHeight / 2 + (paint.fontMetrics.bottom - paint.fontMetrics.top) / 2 - paint.fontMetrics.bottom

        // 洞区域坐标
        val holeCenterX = viewWidth * holeCenterRatio
        val holeLeft = holeCenterX - holeWidthPx/2
        val holeRight = holeCenterX + holeWidthPx/2
        val fadeWidthPx: Float = 50f // 渐变区域宽度（左右各60px）

        // 字符串总宽度
        val textWidth = paint.measureText(text)
        val effectiveOffset = scrollOffset % (textWidth + viewWidth)

        // ---- 洞左侧裁剪并绘制 ----
        canvas.save()
        canvas.clipRect(0f, 0f, holeLeft, viewHeight)
        canvas.drawText(text, viewWidth - effectiveOffset - holeWidthPx, textY, paint)
        // 第二轮循环绘制
        canvas.drawText(text, viewWidth - effectiveOffset + textWidth + viewWidth - holeWidthPx, textY, paint)
        canvas.restore()

        // ---- 洞右侧裁剪并绘制 ----
        canvas.save()
        canvas.clipRect(holeRight, 0f, viewWidth, viewHeight)
        canvas.drawText(text, viewWidth - effectiveOffset, textY, paint)
        canvas.drawText(text, viewWidth - effectiveOffset + textWidth + viewWidth, textY, paint)
        canvas.restore()
    }
}


class HoleJumpMarqueeTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    var text: String = ""
    var textColor: Int = Color.WHITE
    var textSize: Float = 48f
    var holeWidthPx: Float = 0f // 洞宽度
    var holeCenterRatio: Float = 0.5f // 洞中心位置(0~1)
    var marqueeSpeed: Float = 2f // 每帧移动px
    private var scrollOffset: Float = 0f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = textColor
        textSize = this@HoleJumpMarqueeTextView.textSize
    }

    private val scrollRunnable = object : Runnable {
        override fun run() {
            scrollOffset += marqueeSpeed
            invalidate()
            postDelayed(this, 16)
        }
    }

    fun startMarquee() {
        removeCallbacks(scrollRunnable)
        scrollOffset = 0f
        invalidate()
        post(scrollRunnable)
    }

    fun stopMarquee() {
        removeCallbacks(scrollRunnable)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopMarquee()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = textColor
        paint.textSize = textSize

        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()
        val textY = viewHeight / 2 + (paint.fontMetrics.bottom - paint.fontMetrics.top) / 2 - paint.fontMetrics.bottom

        // 洞区域坐标
        val holeCenterX = viewWidth * holeCenterRatio
        val holeLeft = holeCenterX - holeWidthPx / 2
        val holeRight = holeCenterX + holeWidthPx / 2

        // 字符宽度数组
        val charWidths = FloatArray(text.length)
        paint.getTextWidths(text, charWidths)
        val textWidth = charWidths.sum()
        val loopWidth = viewWidth + textWidth

        val effectiveOffset = scrollOffset % loopWidth
        var charX = viewWidth - effectiveOffset

        for (i in text.indices) {
            val w = charWidths[i]
            val nextCharX = charX + w

            // 字符完全在洞左侧
            if (nextCharX <= holeLeft) {
                canvas.save()
                canvas.clipRect(0f, 0f, holeLeft, viewHeight)
                canvas.drawText(text[i].toString(), charX, textY, paint)
                canvas.restore()
            }
            // 字符完全在洞右侧
            else if (charX >= holeRight) {
                canvas.save()
                canvas.clipRect(holeRight, 0f, viewWidth, viewHeight)
                canvas.drawText(text[i].toString(), charX, textY, paint)
                canvas.restore()
            }
            // 字符部分或全部进入洞区，要传送
            else if (charX < holeRight && nextCharX > holeLeft) {
                // 计算传送距离
                val transmitX = holeLeft - (holeRight - charX)
                if (transmitX + w <= holeLeft && transmitX >= 0f) {
                    canvas.save()
                    canvas.clipRect(0f, 0f, holeLeft, viewHeight)
                    canvas.drawText(text[i].toString(), transmitX, textY, paint)
                    canvas.restore()
                }
            }
            charX += w
        }
    }
}