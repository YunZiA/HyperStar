package com.yunzia.hyperstar.hook.app.plugin.powermenu

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.graphics.Color
import android.graphics.Outline
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.ImageView
import yunzia.utils.DensityUtil.Companion.dpToPx
import com.yunzia.hyperstar.hook.app.plugin.powermenu.PowerMenu.Item

class GridAdapter(private val context: Context, private val items: List<Item?>, private val itemClick: () -> Unit) : BaseAdapter() {

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Item {
        return items[position]!!
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            view = createItemLayout(context, items[position]!!)
        }



        //addShowAnimation(view)
        view.setOnClickListener {
            if (view is ViewGroup){
                view.getChildAt(0).isSelected =  !view.getChildAt(0).isSelected
            }
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            getItem(position).click?.invoke(view,view.context)
            itemClick()

        }
        return view
    }

    private fun addShowAnimation(view: View) {

        val scaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f).apply {
            duration = 250  // 动画持续时间
            interpolator = AccelerateDecelerateInterpolator()  // 使用加速减速插值器
        }

        val scaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1f).apply {
            duration = 250  // 动画持续时间
            interpolator = AccelerateDecelerateInterpolator()  // 使用加速减速插值器
        }

        // 创建一个 AnimatorSet 来同时播放多个动画
        val animatorSet = AnimatorSet().apply {
            playTogether(scaleXAnimator, scaleYAnimator)
        }

        // 开始动画
        animatorSet.start()
    }


    private fun createStateListDrawable(context: Context, state: Boolean): StateListDrawable {
        val selectedColor = Color.parseColor("#277af7") // 按下时的颜色
        val defaultColor = Color.parseColor("#59FFFFFF") // 默认颜色

        val selectedDrawable = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(selectedColor)
        }

        val defaultDrawable = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(defaultColor)
        }

        val stateListDrawable = StateListDrawable().apply {
            addState(intArrayOf(android.R.attr.state_selected), selectedDrawable)
            addState(intArrayOf(), if (state) selectedDrawable else defaultDrawable)
        }

        return stateListDrawable
    }

    private fun createItemLayout(context: Context, item: Item): FrameLayout {
        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.OVAL

        val res =  context.resources

        val sliderWidth =res.getIdentifier("slider_width","dimen","miui.systemui.plugin")
        val size = res.getDimensionPixelOffset(sliderWidth)- dpToPx(res,5f).toInt()

        drawable.setColor(if(item.state){
            Color.parseColor("#2856FF")
        }else{
            Color.parseColor("#40FFFFFF")
        })

        val layout = FrameLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(size, size).apply {

            }
            clipToOutline = true
            clipToPadding = true
            outlineProvider = object : ViewOutlineProvider(){
                override fun getOutline(view: View?, outline: Outline?) {
                    val size = view!!.width.coerceAtMost(view.height);
                    outline?.setOval(0, 0, size, size);
                }

            }
            background = if (item.isEmpty == false) createStateListDrawable(context,item.state) else null
            setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // 开始按压动画
                        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                            this,
                            PropertyValuesHolder.ofFloat("scaleX", 0.8f),
                            PropertyValuesHolder.ofFloat("scaleY", 0.8f)
                        ).apply {
                            duration = 150
                        }
                        scaleDown.start()
                        true
                    }
                    MotionEvent.ACTION_UP -> {
                        val scaleUp = ObjectAnimator.ofPropertyValuesHolder(
                            this,
                            PropertyValuesHolder.ofFloat("scaleX", 1.0f),
                            PropertyValuesHolder.ofFloat("scaleY", 1.0f)
                        ).apply {
                            duration = 150
                        }
                        scaleUp.start()
                        v.performClick()
                        true
                    }
                    MotionEvent.ACTION_CANCEL-> {
                        // 结束按压动画
                        val scaleUp = ObjectAnimator.ofPropertyValuesHolder(
                            this,
                            PropertyValuesHolder.ofFloat("scaleX", 1.0f),
                            PropertyValuesHolder.ofFloat("scaleY", 1.0f)
                        ).apply {
                            duration = 150
                        }
                        scaleUp.start()
                        true
                    }
                    else -> false

                }
            }
        }

        val imageView = ImageView(context).apply {
            scaleType =  ImageView.ScaleType.CENTER_INSIDE
            setImageDrawable(item.image)
            setPadding(50,50,50,50)
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            //background = drawable

        }

        //recovery.foreground = Recovery


        layout.addView(imageView)




        return layout
    }
}
