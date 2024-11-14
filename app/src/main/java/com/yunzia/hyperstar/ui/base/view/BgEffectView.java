package com.yunzia.hyperstar.ui.base.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.yunzia.hyperstar.R;

@SuppressLint("ViewConstructor")
public class BgEffectView extends LinearLayout {

    private View mBgEffectView;
    private BgEffectPainter mBgEffectPainter;
    private final float startTime = (float) System.nanoTime();
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private int colorMode = 1;

    Runnable runnableBgEffect = new Runnable() {
        @Override
        public void run() {
            mBgEffectPainter.setAnimTime(((((float) System.nanoTime()) - startTime) / 1.0E9f) % 62.831852f);
            mBgEffectPainter.setResolution(new float[]{mBgEffectView.getWidth(), mBgEffectView.getHeight()});
            mBgEffectPainter.updateMaterials();
            mBgEffectView.setRenderEffect(mBgEffectPainter.getRenderEffect());
            mHandler.postDelayed(runnableBgEffect, 16L);
        }
    };


    public BgEffectView(Context context, int mode) {
        super(context);
        colorMode = mode;
        BgEffect(context);
    }


    public void BgEffect(Context context) {

        mBgEffectView = LayoutInflater.from(context).inflate(R.layout.layout_effect_bg, this, true);


        mBgEffectView.post(() -> {
            if (context != null) {

                Context appContext = context.getApplicationContext();
                mBgEffectPainter = new BgEffectPainter(appContext);
                mBgEffectPainter.showRuntimeShader(appContext, mBgEffectView, colorMode);

                mHandler.post(runnableBgEffect);
            }
        });
    }

    public void  updateMode(int mode){
        if (mode != colorMode){
            colorMode = mode;
            mBgEffectPainter.updateMode(mode);
        }
    }

}

