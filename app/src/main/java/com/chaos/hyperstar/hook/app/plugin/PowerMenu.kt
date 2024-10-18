package com.chaos.hyperstar.hook.app.plugin

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.content.res.XModuleResources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.os.RemoteException
import android.provider.Settings
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.BaseAdapter
import android.widget.FrameLayout
import android.widget.GridView
import android.widget.ImageView
import chaos.utils.DensityUtil.Companion.dpToPx
import com.chaos.hyperstar.R
import com.chaos.hyperstar.hook.app.plugin.PowerMenu.Item
import com.chaos.hyperstar.hook.base.BaseHooker
import com.chaos.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_InitPackageResources


class PowerMenu : BaseHooker() {

    var icBootloader = 0
    var icRecovery = 0
    var icAirplaneOn = 0
    var icAirplaneOff = 0
    var icSilentOn = 0
    var icSilentOff = 0

    val isPowerMenuNavShow = XSPUtils.getBoolean("is_power_menu_nav_show",false)
    val isUsePowerMenuPlus = XSPUtils.getBoolean("is_use_power_menu_plus",false)

    override fun doResources(
        resparam: XC_InitPackageResources.InitPackageResourcesParam?,
        modRes: XModuleResources?
    ) {
        super.doResources(resparam, modRes)


        icBootloader = resparam?.res?.addResource(modRes,R.drawable.ic_bootloader)!!
        icRecovery = resparam.res?.addResource(modRes,R.drawable.ic_recovery)!!
        icAirplaneOn = resparam.res?.addResource(modRes,R.drawable.ic_airplane_on)!!
        icAirplaneOff = resparam.res?.addResource(modRes,R.drawable.ic_airplane_off)!!
        icSilentOn = resparam.res?.addResource(modRes,R.drawable.ic_silent_on)!!
        icSilentOff = resparam.res?.addResource(modRes,R.drawable.ic_silent_off)!!
    }



    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)

        val MiuiGlobalActionsDialog = XposedHelpers.findClass("com.android.systemui.miui.globalactions.MiuiGlobalActionsDialog",classLoader)

        if (isPowerMenuNavShow){
            XposedHelpers.findAndHookMethod(MiuiGlobalActionsDialog,"initDialog",object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    val thisObj = param?.thisObject
                    val mRoot = XposedHelpers.getObjectField(thisObj,"mRoot") as FrameLayout
                    val flags = (View.SYSTEM_UI_FLAG_VISIBLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
                    mRoot.systemUiVisibility = flags

                }
            })

        }

        if (!isUsePowerMenuPlus) return

        var group: View? = null
        XposedHelpers.findAndHookMethod(MiuiGlobalActionsDialog,"initViews",object : XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                val thisObj = param?.thisObject
                val mContext = XposedHelpers.getObjectField(thisObj,"mContext") as Context
                val res = mContext.resources
                val mTalkbackLayout = XposedHelpers.getObjectField(thisObj,"mTalkbackLayout") as FrameLayout
                group = menu(mContext,thisObj)
                val width = dpToPx(res,282f).toInt()
                val layoutParams = FrameLayout.LayoutParams(width, FrameLayout.LayoutParams.WRAP_CONTENT )
                layoutParams.bottomMargin = dpToPx(res,110f).toInt()
                layoutParams.gravity = Gravity.CENTER_HORIZONTAL+Gravity.BOTTOM
                mTalkbackLayout.addView(group, layoutParams)

                val initialHeight = 0
                val targetHeight = width

                ValueAnimator.ofInt(initialHeight, targetHeight).apply {
                    duration = 250  // 动画持续时间
                    interpolator = AccelerateDecelerateInterpolator()  // 使用加速减速插值器
                    addUpdateListener { animation ->
                        val value = animation.animatedValue as Int
                        val params = group!!.layoutParams
                        params.width = value
                        group!!.layoutParams = params
                    }
                    start()
                }


            }
        })

        val SliderView = XposedHelpers.findClass("com.android.systemui.miui.globalactions.SliderView",classLoader)

        XposedHelpers.findAndHookMethod(SliderView,"handleActionMoveForAlpha",Float::class.java,object : XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                if (group == null) return
                val thisObj = param?.thisObject
                val mDark = XposedHelpers.getObjectField(thisObj,"mDark") as View



                group!!.alpha = (1-mDark.alpha)


            }
        })

        XposedHelpers.findAndHookMethod(MiuiGlobalActionsDialog,"dismiss",Int::class.java,object : XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                if (group == null) return
                //group!!.clearAnimation()
                //group!!.animatin

                group!!.visibility = View.GONE


            }
        })

    }

    fun rebootToMode(context: Context,mode:String) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        try {
            powerManager.reboot(mode)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun createStateListDrawable(context: Context, state: Boolean,selectedID:Int,defaultID:Int): StateListDrawable {

        val selectedDrawable = context.getDrawable(selectedID)

        val defaultDrawable = context.getDrawable(defaultID)

        val stateListDrawable = StateListDrawable().apply {
            addState(intArrayOf(android.R.attr.state_selected), selectedDrawable)
            addState(intArrayOf(), if (state) selectedDrawable else defaultDrawable)
        }

        return stateListDrawable
    }

    fun menu(mContext: Context, thisObj: Any?):View {
        val res = mContext.resources

        val VolumeUtil = XposedHelpers.findClass("com.android.systemui.miui.volume.VolumeUtil",classLoader)
        val Bootloader = mContext.getDrawable(icBootloader)
        val Recovery = mContext.getDrawable(icRecovery)

        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 120f
            setColor(Color.parseColor("#40FFFFFF"))
        }


        //drawable.setSize(100, 100) // 设置大小，单位为像素
        val padding = dpToPx(res,10.7f).toInt()

        val airPlaneMode = Settings.Global.getInt(mContext.contentResolver,"airplane_mode_on",0)

        val airPlane = Item(createStateListDrawable(mContext,airPlaneMode==1,icAirplaneOn,icAirplaneOff),"静音",airPlaneMode==1)

        val silentMode = XposedHelpers.callStaticMethod(VolumeUtil,"isSilentMode",mContext)  as Boolean
        val silent = Item(createStateListDrawable(mContext,silentMode,icSilentOn,icSilentOff),"静音",true)


        val items: List<Item?> = listOf(
            Item(Recovery,"恢复模式",false),
            Item(Bootloader,"boot",false),
            airPlane,
            silent
        )
        val group = GridView(mContext).apply {
            setPadding(padding,padding,padding,padding)
            background = drawable
            numColumns = 4
            horizontalSpacing = dpToPx(res,7.12f).toInt()
            adapter = GridAdapter(mContext,items, itemClickListener =  object :GridAdapter. OnItemClickListener{
                override fun onItemClick(position: Int,v: View) {
                    when (position){
                        0->{
                            rebootToMode(mContext,"recovery")
                        }
                        1->{
                            rebootToMode(mContext,"bootloader")
                        }
                        2->{
                            v.isSelected = !airPlane.state
                            Settings.Global.putInt(context.contentResolver, Settings.Global.AIRPLANE_MODE_ON, if (!airPlane.state) 1 else 0)
                            context.sendBroadcast(Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED))

                        }
                        3->{
                            v.isSelected = !silentMode
                            XposedHelpers.callStaticMethod(VolumeUtil,"setSilenceMode",mContext,!silentMode)
                        }
                    }
                    Handler(Looper.getMainLooper()).postDelayed({
                        XposedHelpers.callMethod(thisObj,"dismiss",1)
                    }, 100)

                }

            })
        }

        return group
    }

    data class Item(val image:  Drawable?, val text: String,val state : Boolean)

}

