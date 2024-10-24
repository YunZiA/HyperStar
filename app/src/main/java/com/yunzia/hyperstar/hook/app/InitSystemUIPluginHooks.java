package com.yunzia.hyperstar.hook.app;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

//import com.github.kyuubiran.ezxhelper.EzXHelper;
//import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper;

import com.yunzia.hyperstar.hook.base.BaseHooker;
import com.yunzia.hyperstar.hook.tool.starLog;
import com.github.kyuubiran.ezxhelper.ObjectHelper;

import java.lang.reflect.Method;

import com.yunzia.hyperstar.utils.XSPUtils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
//import miui.systemui.controlcenter.media.MediaPlayerMetaData;

public class InitSystemUIPluginHooks extends BaseHooker {
    public boolean ishHooked = false;

    public Context mContext;

    @Override
    public void doMethods(ClassLoader classLoader) {
        super.doMethods(classLoader);
        startSystemUIPluginHook();
    }


    private void startSystemUIPluginHook(){

        Class<?> CommonUtils = XposedHelpers.findClass("miui.systemui.util.CommonUtils", classLoader);


        hookAllMethods(classLoader, "com.android.systemui.shared.plugins.PluginInstance$Factory", "create",new MethodHook(){

            @Override
            public void before(XC_MethodHook.MethodHookParam param) {
                if (mContext != null){
                    mContext = (Context) param.args[0];

                }

            }

            @Override
            public void after(XC_MethodHook.MethodHookParam param) {
            }
        });

        hookAllMethods(classLoader,"com.android.systemui.shared.plugins.PluginInstance$Factory$$ExternalSyntheticLambda0","get",new MethodHook(){

            @Override
            public void before(XC_MethodHook.MethodHookParam param) {

            }

            @Override
            public void after(XC_MethodHook.MethodHookParam param) {
                Object pathClassLoader = param.getResult();
                if (!ishHooked) {
                    ClassLoader pluginLoader = (ClassLoader) pathClassLoader;
                    starLog.log("Loaded pluginClassLoader: " + pluginLoader);
                    doHook(pluginLoader);
                    ishHooked = true;
                }
            }


        });


    }

    void  cc (){

    }

    public void doHook(ClassLoader classLoader){
        if (XSPUtils.INSTANCE.getBoolean("test",false)){
            findAndHookMethod("miui.systemui.controlcenter.panel.main.qs.QSCardsController",
                    classLoader,
                    "getPriority", new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            param.setResult(150);
                        }
                    });
        }

        Class<?> MediaPlayerMetaData = findClass("miui.systemui.controlcenter.media.MediaPlayerMetaData",classLoader);
        findAndHookMethod("miui.systemui.controlcenter.panel.main.media.MediaPlayerController$MediaPlayerViewHolder",
                classLoader,
                "updateMetaData", MediaPlayerMetaData, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Object mediaPlayerMetaData = param.args[0];
                        starLog.log("updateMetaData:hook");
                        if (mediaPlayerMetaData != null){
                            starLog.log("mediaPlayerMetaData:is get!!!");
                            Method getArt = MediaPlayerMetaData.getDeclaredMethod("getArt");
                            Bitmap Art = (Bitmap) getArt.invoke(mediaPlayerMetaData);
                            //Field item = mViewHolder.getDeclaredField("itemView");

                            ObjectHelper objectHelper = (ObjectHelper) param.thisObject;
                            //objectHelper.getClass().getDeclaredField()

                            //Class<?> viewHolderClass = viewHolderInstance.getClass();

// 获取 itemView 字段
                            //Field itemViewField = viewHolderClass.getDeclaredField("itemView");
                            //itemViewField.setAccessible(true); // 如果 itemView 是私有的，则需要这一步
                            //View itemView = (View) itemViewField.get(viewHolderInstance);
                            //starLog.log("ViewHolder instance obtained: " + itemView);
                            Drawable artDrawable = new BitmapDrawable(Art);
                            //itemView.setBackground(artDrawable);
                                // 使用 viewHolder 进行进一步的操作

                            //thisObject = (Class<?>) param.thisObject;

                        }

                    }
                });


    }
}


