///*
// * This file is part of HookTool.
//
// * HookTool is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as
// * published by the Free Software Foundation, either version 3 of the
// * License.
//
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// * GNU General Public License for more details.
//
// * You should have received a copy of the GNU General Public License
// * along with this program. If not, see <https://www.gnu.org/licenses/>.
//
// * Copyright (C) 2023-2025 HChenX
// */
//package com.yunzia.hyperstar.hook.tool;
//
//import static com.hchen.hooktool.log.LogExpand.createRuntimeExceptionLog;
//import static com.hchen.hooktool.tool.additional.ContextTool.FLAG_ALL;
//import static com.yunzia.hyperstar.hook.tool.starLog.logE;
//import static com.yunzia.hyperstar.hook.tool.starLog.logW;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.res.AssetManager;
//import android.content.res.Resources;
//import android.content.res.TypedArray;
//import android.content.res.loader.ResourcesLoader;
//import android.content.res.loader.ResourcesProvider;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.ParcelFileDescriptor;
//import android.util.Pair;
//import android.util.TypedValue;
//
//import com.hchen.hooktool.HCData;
//import com.hchen.hooktool.hook.IHook;
//import com.hchen.hooktool.tool.CoreTool;
//
//import java.io.File;
//import java.io.IOException;
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Objects;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.function.Consumer;
//
//import de.robv.android.xposed.IXposedHookZygoteInit;
//import de.robv.android.xposed.XC_MethodHook;
//
///**
// * 资源注入工具
// *
// * @author 焕晨HChen
// */
//public final class ResInjectTool {
//
//    private static String TAG = "ResInjectTool";
//    private static ResourcesLoader resourcesLoader = null;
//    private static String mModulePath = null;
//    private static Handler mHandler = null;
//
//    /**
//     * 请在 {@link  com.hchen.hooktool.HCInit#initStartupParam(IXposedHookZygoteInit.StartupParam)} 处调用。
//     *
//     * @param modulePath startupParam.modulePath 即可
//     */
//    public static void initResInjectTool(String modulePath) {
//        mModulePath = modulePath;
//    }
//
//
//    public static String getStackTrace() {
//        StringBuilder stringBuilder = new StringBuilder();
//        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
//        Arrays.stream(stackTraceElements).forEach(new Consumer<StackTraceElement>() {
//            @Override
//            public void accept(StackTraceElement stackTraceElement) {
//                String clazz = stackTraceElement.getClassName();
//                String method = stackTraceElement.getMethodName();
//                String field = stackTraceElement.getFileName();
//                int line = stackTraceElement.getLineNumber();
//                stringBuilder.append("\nat ").append(clazz).append(".")
//                        .append(method).append("(")
//                        .append(field).append(":")
//                        .append(line).append(")");
//            }
//        });
//        return stringBuilder.toString();
//    }
//
//    /**
//     * 把本项目资源注入目标作用域上下文。一般调用本方法即可。<br/>
//     * 请在项目 app 下的 build.gradle 中添加如下代码：
//     * <pre> {@code
//     * Kotlin Gradle DSL:
//     *
//     * androidResources.additionalParameters("--allow-reserved-package-id", "--package-id", "0x64")
//     *
//     * Groovy:
//     *
//     * aaptOptions.additionalParameters '--allow-reserved-package-id', '--package-id', '0x64'
//     *
//     * }<br/>
//     * Tip: `0x64` is the resource id, you can change it to any value you want.(recommended [0x30 to 0x6F])
//     */
//    public static Resources loadModuleRes(Resources resources, boolean doOnMainLooper) {
//        boolean load;
//        if (resources == null) {
//            logW(TAG, "Context can't is null!", getStackTrace());
//            return null;
//        }
//        if (mModulePath == null) {
//            mModulePath = HCData.getModulePath() != null ? HCData.getModulePath() : null;
//            if (mModulePath == null) {
//                logW(TAG, "Module path is null, can't load module res!", getStackTrace());
//                return null;
//            }
//        }
//        load = loadResAboveApi30(resources, doOnMainLooper);
//        if (!load) {
//            /*try {
//                return getModuleRes(context);
//            } catch (PackageManager.NameNotFoundException e) {
//                logE(tag(), "failed to load resource! critical error!! scope may crash!!", e);
//            }*/
//        }
//        if (!resourcesArrayList.contains(resources))
//            resourcesArrayList.add(resources);
//        return resources;
//    }
//
//    public static Resources loadModuleRes(Resources resources) {
//        return loadModuleRes(resources, false);
//    }
//
//    public static Resources loadModuleRes(Context context, boolean doOnMainLooper) {
//        return loadModuleRes(context.getResources(), doOnMainLooper);
//    }
//
//    public static Resources loadModuleRes(Context context) {
//        return loadModuleRes(context, false);
//    }
//
//    /**
//     * 来自 QA 的方法
//     */
//    private static boolean loadResAboveApi30(Resources resources, boolean doOnMainLooper) {
//        if (resourcesLoader == null) {
//            try (ParcelFileDescriptor pfd = ParcelFileDescriptor.open(new File(mModulePath),
//                ParcelFileDescriptor.MODE_READ_ONLY)) {
//                ResourcesProvider provider = ResourcesProvider.loadFromApk(pfd);
//                ResourcesLoader loader = new ResourcesLoader();
//                loader.addProvider(provider);
//                resourcesLoader = loader;
//            } catch (IOException e) {
//                logE(TAG, "Failed to add resource! debug: above api 30.", e);
//                return false;
//            }
//        }
//        if (doOnMainLooper)
//            if (Looper.myLooper() == Looper.getMainLooper()) {
//                return addLoaders(resources);
//            } else {
//                if (mHandler == null) {
//                    mHandler = new Handler(Looper.getMainLooper());
//                }
//                mHandler.post(() -> addLoaders(resources));
//                return true; // 此状态下保持返回 true，请观察日志是否有报错来判断是否成功。
//            }
//        else
//            return addLoaders(resources);
//    }
//
//    private static boolean addLoaders(Resources resources) {
//        try {
//            resources.addLoaders(resourcesLoader);
//        } catch (IllegalArgumentException e) {
//            String expected1 = "Cannot modify resource loaders of ResourcesImpl not registered with ResourcesManager";
//            if (expected1.equals(e.getMessage())) {
//                // fallback to below API 30
//                return loadResBelowApi30(resources);
//            } else {
//                logE(TAG, "Failed to add loaders!", e);
//                return false;
//            }
//        }
//        return true;
//    }
//
//    /**
//     * @noinspection JavaReflectionMemberAccess
//     */
//    @SuppressLint("DiscouragedPrivateApi")
//    private static boolean loadResBelowApi30(Resources resources) {
//        try {
//            AssetManager assets = resources.getAssets();
//            Method addAssetPath = AssetManager.class.getDeclaredMethod("addAssetPath", String.class);
//            addAssetPath.setAccessible(true);
//            Integer cookie = (Integer) addAssetPath.invoke(assets, mModulePath);
//            if (cookie == null || cookie == 0) {
//                logW(TAG, "Method 'addAssetPath' result 0, maybe load res failed!", getStackTrace());
//                return false;
//            }
//        } catch (Throwable e) {
//            logE(TAG, "Failed to add resource! debug: below api 30.", e);
//            return false;
//        }
//        return true;
//    }
//
//    private static final List<Resources> resourcesArrayList = new ArrayList<>();
//    private static final ConcurrentHashMap<Integer, Boolean> resMap = new ConcurrentHashMap<>();
//    private static final List<XC_MethodHook.Unhook> unhooks = new ArrayList<>();
//    private static final ConcurrentHashMap<String, Pair<ReplacementType, Object>> replacements = new ConcurrentHashMap<>();
//
//    private static boolean hooked;
//
//    private ResInjectTool() {
//        hooked = false;
//        resourcesArrayList.clear();
//        resMap.clear();
//        unhooks.clear();
//        replacements.clear();
//    }
//
//    private enum ReplacementType {
//        ID,
//        DENSITY,
//        OBJECT
//    }
//
//    public static int getFakeResId(String resName) {
//        return 0x7e000000 | (resName.hashCode() & 0x00ffffff);
//    }
//
//    public static int getFakeResId(Resources res, int id) {
//        return getFakeResId(res.getResourceName(id));
//    }
//
//    /**
//     * 设置资源 ID 类型的替换
//     */
//    public static void setResReplacement(String pkg, String type, String name, int replacementResId) {
//        try {
//            applyHooks();
//            replacements.put(pkg + ":" + type + "/" + name, new Pair<>(ReplacementType.ID, replacementResId));
//        } catch (Throwable t) {
//            logE(TAG, "Failed to set res replacement!", t);
//        }
//    }
//
//    /**
//     * 设置密度类型的资源
//     */
//    public static void setDensityReplacement(String pkg, String type, String name, float replacementResValue) {
//        try {
//            applyHooks();
//            replacements.put(pkg + ":" + type + "/" + name, new Pair<>(ReplacementType.DENSITY, replacementResValue));
//        } catch (Throwable t) {
//            logE(TAG, "Failed to set density res replacement!", t);
//        }
//    }
//
//    /**
//     * 设置 Object 类型的资源
//     */
//    public static void setObjectReplacement(String pkg, String type, String name, Object replacementResValue) {
//        try {
//            applyHooks();
//            replacements.put(pkg + ":" + type + "/" + name, new Pair<>(ReplacementType.OBJECT, replacementResValue));
//        } catch (Throwable t) {
//            logE(TAG, "Failed to set object res replacement!", t);
//        }
//    }
//
//    private static void applyHooks() {
//        if (hooked) return;
//        if (mModulePath == null) {
//            mModulePath = HCData.getModulePath() != null ? HCData.getModulePath() : null;
//            if (mModulePath == null) {
//                unHookRes();
//                throw new RuntimeException(createRuntimeExceptionLog("Module path is null, Please init this in initStartupParam()!"));
//            }
//        }
//        Method[] resMethods = Resources.class.getDeclaredMethods();
//        for (Method method : resMethods) {
//            String name = method.getName();
//            switch (name) {
//                case "getInteger", "getLayout", "getBoolean", "getDimension",
//                     "getDimensionPixelOffset", "getDimensionPixelSize", "getText", "getFloat",
//                     "getIntArray", "getStringArray", "getTextArray", "getAnimation" -> {
//                    if (method.getParameterTypes().length == 1
//                        && method.getParameterTypes()[0].equals(int.class)) {
//                        hookResMethod(method.getName(), int.class, hookResBefore);
//                    }
//                }
//                case "getColor" -> {
//                    if (method.getParameterTypes().length == 2) {
//                        hookResMethod(method.getName(), int.class, Resources.Theme.class, Optional.of(hookResBefore));
//                    }
//                }
//                case "getFraction" -> {
//                    if (method.getParameterTypes().length == 3) {
//                        hookResMethod(method.getName(), int.class, int.class, int.class, Optional.of(hookResBefore));
//                    }
//                }
//                case "getDrawableForDensity" -> {
//                    if (method.getParameterTypes().length == 3) {
//                        hookResMethod(method.getName(), int.class, int.class, Resources.Theme.class, Optional.of(hookResBefore));
//                    }
//                }
//            }
//        }
//
//        Method[] typedMethod = TypedArray.class.getDeclaredMethods();
//        for (Method method : typedMethod) {
//            if (method.getName().equals("getColor")) {
//                hookTypedMethod(method.getName(), int.class, int.class, hookTypedBefore);
//            }
//        }
//        hooked = true;
//    }
//
//    private static void hookResMethod(String name, Object... args) {
//        unhooks.add(CoreTool.hookMethod(Resources.class, name, args));
//    }
//
//    private static void hookTypedMethod(String name, Object... args) {
//        unhooks.add(CoreTool.hookMethod(TypedArray.class, name, args));
//    }
//
//    public static void unHookRes() {
//        if (unhooks.isEmpty()) {
//            hooked = false;
//            return;
//        }
//        for (XC_MethodHook.Unhook unhook : unhooks) {
//            unhook.unhook();
//        }
//        unhooks.clear();
//        hooked = false;
//    }
//
//    private static final IHook hookTypedBefore = new IHook() {
//        @Override
//        public void before() {
//            int index = (int) getArgs(0);
//            int[] mData = (int[]) CoreTool.getField(thisObject(), "mData");
//            int type = mData[index];
//            int id = mData[index + 3];
//
//            if (id != 0 && (type != TypedValue.TYPE_NULL)) {
//                Resources mResources = (Resources) CoreTool.getField(thisObject(), "mResources");
//                Object value = getTypedArrayReplacement(mResources, id);
//                if (value != null) {
//                    setResult(value);
//                }
//            }
//        }
//    };
//
//    private static final IHook Optional.of(hookResBefore) = new IHook() {
//        @Override
//        public void before() {
//            if (resourcesArrayList.isEmpty()) {
//                resourcesArrayList.add(loadModuleRes(ContextTool.getContext(FLAG_ALL)));
//            }
//            if (Boolean.TRUE.equals(resMap.get((int) getArgs(0)))) {
//                return;
//            }
//            for (Resources resources : resourcesArrayList) {
//                if (resources == null) return;
//                String method = mMember.getName();
//                Object value;
//                try {
//                    value = getResourceReplacement(resources, (Resources) thisObject(), method, mArgs);
//                } catch (Resources.NotFoundException e) {
//                    continue;
//                }
//                if (value != null) {
//                    if ("getDimensionPixelOffset".equals(method) || "getDimensionPixelSize".equals(method)) {
//                        if (value instanceof Float) value = ((Float) value).intValue();
//                    }
//                    setResult(value);
//                    break;
//                }
//            }
//        }
//    };
//
//    private static Object getResourceReplacement(Resources resources, Resources res, String method, Object[] args) throws Resources.NotFoundException {
//        if (resources == null) return null;
//        String pkgName = null;
//        String resType = null;
//        String resName = null;
//        try {
//            pkgName = res.getResourcePackageName((int) args[0]);
//            resType = res.getResourceTypeName((int) args[0]);
//            resName = res.getResourceEntryName((int) args[0]);
//        } catch (Throwable ignore) {
//        }
//        if (pkgName == null || resType == null || resName == null) return null;
//
//        String resFullName = pkgName + ":" + resType + "/" + resName;
//        String resAnyPkgName = "*:" + resType + "/" + resName;
//
//        Object value;
//        Integer modResId;
//        Pair<ReplacementType, Object> replacement = null;
//        if (replacements.containsKey(resFullName)) {
//            replacement = replacements.get(resFullName);
//        } else if (replacements.containsKey(resAnyPkgName)) {
//            replacement = replacements.get(resAnyPkgName);
//        }
//        if (replacement != null) {
//            switch (replacement.first) {
//                case OBJECT -> {
//                    return replacement.second;
//                }
//                case DENSITY -> {
//                    return (Float) replacement.second * res.getDisplayMetrics().density;
//                }
//                case ID -> {
//                    modResId = (Integer) replacement.second;
//                    if (modResId == 0) return null;
//                    try {
//                        resources.getResourceName(modResId);
//                    } catch (Resources.NotFoundException n) {
//                        throw n;
//                    }
//                    if (method == null) return null;
//                    resMap.put(modResId, true);
//                    if ("getDrawable".equals(method))
//                        value = CoreTool.callMethod(resources, method, modResId, args[1]);
//                    else if ("getDrawableForDensity".equals(method) || "getFraction".equals(method))
//                        value = CoreTool.callMethod(resources, method, modResId, args[1], args[2]);
//                    else
//                        value = CoreTool.callMethod(resources, method, modResId);
//                    resMap.remove(modResId);
//                    return value;
//                }
//            }
//        }
//        return null;
//    }
//
//    private static Object getTypedArrayReplacement(Resources resources, int id) {
//        if (id != 0) {
//            String pkgName = null;
//            String resType = null;
//            String resName = null;
//            try {
//                pkgName = resources.getResourcePackageName(id);
//                resType = resources.getResourceTypeName(id);
//                resName = resources.getResourceEntryName(id);
//            } catch (Throwable ignore) {
//            }
//            if (pkgName == null || resType == null || resName == null) return null;
//
//            try {
//                String resFullName = pkgName + ":" + resType + "/" + resName;
//                String resAnyPkgName = "*:" + resType + "/" + resName;
//
//                Pair<ReplacementType, Object> replacement = null;
//                if (replacements.containsKey(resFullName)) {
//                    replacement = replacements.get(resFullName);
//                } else if (replacements.containsKey(resAnyPkgName)) {
//                    replacement = replacements.get(resAnyPkgName);
//                }
//                if (replacement != null && (Objects.requireNonNull(replacement.first) == ReplacementType.OBJECT)) {
//                    return replacement.second;
//                }
//            } catch (Throwable e) {
//                logE(TAG, e);
//            }
//        }
//        return null;
//    }
//
//    // 下面注入方法存在风险，可能导致资源混乱，抛弃。
//    /*public static Context getModuleContext(Context context)
//            throws PackageManager.NameNotFoundException {
//        return getModuleContext(context, null);
//    }
//
//    public static Context getModuleContext(Context context, Configuration config)
//            throws PackageManager.NameNotFoundException {
//        Context mModuleContext;
//        mModuleContext = context.createPackageContext(mProjectPkg, Context.CONTEXT_IGNORE_SECURITY).createDeviceProtectedStorageContext();
//        return config == null ? mModuleContext : mModuleContext.createConfigurationContext(config);
//    }
//
//    public static Resources getModuleRes(Context context)
//            throws PackageManager.NameNotFoundException {
//        Configuration config = context.getResources().getConfiguration();
//        Context moduleContext = getModuleContext(context);
//        return (config == null ? moduleContext.getResources() : moduleContext.createConfigurationContext(config).getResources());
//    }*/
//}
