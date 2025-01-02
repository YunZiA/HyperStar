package com.yunzia.hyperstar;

import static com.yunzia.hyperstar.BuildConfig.APPLICATION_ID;
import static com.yunzia.hyperstar.utils.VersionKt.isOS2;
import static com.yunzia.hyperstar.utils.VersionKt.isOS2Hook;

import android.content.res.XModuleResources;

import com.yunzia.hyperstar.hook.base.InitMiuiHomeHook;
import com.yunzia.hyperstar.hook.base.SystemUIHookForOS1;
import com.yunzia.hyperstar.hook.base.SystemUIHookForOS2;
import com.yunzia.hyperstar.hook.base.BaseHooker;
import com.yunzia.hyperstar.hook.tool.starLog;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class InitHook extends BaseHooker implements IXposedHookLoadPackage, IXposedHookInitPackageResources, IXposedHookZygoteInit {

    private final SystemUIHookForOS2 systemUIHook0S2 = new SystemUIHookForOS2();
    private final SystemUIHookForOS1 systemUIHook0S1 = new SystemUIHookForOS1();
    private final Boolean isOS2Hook = isOS2Hook();

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        super.initZygote(startupParam);
        String hookChannel = isOS2Hook ? "OS2" : "OS1";
        starLog.log("hook channel is " + hookChannel);

    }


    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        super.handleInitPackageResources(resparam);
        XModuleResources modRes = XModuleResources.createInstance(mPath, resparam.res);
        if (isOS2Hook){
            systemUIHook0S2.doResources(resparam,modRes);
        }else {
            systemUIHook0S1.doResources(resparam,modRes);

        }




    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals(APPLICATION_ID)){
            XposedHelpers.findAndHookMethod("com.yunzia.hyperstar.utils.Utils", lpparam.classLoader, "isModuleActive", XC_MethodReplacement.returnConstant(true));
        }

        if (isOS2Hook){
            systemUIHook0S2.doMethods(lpparam);
            new InitMiuiHomeHook().doMethods(lpparam);
        }else {
            systemUIHook0S1.doMethods(lpparam);

        }


    }



}
