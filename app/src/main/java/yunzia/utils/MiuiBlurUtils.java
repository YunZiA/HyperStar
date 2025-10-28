package yunzia.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import java.lang.reflect.Method;

public class MiuiBlurUtils {
    private static Boolean ENABLE_MIUI_BLUR = null;
    public static Method METHOD_ADD_BG_BLEND_COLOR = null;
    public static Method METHOD_CLEAR_BG_BLEND_COLOR = null;
    public static Method METHOD_GET_PASS_WINDOW_BLUR_MODE = null;
    public static Method METHOD_SET_BG_BLUR_MODE = null;
    public static Method METHOD_SET_BG_BLUR_RADIUS = null;
    public static Method METHOD_SET_PASS_WINDOW_BLUR_MODE = null;
    public static Method METHOD_SET_VIEW_BLUR_MODE = null;
    private static Boolean SUPPORT_MIUI_BLUR = null;
    private static boolean isForceEnable = true;

    static {
        if (Build.VERSION.SDK_INT >= 33) {
            SUPPORT_MIUI_BLUR = Boolean.valueOf(SystemProperties.get("persist.sys.background_blur_supported", "false"));
        } else {
            SUPPORT_MIUI_BLUR = Boolean.FALSE;
        }
    }

    public static boolean addBackgroundBlenderColor(View view, int i, int i2) {
        if (!SUPPORT_MIUI_BLUR.booleanValue() || !isEffectEnable(view.getContext())) {
            return false;
        }
        try {
            if (METHOD_ADD_BG_BLEND_COLOR == null) {
                Class cls = Integer.TYPE;
                METHOD_ADD_BG_BLEND_COLOR = View.class.getMethod("addMiBackgroundBlendColor", cls, cls);
            }
            METHOD_ADD_BG_BLEND_COLOR.invoke(view, Integer.valueOf(i), Integer.valueOf(i2));
            return true;
        } catch (Exception unused) {
            METHOD_ADD_BG_BLEND_COLOR = null;
            return false;
        }
    }

    public static boolean clearBackgroundBlenderColor(View view) {
        if (!SUPPORT_MIUI_BLUR.booleanValue()) {
            return false;
        }
        try {
            if (METHOD_CLEAR_BG_BLEND_COLOR == null) {
                METHOD_CLEAR_BG_BLEND_COLOR = View.class.getMethod("clearMiBackgroundBlendColor", new Class[0]);
            }
            METHOD_CLEAR_BG_BLEND_COLOR.invoke(view, new Object[0]);
            return true;
        } catch (Exception unused) {
            METHOD_CLEAR_BG_BLEND_COLOR = null;
            return false;
        }
    }

    public static boolean clearBackgroundBlur(View view) {
        if (setBackgroundBlurMode(view, 0)) {
            return setViewBlurMode(view, 0);
        }
        return false;
    }

    public static synchronized void clearEffectEnable() {
        synchronized (MiuiBlurUtils.class) {
            ENABLE_MIUI_BLUR = null;
        }
    }

    public static boolean getPassWindowBlurEnabled(View view) {
        if (!SUPPORT_MIUI_BLUR.booleanValue()) {
            return false;
        }
        try {
            if (METHOD_GET_PASS_WINDOW_BLUR_MODE == null) {
                METHOD_GET_PASS_WINDOW_BLUR_MODE = View.class.getMethod("getPassWindowBlurEnabled", new Class[0]);
            }
            return ((Boolean) METHOD_GET_PASS_WINDOW_BLUR_MODE.invoke(view, new Object[0])).booleanValue();
        } catch (Exception unused) {
            METHOD_GET_PASS_WINDOW_BLUR_MODE = null;
            return false;
        }
    }

    public static synchronized boolean isEffectEnable(Context context) {
        synchronized (MiuiBlurUtils.class) {
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
        }
    }

    public static boolean isEnable() {
        return SUPPORT_MIUI_BLUR.booleanValue();
    }

