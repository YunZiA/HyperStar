package com.yunzia.hyperstar;

import static com.yunzia.hyperstar.BuildConfig.APPLICATION_ID;

import android.content.res.Resources;
import android.content.res.XModuleResources;

import com.yunzia.hyperstar.hook.base.InitMiuiHomeHook;
import com.yunzia.hyperstar.hook.base.InitSystemUIHook;
import com.yunzia.hyperstar.hook.base.BaseHooker;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import yunzia.utils.SystemProperties;

public class InitHook extends BaseHooker implements IXposedHookLoadPackage, IXposedHookInitPackageResources, IXposedHookZygoteInit {

    private final InitSystemUIHook systemUIHook;
    private final boolean errVersion ;

    public InitHook() {

        errVersion = SystemProperties.getInt("ro.mi.os.version.code",1) != 2;
        systemUIHook = new InitSystemUIHook();
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        super.initZygote(startupParam);
        if (errVersion) return;
        Resources res = XModuleResources.createInstance(startupParam.modulePath, null);
        systemUIHook.getLocalRes(res);

    }


    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        super.handleInitPackageResources(resparam);
        if (errVersion) return;
        systemUIHook.doRes(resparam);
        if (!resparam.packageName.equals("miui.systemui.plugin"))
            return;
        //starLog.log("替换资源");

        XModuleResources modRes = XModuleResources.createInstance(mPath, resparam.res);
        resparam.res.setReplacement("miui.systemui.plugin", "drawable", "ic_header_settings", modRes.fwd(R.drawable.ic_header_settings));
        resparam.res.setReplacement("miui.systemui.plugin", "drawable", "ic_controls_edit", modRes.fwd(R.drawable.ic_controls_edit));

        systemUIHook.doResources(resparam,modRes);


    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (errVersion) return;
        if (lpparam.packageName.equals(APPLICATION_ID)){
            XposedHelpers.findAndHookMethod(APPLICATION_ID+".MainActivity", lpparam.classLoader, "isModuleActive", XC_MethodReplacement.returnConstant(true));
        }

        systemUIHook.doMethods(lpparam);
        new InitMiuiHomeHook().doMethods(lpparam);

    }



}
