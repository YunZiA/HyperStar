package yunzia.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.view.ViewCompat;

public class ViewUtils {

    public static boolean isNightMode(Context context) {
        return isNightMode(context.getResources().getConfiguration());
    }

    public static boolean isNightMode(Configuration configuration) {
        return configuration.isNightModeActive();
    }





}

