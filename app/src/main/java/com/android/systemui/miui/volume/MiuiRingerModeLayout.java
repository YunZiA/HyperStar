//
// Decompiled by Jadx - 675ms
//
package com.android.systemui.miui.volume;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
//import c.a.b.w1.a.b;
//import c.a.b.w1.a.d;
//import c.a.b.w1.a.e;
//import c.a.b.w1.a.f;
//import miuix.view.HapticFeedbackConstants;

public class MiuiRingerModeLayout extends LinearLayout {

    public MiuiRingerModeLayout(Context context) {
        super(context);
    }

    public MiuiRingerModeLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MiuiRingerModeLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MiuiRingerModeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
