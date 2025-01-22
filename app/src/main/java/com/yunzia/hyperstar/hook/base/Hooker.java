package com.yunzia.hyperstar.hook.base;

import android.content.res.XModuleResources;
import android.graphics.Color;

import de.robv.android.xposed.callbacks.XC_InitPackageResources;


public abstract class Hooker extends HookerHelper  {

    public String plugin = "miui.systemui.plugin";
    public String systemUI = "com.android.systemui";

    public XC_InitPackageResources.InitPackageResourcesParam resparam;
    public XModuleResources modRes;
    public ClassLoader classLoader;


    public Hooker(){

    }

    public void initResources(XC_InitPackageResources.InitPackageResourcesParam resparam, XModuleResources modRes){
        this.resparam = resparam;
        this.modRes = modRes;
    }

    public void initHook(ClassLoader classLoader){
        this.classLoader = classLoader;
    }

    public void ReplaceColor(String color,String colorValue){
        resparam.res.setReplacement(plugin, "color", color, Color.parseColor(colorValue));
    }

    public void ReplaceIntArray(String array, ArrayChange arrayChange){
        int arrayId = resparam.res.getIdentifier(array,"array",plugin);
        int[] ay = resparam.res.getIntArray(arrayId);
        arrayChange.change(ay);
        resparam.res.setReplacement(plugin, "array", array, ay);

    }

    public interface ArrayChange{
        void change(int[] array);
    }




}
