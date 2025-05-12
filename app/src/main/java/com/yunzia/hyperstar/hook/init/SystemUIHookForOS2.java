package com.yunzia.hyperstar.hook.init;

import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.yunzia.hyperstar.hook.app.systemui.os2.AddCatPaw;
import com.yunzia.hyperstar.hook.app.systemui.os2.LowDeviceBackgroundColor;
import com.yunzia.hyperstar.hook.app.systemui.os2.NavigationBarBackground;
import com.yunzia.hyperstar.hook.app.systemui.os2.NotificationForLm;
import com.yunzia.hyperstar.hook.app.systemui.os2.QSHeaderView;
import com.yunzia.hyperstar.hook.app.systemui.os2.Test;
import com.yunzia.hyperstar.hook.base.Init;
import com.yunzia.hyperstar.hook.base.InitHooker;

@Init(packageName = "com.android.systemui")
public class SystemUIHookForOS2 extends InitHooker {

    private final PluginHookForOS2 pluginHookForOS2 = new PluginHookForOS2();
    private final QSHeaderView qsHeaderView = new QSHeaderView();
    private final AddCatPaw addCatPaw = new AddCatPaw();
    private final Test test = new Test();

    @Override
    public void initResources() {
        if (!resparam.packageName.equals(mPackageName)){
            initResource(pluginHookForOS2);
            return;
        }
        initResource(qsHeaderView);
       // modRes.fwd(R.color.black)
        resparam.res.setReplacement(mPackageName,"color","shade_solid_background_color","#00000000");

        //initResource(test);
        //initResource(addCatPaw);

    }

    @Override
    public void initHook() {
        initHooker(new NavigationBarBackground());
        initHooker(new LowDeviceBackgroundColor());
        initHooker(new NotificationForLm());
        initHooker(qsHeaderView);
        initHooker(pluginHookForOS2);
        //initHooker(test);

        //initHooker(addCatPaw);
//
//        doTestHook();
    }


    private void doTestHook() {

//    "isFlipDevice"



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
