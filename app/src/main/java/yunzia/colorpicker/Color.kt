package yunzia.colorpicker

import android.util.Log
import androidx.compose.ui.graphics.Color

fun Color.toHex(): String {
    val red = "%02X".format((this.red * 255).toInt())
    val green = "%02X".format((this.green * 255).toInt())
    val blue = "%02X".format((this.blue * 255).toInt())
    val alpha = "%02X".format((this.alpha * 255).toInt())
    return "#${alpha}${red}${green}${blue}"
}

fun Color.toHexWithoutAlpha(): String {
    val red = "%02X".format(this.red * 255)
    val green = "%02X".format(this.green * 255)
    val blue = "%02X".format(this.blue * 255)
    return "#$red$green$blue"
}

fun String.colorFromHex(): Color {
    val hex = this.drop(1)
    Log.d("ggc", "colorFromHex: ${hex}")// 去掉前面的'#'

    if (hex.length == 6){
        val red = hex.substring(0, 2).toInt(16)
        val green = hex.substring(2, 4).toInt(16)
        val blue = hex.substring(4, 6).toInt(16)
        Log.d("ggc", "6: ${hex}")
        return Color(red, green, blue)
    }else if (hex.length == 8){
        Log.d("ggc", "8: ${hex}")
        val alpha = hex.substring(0, 2).toInt(16)
        val red = hex.substring(2, 4).toInt(16)
        val green = hex.substring(4, 6).toInt(16)
        val blue = hex.substring(6, 8).toInt(16)
        return Color(red, green, blue,alpha)
    }else{
        Log.d("ggc", "else: ${hex.length}")
        return Color.Transparent
    }
}