class GridAdapter(private val context: Context, private val items: List<Item?>, private val itemClickListener: OnItemClickListener) : BaseAdapter() {

    interface OnItemClickListener {
        fun onItemClick(position: Int,v:View)
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
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
            itemClickListener.onItemClick(position,view)
        }
        return view
    }

    private fun addShowAnimation(view: View) {

        val scaleXAnimator = ObjectAnimator.ofFloat(view, "scaleX", 0.9f, 1f).apply {
            duration = 100  // 动画持续时间
            interpolator = AccelerateDecelerateInterpolator()  // 使用加速减速插值器
        }

        val scaleYAnimator = ObjectAnimator.ofFloat(view, "scaleY", 0.9f, 1f).apply {
            duration = 100  // 动画持续时间
            interpolator = AccelerateDecelerateInterpolator()  // 使用加速减速插值器
        }

        // 创建一个 AnimatorSet 来同时播放多个动画
        val animatorSet = AnimatorSet().apply {
            playTogether(scaleXAnimator, scaleYAnimator)
        }

        // 开始动画
        animatorSet.start()
    }


    fun createStateListDrawable(context: Context, state: Boolean): StateListDrawable {
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

    fun createItemLayout(context: Context, item: Item): FrameLayout {
        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.OVAL
        val size = dpToPx(context.resources,60.6f).toInt()

        drawable.setColor(if(item.state){
            Color.parseColor("#2856FF")
        }else{
            Color.parseColor("#40FFFFFF")
        })

        val layout = FrameLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(size, size).apply {

            }
            background = createStateListDrawable(context,item.state)
            setOnTouchListener { _, event ->
                when (event.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        // 开始按压动画
                        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                            this,
                            PropertyValuesHolder.ofFloat("scaleX", 0.8f),
                            PropertyValuesHolder.ofFloat("scaleY", 0.8f)
                        ).apply {
                            duration = 150
                        }
                        scaleDown.start()
                    }
                    android.view.MotionEvent.ACTION_UP, android.view.MotionEvent.ACTION_CANCEL -> {
                        // 结束按压动画
                        val scaleUp = ObjectAnimator.ofPropertyValuesHolder(
                            this,
                            PropertyValuesHolder.ofFloat("scaleX", 1.0f),
                            PropertyValuesHolder.ofFloat("scaleY", 1.0f)
                        ).apply {
                            duration = 150
                        }
                        scaleUp.start()
                    }
                }
                false
            }
        }

        val imageView = ImageView(context).apply {
            scaleType =  ImageView.ScaleType.CENTER
            setImageDrawable(item.image)
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            //background = drawable

        }

        //recovery.foreground = Recovery


        layout.addView(imageView)




        return layout
    }
}
