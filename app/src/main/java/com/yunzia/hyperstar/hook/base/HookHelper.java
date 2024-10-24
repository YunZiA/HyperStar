package com.yunzia.hyperstar.hook.base;

import android.content.Context;
import android.graphics.Color;

import de.robv.android.xposed.XposedHelpers;

public class HookHelper {

    public static void setColorField(Context context,String fieldName, String color){
        XposedHelpers.setIntField(context,fieldName, Color.parseColor(color));
    }

}
