package com.yunzia.hyperstar.hook.base;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import android.content.res.XModuleResources;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.yunzia.hyperstar.R;
import com.yunzia.hyperstar.hook.app.systemui.NavigationBarBackground;
import com.yunzia.hyperstar.hook.app.systemui.QSHeaderView;
import com.yunzia.hyperstar.hook.tool.starLog;

import java.util.Objects;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class InitSystemUIHook extends BaseHooker {

    private final InitSystemUIPluginHook initSystemUIPluginHook;
    private final QSHeaderView qsHeaderView;

    public InitSystemUIHook(){

        initSystemUIPluginHook = new InitSystemUIPluginHook();
        qsHeaderView = new QSHeaderView();
    }

    @Override
    public void doMethods(XC_LoadPackage.LoadPackageParam lpparam) {
        super.doMethods(lpparam);
        if (lpparam.packageName.equals("com.android.systemui")){

            starLog.log("Loaded app: "+lpparam.packageName);
            doMethodsHook(lpparam.classLoader);

        }
    }


    @Override
    public void doResources(XC_InitPackageResources.InitPackageResourcesParam resparam, XModuleResources modRes) {
        super.doResources(resparam, modRes);
        doResources(initSystemUIPluginHook);
        //initSystemUIPluginHook.doResources(resparam,modRes);
        if (!resparam.packageName.equals(systemUI)) return;
        doResources(qsHeaderView);




    }

    @Override
    public void doMethodsHook(ClassLoader classLoader) {
        super.doMethodsHook(classLoader);
        doBaseMethods(new NavigationBarBackground());
        doBaseMethods(qsHeaderView);
        doBaseMethods(initSystemUIPluginHook);

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



//    private void doResHook(XC_InitPackageResources.InitPackageResourcesParam resparam){
//
//        resparam.res.setReplacement(resparam.packageName,"drawable","qs");
//
//    }

}
