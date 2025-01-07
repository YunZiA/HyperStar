package com.yunzia.hyperstar.hook.init;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import android.content.res.XModuleResources;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.yunzia.hyperstar.hook.app.systemui.os2.NavigationBarBackground;
import com.yunzia.hyperstar.hook.app.systemui.os2.QSHeaderView;
import com.yunzia.hyperstar.hook.base.Init;
import com.yunzia.hyperstar.hook.base.InitHooker;

import de.robv.android.xposed.callbacks.XC_InitPackageResources;

@Init(packageName = "com.android.systemui")
public class SystemUIHookForOS2 extends InitHooker {

    private final PluginHookForOS2 pluginHookForOS2 = new PluginHookForOS2();
    private final QSHeaderView qsHeaderView = new QSHeaderView();

    @Override
    public void initResources() {
        initResource(pluginHookForOS2);
        if (!resparam.packageName.equals(mPackageName)) return;
        initResource(qsHeaderView);

    }

    @Override
    public void initHook() {
        initHooker(new NavigationBarBackground());
        initHooker(qsHeaderView);
        initHooker(pluginHookForOS2);

        //doTestHook();
    }


    private void doTestHook() {


    }

    public static void applyViewShadowForMediaAlbum(float f, float f2, int i, View view) {
        try {
            Class<?> cls = Class.forName("android.view.View");
            Class<?> cls2 = Float.TYPE;
            cls.getMethod("setMiShadow", Integer.TYPE, cls2, cls2, cls2, cls2, Boolean.TYPE).invoke(view, Integer.valueOf(Color.argb(i, 0, 0, 0)), Float.valueOf(0.0f), Float.valueOf(f), Float.valueOf(f2), Float.valueOf(1.0f), Boolean.FALSE);
        } catch (Exception unused) {
            Log.d("NotificationUtil", "applyViewShadowForMediaAlbum setMiShadow Method not found!");
        }
    }



}
