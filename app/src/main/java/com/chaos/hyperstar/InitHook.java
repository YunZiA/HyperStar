package com.chaos.hyperstar;

import android.content.res.Resources;
import android.content.res.XModuleResources;

import com.chaos.hyperstar.hook.app.plugin.QsMediaCoverBackground;
import com.chaos.hyperstar.hook.base.InitSystemUIHook;
import com.chaos.hyperstar.hook.base.BaseHooker;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import com.chaos.hyperstar.hook.tool.starLog;
import com.chaos.hyperstar.utils.XSPUtils;

public class InitHook extends BaseHooker implements IXposedHookLoadPackage, IXposedHookInitPackageResources, IXposedHookZygoteInit {

    private InitSystemUIHook systemUIHook;

    public InitHook() {
        systemUIHook = new InitSystemUIHook();
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals(BuildConfig.APPLICATION_ID)){
            XposedHelpers.findAndHookMethod(BuildConfig.APPLICATION_ID+".MainActivity", lpparam.classLoader, "isModuleActive", XC_MethodReplacement.returnConstant(true));
        }
        systemUIHook.doMethods(lpparam);

    }


    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        systemUIHook.doRes(resparam);
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        Resources res = XModuleResources.createInstance(startupParam.modulePath, null);
        systemUIHook.getLocalRes(res);
        //new QsMediaCoverBackground().getLocalRes(res);

    }


}
