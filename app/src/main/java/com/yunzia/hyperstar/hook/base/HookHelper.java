package com.yunzia.hyperstar.hook.base;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

import com.yunzia.hyperstar.hook.tool.starLog;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public abstract class HookHelper {


    public interface ArrayChange{
        void change(int[] array);
    }


    public void setColorField(Object context, String fieldName, String color){
        XposedHelpers.setIntField(context,fieldName, Color.parseColor(color));
    }
    public int getColor(Resources res, String name, String defPackage){
        int id = res.getIdentifier(name,"color",defPackage);
        return res.getColor(id,res.newTheme());
    }

    public int getColor(Resources res, String name, String defPackage,String defColor){
        try{
            int id = res.getIdentifier(name,"color",defPackage);
            return res.getColor(id,res.newTheme());

        } catch (Resources.NotFoundException e) {
            starLog.logE("color "+name+" is not found!");
            return Color.parseColor(defColor);
        }

    }

    public float getDimension(Resources res, String name, String defPackage){
        int id = res.getIdentifier(name,"dimen",defPackage);
        return res.getDimension(id);

    }
    public float getDimensionPixelOffset(Resources res, String name, String defPackage){
        int id = res.getIdentifier(name,"dimen",defPackage);
        return res.getDimensionPixelOffset(id);

    }
    public float getDimensionPixelSize(Resources res, String name, String defPackage){
        int id = res.getIdentifier(name,"dimen",defPackage);
        return res.getDimensionPixelSize(id);

    }

    public Class<?> findClass( String className,ClassLoader classLoader){

        Class<?> cc = XposedHelpers.findClassIfExists(className,classLoader);
        if (cc == null){
            starLog.logE(className+" is not find");
        }
        return cc;
    }

    public void hookAllMethods (ClassLoader classLoader,
                                String className,
                                String methodName,
                                MethodHook methodHook){
        Class<?> hookClass= XposedHelpers.findClassIfExists(className,classLoader);
        if (hookClass == null){
            starLog.logE(className+" is not find");
            return;
        }
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

    public void hookAllMethods (Class<?> hookClass,
                                String methodName,
                                MethodHook methodHook){
        if (hookClass == null){
            starLog.logE(methodName+"'s class is null");
            return;
        }
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



    public interface MethodHook{
        void before(XC_MethodHook.MethodHookParam param);
        void after(XC_MethodHook.MethodHookParam param);
    }

}
