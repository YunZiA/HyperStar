package com.yunzia.hyperstar.hook.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yunzia.hyperstar.R;

public class QSHeaderButton extends LinearLayout {
    public QSHeaderButton(Context context) {
        super(context);
        init(context);
    }

    public QSHeaderButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public QSHeaderButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public QSHeaderButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @SuppressLint("ResourceType")
    private void init(Context context) {
        setOrientation(LinearLayout.HORIZONTAL);
        // 创建并添加编辑按钮
        Button edit = creatHeaderButtom(context,R.drawable.ic_controls_edit);
        // 创建并添加设置按钮
        Button settings = creatHeaderButtom(context,R.drawable.ic_search_icon);

        //int buttonSize = getResources().getDimensionPixelSize(R.dimen.header_text_size) * 14 / 10;

        //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(buttonSize, buttonSize);
        addView(settings);

        //marginStart = getResources().getDimensionPixelSize(R.dimen.header_privacy_container_margin_start);

        //params.setMargins(changeSpace(marginStart), 0, 0, 0);
        addView(edit);

        edit.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                v.performHapticFeedback(0);
            }
        });

//        edit.setOnLongClickListener(new OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                Log.d("ggc", "ChaosHeaderView: edit.setOnLongClickListener");
//                v.performHapticFeedback(0);
//                return openEditView();
//            }
//        });

//        settings.setOnClickListener(new NoDoubleClickListener() {
//            @Override
//            public void onNoDoubleClick(View v) {
//                Log.d("ggc", "ChaosHeaderView: settings.setOnClickListener");
//                v.performHapticFeedback(0);
//                goSettings();
//
//            }
//        });
//
//        settings.setOnLongClickListener(new OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                Log.d("ggc", "ChaosHeaderView: settings.setOnLongClickListener");
//                v.performHapticFeedback(0);
//                collapseStatusBar(mContext);
//                return goSettings();
//            }
//        });


    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        Toast.makeText(changedView.getContext(),"改变",Toast.LENGTH_SHORT).show();
    }



    private boolean goSettings(Context context){
        Intent intent = new Intent();
        intent.setClassName("com.android.settings","com.android.settings.MainSettings");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        collapseStatusBar(context);
        return true;
    }

    private void collapseStatusBar(Context context) {
        try {
            @SuppressLint("WrongConstant") Object systemService = context.getSystemService("statusbar");
            systemService.getClass().getMethod("collapsePanels", new Class[0]).invoke(systemService, new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Button creatHeaderButtom(Context context,int res){
        Button headerButtom = new Button(context);
        headerButtom.setBackgroundResource(res);

        return headerButtom;
    }

    public abstract static class NoDoubleClickListener implements View.OnClickListener {

        public static final int MIN_CLICK_DELAY_TIME = 1000;
        private long lastClickTime = 0;
        public abstract void onNoDoubleClick(View v);

        @Override
        public void onClick(View v) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                lastClickTime = currentTime;
                onNoDoubleClick(v);
            }
        }
    }
}


