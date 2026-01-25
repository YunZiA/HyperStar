//package com.yunzia.hyperstar;
//
//import static com.yunzia.hyperstar.BuildConfig.APPLICATION_ID;
//import static com.yunzia.hyperstar.utils.VersionKt.isHookChannel;
//
//import android.content.res.XModuleResources;
//
//import com.yunzia.hyperstar.hook.init.InitMiuiHomeHook;
//import com.yunzia.hyperstar.hook.init.SystemUIHookForOS1;
//import com.yunzia.hyperstar.hook.init.SystemUIHookForOS2;
//import com.yunzia.hyperstar.hook.core.Log;
//
//import de.robv.android.xposed.IXposedHookInitPackageResources;
//import de.robv.android.xposed.IXposedHookLoadPackage;
//import de.robv.android.xposed.IXposedHookZygoteInit;
//import de.robv.android.xposed.XC_MethodReplacement;
//import de.robv.android.xposed.XposedHelpers;
//import de.robv.android.xposed.callbacks.XC_InitPackageResources;
//import de.robv.android.xposed.callbacks.XC_LoadPackage;
//import xyz.xfqlittlefan.notdeveloper.xposed.NotDeveloperBaseHook;
//
//
//
//public class inits implements IXposedHookLoadPackage, IXposedHookInitPackageResources, IXposedHookZygoteInit {
//
//    private final SystemUIHookForOS2 systemUIHook0S2 = new SystemUIHookForOS2();
//    private final SystemUIHookForOS1 systemUIHook0S1 = new SystemUIHookForOS1();
//    private String mPath;
//    private final int isHookChannel = isHookChannel()+1;
//
//    @Override
//    public void initZygote(StartupParam startupParam) throws Throwable {
//        mPath=startupParam.modulePath;
//        log("HookChannel is currently configured for OS" + isHookChannel);
//
//    }
//
//
//    @Override
//    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
//        XModuleResources modRes = XModuleResources.createInstance(mPath, resparam.res);
//        switch (isHookChannel){
//            case 1:
//                systemUIHook0S1.initResources(resparam,modRes);
//                break;
//            case 2:
//                systemUIHook0S2.initResources(resparam,modRes);
//                break;
//            default:
//                logE("Resource initialization failed! Because the HookChannel is OS"+isHookChannel);
//        }
//
//
//    }
//
//    @Override
//    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
//        if (lpparam.packageName.equals(APPLICATION_ID)){
//
//            XposedHelpers.findAndHookMethod("com.yunzia.hyperstar.utils.Helper", lpparam.classLoader, "isModuleActive", XC_MethodReplacement.returnConstant(true));
//        }
//
//        new NotDeveloperBaseHook().init(lpparam);
//
//        switch (isHookChannel) {
//            case 1:
//                systemUIHook0S1.init(lpparam);
//                break;
//            case 2:
//                systemUIHook0S2.init(lpparam);
//                new InitMiuiHomeHook().init(lpparam);
//                break;
//            default:
//                logE("Hook initialization failed! Because the HookChannel is OS"+isHookChannel);
//
//        }
//
//
//    }
//
//
//
//}
