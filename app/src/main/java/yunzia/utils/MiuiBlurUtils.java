package yunzia.utils;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import java.lang.reflect.Method;

public abstract class MiuiBlurUtils {
    private static Boolean ENABLE_MIUI_BLUR = null;
    public static Method METHOD_ADD_BG_BLEND_COLOR = null;
    public static Method METHOD_CLEAR_BG_BLEND_COLOR = null;
    public static Method METHOD_SET_BG_BLUR_MODE = null;
    public static Method METHOD_SET_BG_BLUR_RADIUS = null;
    public static Method METHOD_SET_VIEW_BLUR_MODE = null;
    private static Boolean SUPPORT_MIUI_BLUR = null;
    private static boolean isForceEnable = true;

    static {
        if (!isForceEnable) {
            SUPPORT_MIUI_BLUR = Boolean.FALSE;
        } else {
            SUPPORT_MIUI_BLUR = Boolean.valueOf(SystemProperties.get("persist.sys.background_blur_supported", "false"));
        }
    }

    public static boolean isEnable() {
        return SUPPORT_MIUI_BLUR.booleanValue();
    }

    public static synchronized boolean isEffectEnable(Context context) {
        synchronized (MiuiBlurUtils.class) {
            try {
                if (!SUPPORT_MIUI_BLUR.booleanValue()) {
                    return false;
                }
                if (context == null) {
                    return false;
                }
                if (ENABLE_MIUI_BLUR == null) {
                    ENABLE_MIUI_BLUR = Boolean.valueOf(Settings.Secure.getInt(context.getContentResolver(), "background_blur_enable", 0) == 1);
                }
                return ENABLE_MIUI_BLUR.booleanValue();
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public static synchronized void clearEffectEnable() {
        synchronized (MiuiBlurUtils.class) {
            ENABLE_MIUI_BLUR = null;
        }
    }

    public static boolean setBackgroundBlur(View view, int i) {
        return setBackgroundBlur(view, i, false);
    }

    public static boolean setBackgroundBlur(View view, int i, boolean z) {
        return setBackgroundBlur(view, i, z ? 2 : 1);
    }

    public static boolean setBackgroundBlur(View view, int i, int i2) {
        if (!SUPPORT_MIUI_BLUR.booleanValue() || !isEffectEnable(view.getContext())) {
            return false;
        }
        try {
            if (METHOD_SET_BG_BLUR_MODE == null) {
                METHOD_SET_BG_BLUR_MODE = View.class.getMethod("setMiBackgroundBlurMode", Integer.TYPE);
            }
            if (METHOD_SET_BG_BLUR_RADIUS == null) {
                METHOD_SET_BG_BLUR_RADIUS = View.class.getMethod("setMiBackgroundBlurRadius", Integer.TYPE);
            }
            METHOD_SET_BG_BLUR_MODE.invoke(view, 1);
            METHOD_SET_BG_BLUR_RADIUS.invoke(view, i);
            return setViewBlurMode(view, i2);
        } catch (Exception unused) {
            METHOD_SET_BG_BLUR_MODE = null;
            METHOD_SET_BG_BLUR_RADIUS = null;
            return false;
        }
    }

    public static boolean setBackgroundBlurMode(View view, int i) {
        if (!SUPPORT_MIUI_BLUR) {
            return false;
        }
        try {
            if (METHOD_SET_BG_BLUR_MODE == null) {
                METHOD_SET_BG_BLUR_MODE = View.class.getMethod("setMiBackgroundBlurMode", Integer.TYPE);
            }
            METHOD_SET_BG_BLUR_MODE.invoke(view, i);
            return true;
        } catch (Exception unused) {
            METHOD_SET_BG_BLUR_MODE = null;
            return false;
        }
    }

    public static boolean clearBackgroundBlur(View view) {
        if (setBackgroundBlurMode(view, 0)) {
            return setViewBlurMode(view, 0);
        }
        return false;
    }

    public static boolean setViewBlurMode(View view, int i) {
        if (!SUPPORT_MIUI_BLUR) {
            return false;
        }
        try {
            if (METHOD_SET_VIEW_BLUR_MODE == null) {
                METHOD_SET_VIEW_BLUR_MODE = View.class.getMethod("setMiViewBlurMode", Integer.TYPE);
            }
            METHOD_SET_VIEW_BLUR_MODE.invoke(view, i);
            return true;
        } catch (Exception unused) {
            METHOD_SET_VIEW_BLUR_MODE = null;
            return false;
        }
    }

    public static boolean addBackgroundBlenderColor(View view, int i, int i2) {
        if (!SUPPORT_MIUI_BLUR || !isEffectEnable(view.getContext())) {
            return false;
        }
        try {
            if (METHOD_ADD_BG_BLEND_COLOR == null) {
                Log.d("backgroundBlur", "addBackgroundBlenderColor: METHOD_ADD_BG_BLEND_COLOR = null");
                Class<Integer> cls = Integer.TYPE;
                METHOD_ADD_BG_BLEND_COLOR = View.class.getMethod("addMiBackgroundBlendColor", cls, cls);
            }else {
                Log.d("backgroundBlur", "addBackgroundBlenderColor: METHOD_ADD_BG_BLEND_COLOR != null");

            }
            METHOD_ADD_BG_BLEND_COLOR.invoke(view, i, i2);
            return true;
        } catch (Exception unused) {
            METHOD_ADD_BG_BLEND_COLOR = null;
            return false;
        }
    }

    public static boolean clearBackgroundBlenderColor(View view) {
        if (!SUPPORT_MIUI_BLUR) {
            return false;
        }
        try {
            if (METHOD_CLEAR_BG_BLEND_COLOR == null) {
                Class<?>[] clsArr = new Class[0];
                METHOD_CLEAR_BG_BLEND_COLOR = View.class.getMethod("clearMiBackgroundBlendColor", clsArr);
            }
            METHOD_CLEAR_BG_BLEND_COLOR.invoke(view, (Object) null);
            return true;
        } catch (Exception unused) {
            METHOD_CLEAR_BG_BLEND_COLOR = null;
            return false;
        }
    }
}

