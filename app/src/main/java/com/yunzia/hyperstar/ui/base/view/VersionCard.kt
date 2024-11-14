//package com.yunzia.hyperstar.ui.base.view
//
//import android.content.Context
//import android.graphics.Color
//import android.os.Parcel
//import android.os.Parcelable
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.FrameLayout
//import android.widget.ImageView
//import com.yunzia.hyperstar.R
//import top.yukonga.miuix.kmp.theme.darkColorScheme
//import top.yukonga.miuix.kmp.theme.lightColorScheme
//import yunzia.utils.MiuiBlurUtils
//import yunzia.utils.ViewUtils
//
//class VersionCard(context: Context,mode: Int) : FrameLayout(context) {
//
//    private var color :IntArray
//    private lateinit var mRootView: ViewGroup
//    private lateinit var mTextLogoView: ImageView
//
//    init {
//        color = getColor(mode)
//        initView()
//    }
//
//    private fun initView() {
//        mRootView = LayoutInflater.from(context).inflate(R.layout.layout_version_card, this, true) as ViewGroup
//        mTextLogoView = findViewById(R.id.app_text_logo_view)
//        setLogoBlur()
//    }
//
//    private fun setLogoBlur() {
//        if (MiuiBlurUtils.isEnable() && MiuiBlurUtils.isEffectEnable(context)) {
//            mRootView.setBackgroundColor(Color.TRANSPARENT)
////            MiuiBlurUtils.setBackgroundBlur(
////                mRootView,
////                (context.resources.displayMetrics.density * 50.0f + 0.5f).toInt()
////            )
//            //MiuiBlurUtils.setViewBlurMode(mRootView, 0)
//
//            val mixColors = context.resources.getIntArray(R.array.mix_color)
//            mTextLogoView.setBackgroundColor(Color.TRANSPARENT)
//            mTextLogoView.setBackgroundResource(R.drawable.hyperstar2_new)
//
//            //enableTextBlur(mTextLogoView, true, color)
//            Log.d("VersionCard", "start logoBlur: ")
//        } else {
//            mTextLogoView.setBackgroundResource(R.drawable.hyperstar2)
//        }
//    }
//
//    fun setColor(mode:Int){
//
//        color =  if (ViewUtils.isNightMode(context,mode)) resources.getIntArray(R.array.mix_color_night) else resources.getIntArray(R.array.mix_color)
//
//        //setLogoBlur()
//    }
//
//    private fun getColor(mode:Int):IntArray{
//       return  if (ViewUtils.isNightMode(context,mode)) resources.getIntArray(R.array.mix_color_night) else resources.getIntArray(R.array.mix_color)
//
//    }
//
//
//
//    private fun enableTextBlur(view: View, z: Boolean, iArr: IntArray) {
//        if (z) {
//            MiuiBlurUtils.setViewBlurMode(view, 3)
//            for (i in iArr.indices step 2) {
//                if (i + 1 < iArr.size) {
//                    MiuiBlurUtils.addBackgroundBlenderColor(view, iArr[i], iArr[i + 1])
//                }
//            }
//        } else {
//            MiuiBlurUtils.setViewBlurMode(view, 0)
//            MiuiBlurUtils.clearBackgroundBlenderColor(view)
//        }
//    }
//
//
//}