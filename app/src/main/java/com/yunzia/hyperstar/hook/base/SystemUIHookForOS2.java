package com.yunzia.hyperstar.hook.base;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import android.content.res.XModuleResources;
import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.yunzia.hyperstar.hook.app.systemui.os2.NavigationBarBackground;
import com.yunzia.hyperstar.hook.app.systemui.os2.QSHeaderView;
import com.yunzia.hyperstar.hook.tool.starLog;

import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class SystemUIHookForOS2 extends BaseHooker {

    private final PluginHookForOS2 initSystemUIPluginHook = new PluginHookForOS2();
    private final QSHeaderView qsHeaderView = new QSHeaderView();


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
//        XposedHelpers.findAndHookMethod("miui.systemui.controlcenter.qs.tileview.QSTileItemIconView", classLoader,
//                "getActiveBackgroundDrawable", "com.android.systemui.plugins.qs.QSTile$State",
//                new XC_MethodHook() {
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) {
//                        Drawable drawable = (Drawable) param.getResult();
//                        if (drawable instanceof GradientDrawable){
//                            //drawable.setColorFilter();
//                            ( (GradientDrawable) drawable).setColor(Color.WHITE);
//                            param.setResult(drawable);
//                        }
//
//                    }
//                }
//        );

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
