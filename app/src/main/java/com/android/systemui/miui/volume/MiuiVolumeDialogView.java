package com.android.systemui.miui.volume;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.android.systemui.miui.volume.widget.ExpandCollapseLinearLayout;

public class MiuiVolumeDialogView extends ExpandCollapseLinearLayout {

    public int collapsingTransition;
    public int expandingTransition;
    public MiuiVolumeDialogView(Context context) {
        super(context);
    }

    public MiuiVolumeDialogView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MiuiVolumeDialogView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MiuiVolumeDialogView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
