package com.yunzia.hyperstar.hook.base;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import android.content.res.Resources;
import android.content.res.XModuleResources;

import com.yunzia.hyperstar.hook.app.systemui.NavigationBarBackground;
import com.yunzia.hyperstar.hook.tool.starLog;

import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class InitSystemUIHook extends BaseHooker {

    private final InitSystemUIPluginHook initSystemUIPluginHook;

    public InitSystemUIHook(){
        initSystemUIPluginHook = new InitSystemUIPluginHook();
    }

    @Override
    public void getLocalRes(Resources res) {
        super.getLocalRes(res);
        initSystemUIPluginHook.getLocalRes(res);
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
    public void doRes(XC_InitPackageResources.InitPackageResourcesParam resparam) {
        super.doRes(resparam);
        if (resparam.packageName.equals("com.android.systemui")){

            //doResHook(resparam);

        }
    }

    @Override
    public void doResources(XC_InitPackageResources.InitPackageResourcesParam resparam, XModuleResources modRes) {
        super.doResources(resparam, modRes);
        initSystemUIPluginHook.doResources(resparam,modRes);

    }

    @Override
    public void doMethodsHook(ClassLoader classLoader) {
        super.doMethodsHook(classLoader);
        doBaseMethods(initSystemUIPluginHook);
        doBaseMethods(new NavigationBarBackground());

        initSystemUIPluginHook.doMethods(classLoader);

        doTestHook();
    }
    //     public void doMethodsHook(ClassLoader classLoader) {
//
//
//
//    }

    private void doTestHook() {





    }

//    private void doResHook(XC_InitPackageResources.InitPackageResourcesParam resparam){
//
//        resparam.res.setReplacement(resparam.packageName,"drawable","qs");
//
//    }

}