    public static boolean setBackgroundBlur(View view, int i) {
        return setBackgroundBlur(view, i, false);
    }

    public static boolean setBackgroundBlurMode(View view, int i) {
        if (!SUPPORT_MIUI_BLUR.booleanValue()) {
            return false;
        }
        try {
            if (METHOD_SET_BG_BLUR_MODE == null) {
                METHOD_SET_BG_BLUR_MODE = View.class.getMethod("setMiBackgroundBlurMode", Integer.TYPE);
            }
            METHOD_SET_BG_BLUR_MODE.invoke(view, Integer.valueOf(i));
            return true;
        } catch (Exception unused) {
            METHOD_SET_BG_BLUR_MODE = null;
            return false;
        }
    }

    public static boolean setBackgroundBlurRadius(View view, int i) {
        if (!SUPPORT_MIUI_BLUR.booleanValue()) {
            return false;
        }
        if (i > 400) {
            i = 400;
        }
        try {
            if (METHOD_SET_BG_BLUR_RADIUS == null) {
                METHOD_SET_BG_BLUR_RADIUS = View.class.getMethod("setMiBackgroundBlurRadius", Integer.TYPE);
            }
            METHOD_SET_BG_BLUR_RADIUS.invoke(view, Integer.valueOf(i));
            return true;
        } catch (Exception unused) {
            METHOD_SET_BG_BLUR_RADIUS = null;
            return false;
        }
    }

    public static void setPassWindowBlurEnabled(View view, boolean z) {
        if (SUPPORT_MIUI_BLUR.booleanValue()) {
            try {
                if (METHOD_SET_PASS_WINDOW_BLUR_MODE == null) {
                    METHOD_SET_PASS_WINDOW_BLUR_MODE = View.class.getMethod("setPassWindowBlurEnabled", Boolean.TYPE);
                }
                METHOD_SET_PASS_WINDOW_BLUR_MODE.invoke(view, Boolean.valueOf(z));
            } catch (Exception unused) {
                Log.d("ggc", "setPassWindowBlurEnabled: null");
                METHOD_SET_PASS_WINDOW_BLUR_MODE = null;
            }
        }
    }

    public static boolean setViewBlurMode(View view, int i) {
        if (!SUPPORT_MIUI_BLUR.booleanValue()) {
            return false;
        }
        try {
            if (METHOD_SET_VIEW_BLUR_MODE == null) {
                METHOD_SET_VIEW_BLUR_MODE = View.class.getMethod("setMiViewBlurMode", Integer.TYPE);
            }
            METHOD_SET_VIEW_BLUR_MODE.invoke(view, Integer.valueOf(i));
            return true;
        } catch (Exception unused) {
            METHOD_SET_VIEW_BLUR_MODE = null;
            return false;
        }
    }

    public static boolean setBackgroundBlur(View view, int i, boolean z) {
        return setBackgroundBlur(view, i, z ? 2 : 1);
    }

    public static boolean setBackgroundBlur(View view, int i, int i2) {
        if (!SUPPORT_MIUI_BLUR.booleanValue() || !isEffectEnable(view.getContext())) {
            return false;
        }
        if (i > 400) {
            i = 400;
        }
        try {
            if (METHOD_SET_BG_BLUR_MODE == null) {
                METHOD_SET_BG_BLUR_MODE = View.class.getMethod("setMiBackgroundBlurMode", Integer.TYPE);
            }
            if (METHOD_SET_BG_BLUR_RADIUS == null) {
                METHOD_SET_BG_BLUR_RADIUS = View.class.getMethod("setMiBackgroundBlurRadius", Integer.TYPE);
            }
            METHOD_SET_BG_BLUR_MODE.invoke(view, 1);
            METHOD_SET_BG_BLUR_RADIUS.invoke(view, Integer.valueOf(i));
            return setViewBlurMode(view, i2);
        } catch (Exception unused) {
            METHOD_SET_BG_BLUR_MODE = null;
            METHOD_SET_BG_BLUR_RADIUS = null;
            return false;
        }
    }
}
