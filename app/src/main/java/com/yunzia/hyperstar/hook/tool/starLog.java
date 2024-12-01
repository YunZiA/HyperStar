package com.yunzia.hyperstar.hook.tool;

import de.robv.android.xposed.XposedBridge;

public class starLog {
    private static final String HOOK_TAG = "[HyperStar2.0]";
    public static void log(String msg) {
        XposedBridge.log(HOOK_TAG+"{ " + msg + " }");
    }

    public static void logI(String msg) {
        XposedBridge.log(HOOK_TAG+" [I]: " + msg);
    }

    public static void logI(String tagOpkg, String msg) {
        XposedBridge.log(HOOK_TAG+" [I][" + tagOpkg + "]: " + msg);
    }

    public static void logI(String tag, String pkg, String msg) {
        XposedBridge.log(HOOK_TAG+" [I][" + pkg + "][" + tag + "]: " + msg);
    }

    public static void logW(String msg) {
        XposedBridge.log(HOOK_TAG+" [W]: " + msg);
    }

    public static void logW(String tag, String pkg, String msg) {
        XposedBridge.log(HOOK_TAG+" [W][" + pkg + "][" + tag + "]: " + msg);
    }

    public static void logW(String tag, String pkg, Throwable log) {
        XposedBridge.log(HOOK_TAG+" [W][" + pkg + "][" + tag + "]: " + log);
    }

    public static void logW(String tag, String pkg, String msg, Exception exp) {
        XposedBridge.log(HOOK_TAG+" [W][" + pkg + "][" + tag + "]: " + msg + ", by: " + exp);
    }

    public static void logW(String tag, String pkg, String msg, Throwable log) {
        XposedBridge.log(HOOK_TAG+" [W][" + pkg + "][" + tag + "]: " + msg + ", by: " + log);
    }

    public static void logW(String tag, String msg) {
        XposedBridge.log(HOOK_TAG+" [W][" + tag + "]: " + msg);
    }

    public static void logW(String tag, Throwable log) {
        XposedBridge.log(HOOK_TAG+" [W][" + tag + "]: " + log);
    }

    public static void logW(String tag, String msg, Exception exp) {
        XposedBridge.log(HOOK_TAG+" [W][" + tag + "]: " + msg + ", by: " + exp);
    }

    public static void logE(String tag, String msg) {
        XposedBridge.log(HOOK_TAG+" [E][" + tag + "]: " + msg);
    }

    public static void logE(String msg) {
        XposedBridge.log(HOOK_TAG+" [E]: " + msg);
    }

    public static void logE(String tag, Throwable log) {
        XposedBridge.log(HOOK_TAG+" [E][" + tag + "]: " + log);
    }

    public static void logE(String tag, String pkg, String msg) {
        XposedBridge.log(HOOK_TAG+" [E][" + pkg + "][" + tag + "]: " + msg);
    }

    public static void logE(String tag, String pkg, Throwable log) {
        XposedBridge.log(HOOK_TAG+" [E][" + pkg + "][" + tag + "]: " + log);
    }

    public static void logE(String tag, String pkg, Exception exp) {
        XposedBridge.log(HOOK_TAG+" [E][" + pkg + "][" + tag + "]: " + exp);
    }

    public static void logE(String tag, String pkg, String msg, Throwable log) {
        XposedBridge.log(HOOK_TAG+" [E][" + pkg + "][" + tag + "]: " + msg + ", by: " + log);
    }

    public static void logE(String tag, String pkg, String msg, Exception exp) {
        XposedBridge.log(HOOK_TAG+" [E]{" + pkg + "][" + tag + "]: " + msg + ", by: " + exp);
    }

    public static void logD(String msg) {
        XposedBridge.log(HOOK_TAG+" [D]: " + msg);
    }

    public static void logD(String tag, String pkg, String msg) {
        XposedBridge.log(HOOK_TAG+"[D][" + pkg + "][" + tag + "]: " + msg);
    }
}
