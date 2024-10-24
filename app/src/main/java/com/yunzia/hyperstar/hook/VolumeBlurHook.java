package com.yunzia.hyperstar.hook;//package com.chaos.com.yunzia.hyperstar.hook;
//
//import android.util.Log;
//
//
//import de.robv.android.xposed.XC_MethodHook;
//import de.robv.android.xposed.XposedHelpers;
//import de.robv.android.xposed.callbacks.XC_InitPackageResources;
//
//public class VolumeBlurHook extends BaseSystemUIHook{
//    private String xmlValue="volume_blur";
//
//    @Override
//    public void isGetContextAndLoader() {
//        super.isGetContextAndLoader();
//
//    }
//
//    @Override
//    public void isGetHookResources(XC_InitPackageResources.InitPackageResourcesParam resparam) {
//        super.isGetHookResources(resparam);
//        setVBlurSupported(resparam);
//    }
//
//    private void setVBlurSupported(XC_InitPackageResources.InitPackageResourcesParam resparam){
//        setVolumeBlurTrue();
//
//
//    }
//
//
//
//    public void setVolumeBlurTrue(){
//        Log.e("mario", "setVolumeBlurTrue: ");
//
//        Class<?> CommonUtils= XposedHelpers.findClass("com.android.systemui.miui.volume.Util",pluginLoader);
//        if (CommonUtils!=null){
//            XposedHelpers.findAndHookMethod(CommonUtils, "isSupportBlurS", new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    super.afterHookedMethod(param);
//                    param.setResult(true);
//                }
//            });
//        }
//    }
//
//}
