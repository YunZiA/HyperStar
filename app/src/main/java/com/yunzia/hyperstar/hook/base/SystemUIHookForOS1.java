package com.yunzia.hyperstar.hook.base;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import android.content.res.XModuleResources;

import com.yunzia.hyperstar.hook.app.systemui.os1.NavigationBarBackground;
import com.yunzia.hyperstar.hook.tool.starLog;

import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class SystemUIHookForOS1 extends BaseHooker {

    private final PluginHookForOS1 pluginHookForOS1 = new PluginHookForOS1();

    public SystemUIHookForOS1(){
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
        pluginHookForOS1.doResources(resparam,modRes);
        if (!resparam.packageName.equals(systemUI)) return;
    }

    @Override
    public void doMethodsHook(ClassLoader classLoader) {
        super.doMethodsHook(classLoader);
        doBaseMethods(pluginHookForOS1);
        doBaseMethods(new NavigationBarBackground());

        doTestHook();
    }


    private void doTestHook() {



    }


}
