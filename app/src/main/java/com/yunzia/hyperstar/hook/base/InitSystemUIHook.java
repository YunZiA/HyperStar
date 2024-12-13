package com.yunzia.hyperstar.hook.base;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import android.content.res.XModuleResources;

import com.yunzia.hyperstar.R;
import com.yunzia.hyperstar.hook.app.systemui.NavigationBarBackground;
import com.yunzia.hyperstar.hook.app.plugin.QSHeaderView;
import com.yunzia.hyperstar.hook.tool.starLog;

import java.util.Objects;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class InitSystemUIHook extends BaseHooker {

    private final InitSystemUIPluginHook initSystemUIPluginHook;

    public InitSystemUIHook(){
        initSystemUIPluginHook = new InitSystemUIPluginHook();
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
//        resparam.res.setReplacement(
//                systemUI,
//                "drawable",
//                "ic_header_settings",
//                modRes.fwd(R.drawable.ic_header_settings)
//        );
//        resparam.res.setReplacement(
//                systemUI,
//                "drawable",
//                "ic_controls_edit",
//                modRes.fwd(R.drawable.ic_controls_edit)
//        );
        initSystemUIPluginHook.doResources(resparam,modRes);

        if (Objects.equals(resparam.packageName, systemUI)) {

        }


    }

    @Override
    public void doMethodsHook(ClassLoader classLoader) {
        super.doMethodsHook(classLoader);
        doBaseMethods(new NavigationBarBackground());
        doBaseMethods(initSystemUIPluginHook);

        doTestHook();
    }
    //     public void doMethodsHook(ClassLoader classLoader) {
//
//
//
//    }

    private void doTestHook() {

        Class<?> ControlCenterHeaderController  = findClass("com.android.systemui.controlcenter.shade.ControlCenterHeaderController",classLoader);

        XposedBridge.hookAllConstructors(ControlCenterHeaderController,new XC_MethodHook(){
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                starLog.log("ControlCenterHeaderController1111111111");

            }
        });


    }

//    private void doResHook(XC_InitPackageResources.InitPackageResourcesParam resparam){
//
//        resparam.res.setReplacement(resparam.packageName,"drawable","qs");
//
//    }

}
