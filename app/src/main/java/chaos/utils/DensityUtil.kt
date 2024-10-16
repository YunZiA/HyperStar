package chaos.utils

import android.content.res.Resources

class DensityUtil {
    companion object {
        fun dpToPx(resources: Resources, dp: Float): Float {
            // 获取屏幕的密度
            val density = resources.displayMetrics.density

            // 转换 dp 到 px
            return dp * density
        }
    }

}