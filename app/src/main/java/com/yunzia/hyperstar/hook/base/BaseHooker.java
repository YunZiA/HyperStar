package com.yunzia.hyperstar.hook.base;

import android.content.res.Resources;
import android.content.res.XModuleResources;
import android.graphics.Color;
import android.util.Log;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public abstract class BaseHooker {

    public String plugin = "miui.systemui.plugin";
    public String systemUI = "com.android.systemui";

    public BaseHooker(){

    }

    public interface ArrayChange{
        void change(int[] array);
    }

    //private XModuleResources mXModuleResources;
    //public XC_LoadPackage.LoadPackageParam lpparam;

    public XC_InitPackageResources.InitPackageResourcesParam resparam;
    public XModuleResources modRes;
    public ClassLoader classLoader;
    public ClassLoader secClassLoader;

    //public String modulePath;

    public void getLocalRes(Resources res){};
    public void doResources(XC_InitPackageResources.InitPackageResourcesParam resparam,XModuleResources modRes){
        this.resparam = resparam;
        this.modRes = modRes;
    }
    public void doResources(BaseHooker baseHooker){

        baseHooker.doResources(resparam,modRes);
    };
    public void doMethods(ClassLoader classLoader){
        this.classLoader = classLoader;
    };
    public void doMethodsHook(ClassLoader classLoader){
        this.classLoader = classLoader;
    }
    public void doBaseMethods(BaseHooker baseHooker){

        baseHooker.doMethods(classLoader);
    };

    public void doSecMethods(BaseHooker baseHooker){

        baseHooker.doMethods(secClassLoader);
    };

    public void doHook(ClassLoader classLoader){
        Log.d("ggc", "doHook: super");
        this.secClassLoader = classLoader;
    };

    public void doMethods(XC_LoadPackage.LoadPackageParam lpparam){
    };
    public void doRes(XC_InitPackageResources.InitPackageResourcesParam resparam){}

    public void ReplaceColor(String color,String colorValue){
        resparam.res.setReplacement(plugin, "color", color, Color.parseColor(colorValue));
    }
    public void ReplaceIntArray(String array,ArrayChange arrayChange){
        int arrayId = resparam.res.getIdentifier(array,"array",plugin);
        int[] ay = resparam.res.getIntArray(arrayId);
        arrayChange.change(ay);
        resparam.res.setReplacement(plugin, "array", array, ay);

    }
    public void setColorField(Object context, String fieldName, String color){
        XposedHelpers.setIntField(context,fieldName, Color.parseColor(color));
    }
    //public ProviderUtils mProviderUtils;
    public String mPath;



    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) throws Throwable {
        mPath=startupParam.modulePath;
    }


    public boolean classIsExist(XC_LoadPackage.LoadPackageParam lpparam, String className){
        Class<?> hookClass= XposedHelpers.findClass(className,lpparam.classLoader);
        return hookClass != null;
    }

    public void hookAllMethods (ClassLoader classLoader,
                                String className,
                                String methodName,
                                MethodHook methodHook){
        Class<?> hookClass= XposedHelpers.findClass(className,classLoader);
        XposedBridge.hookAllMethods(hookClass, methodName, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                methodHook.before(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                methodHook.after(param);
            }
        });


    }



    protected void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        //setmXModuleResources(XModuleResources.createInstance(mPath, resparam.res));
    }


    public void setmXModuleResources(XModuleResources mXModuleResources) {
        //this.mXModuleResources = mXModuleResources;
    }


    public interface MethodHook{
        void before(XC_MethodHook.MethodHookParam param);
        void after(XC_MethodHook.MethodHookParam param);
    }

}
