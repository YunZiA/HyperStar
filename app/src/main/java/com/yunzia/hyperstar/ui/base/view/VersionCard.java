package com.yunzia.hyperstar.ui.base.view;


import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yunzia.hyperstar.R;

import yunzia.utils.MiuiBlurUtils;
import yunzia.utils.ViewUtils;


//public class VersionCard extends FrameLayout {
//
//    private int modeValue = 0;
//
//    ViewGroup mRootView;
//    private ImageView mTextLogoView;
//
//    //public LogoAnimationController mAnimationController;
//
//
//    public VersionCard(@NonNull Context context) {
//        super(context);
//        initView();
//    }
//
//    public VersionCard(@NonNull Context context, @Nullable AttributeSet attrs) {
//        super(context, attrs);
//        initView();
//    }
//
//    private void initView() {
//        mRootView = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.layout_version_card, this, true);
//
//        mTextLogoView = findViewById(R.id.app_text_logo_view);
//
//        setLogoBlur();
//    }
//
//
//
//    private void setLogoBlur() {
//        if (MiuiBlurUtils.isEnable() && MiuiBlurUtils.isEffectEnable(getContext())) {
//            mRootView.setBackgroundColor(Color.TRANSPARENT);
//            MiuiBlurUtils.setBackgroundBlur(mRootView, (int) ((getResources().getDisplayMetrics().density * 50.0f) + 0.5f));
//            MiuiBlurUtils.setViewBlurMode(mRootView, 0);
//            int[] mix = getResources().getIntArray(R.array.mix_color);
//            mTextLogoView.setBackgroundColor(Color.TRANSPARENT);
//            mTextLogoView.setBackgroundResource(R.drawable.hyperstar2_new);
//            modeValue = ViewUtils.isNightMode(getContext()) ? 18 : 19;
//            enableTextBlur(mTextLogoView, true, mix);
////
//            Log.d("VersionCard", "start logoBlur: ");
//        } else {
//            mTextLogoView.setBackgroundResource(R.drawable.hyperstar2);
//        }
//    }
//
//    private void enableTextBlur(View view, boolean z, int[] iArr) {
//        if (z) {
//            MiuiBlurUtils.setViewBlurMode(view, 3);
//            //MiuiBlurUtils.setMiBackgroundBlendColors(view,iArr,1f);
//            for (int i = 0; i < iArr.length; i++) {
//                MiuiBlurUtils.addBackgroundBlenderColor(view, iArr[i++], iArr[i]);
//            }
//        } else {
//            MiuiBlurUtils.setViewBlurMode(view, 0);
//            MiuiBlurUtils.clearBackgroundBlenderColor(view);
//        }
//    }
//
//}
