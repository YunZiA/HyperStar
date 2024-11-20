package com.yunzia.hyperstar.ui.base.navtype

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import androidx.navigation.NavType
import com.google.gson.Gson
import kotlinx.parcelize.Parcelize

@Parcelize
data class PagersModel(val title:String,var key:String):Parcelable



inline fun <reified T : Parcelable> pagersJson(isNullableAllowed: Boolean = false): NavType<T> {
    return object : NavType<T>(isNullableAllowed) {

        override val name: String
            get() = "pagersJson"

        override fun get(bundle: Bundle, key: String): T? {  //从Bundle中检索 Parcelable类型
            return bundle.getParcelable(key)
        }

        override fun parseValue(value: String): T {  //定义传递给 String 的 Parsing 方法
            return Gson().fromJson(value, T::class.java)
        }

        override fun put(bundle: Bundle, key: String, value: T) {  //作为 Parcelable 类型添加到 Bundle
            bundle.putParcelable(key, value)
        }

    }
}