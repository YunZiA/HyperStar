package com.yunzia.hyperstar.hook.base;

import android.content.res.XModuleResources;

import com.yunzia.hyperstar.hook.util.starLog;

import java.util.Objects;

import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public abstract class InitHooker extends HookerHelper  {

    public String plugin = "miui.systemui.plugin";
    public Init init = getClass().getAnnotation(Init.class);
    public XC_InitPackageResources.InitPackageResourcesParam resparam;
    public XC_LoadPackage.LoadPackageParam lpparam;
    public XModuleResources modRes;
    public ClassLoader classLoader;
    public ClassLoader secClassLoader;

    public final String mPackageName = init != null ? init.packageName() : "null";

    public InitHooker(){

    }

    public void initHook(XC_LoadPackage.LoadPackageParam lpparam){
        this.lpparam = lpparam;
        if (Objects.equals(lpparam.packageName, init.packageName())){
            starLog.log("Loaded app: " + lpparam.packageName);
            this.classLoader = lpparam.classLoader;

            initHook();

        }
    }

    public void initHook(ClassLoader classLoader){
        this.classLoader = classLoader;
        initHook();

    }


    public void initResources(XC_InitPackageResources.InitPackageResourcesParam resparam, XModuleResources modRes){
        this.resparam = resparam;
        this.modRes = modRes;
        initResources();
    }
    public void initResources(){}


    public void initResource(Hooker hooker){

        hooker.initResources(resparam,modRes);
    }
    public void initResource(InitHooker hooker){

        hooker.initResources(resparam,modRes);
    }

    public void initHook(){}


    public void initHooker(Hooker hooker){
        try{
            hooker.initHook(classLoader);
        } catch (Exception e) {
            starLog.log(e.getMessage());
        }
    }
    public void initHooker(InitHooker initHooker){
        try{
            initHooker.initHook(classLoader);
        } catch (Exception e) {
            starLog.log(e.getMessage());
        }
    }
    public void initSecHook(ClassLoader secClassLoader){
        this.secClassLoader = secClassLoader;
    }

    public void initSecHooker(Hooker hooker)  {
        try{
            hooker.initHook(secClassLoader);
        } catch (Exception e) {
            starLog.log(e.getMessage());
        }

    }

}
