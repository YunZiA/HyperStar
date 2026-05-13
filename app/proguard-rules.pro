# ==========================================
# HyperStar ProGuard Rules
# ==========================================

# --- 基础属性保留 ---
-keepattributes Signature
-keepattributes EnclosingMethod
-keepattributes *Annotation*
-keepattributes InnerClasses
-keepattributes SourceFile,LineNumberTable

# --- Xposed / LSPosed 模块入口 ---
-keep class com.yunzia.hyperstar.hook.ModuleMain { *; }
-keep class com.yunzia.hyperstar.hook.core.base.BaseXposedModule { *; }

# --- LibXposed API ---
-keep class io.github.libxposed.** { *; }
-keep class io.github.libxposed.api.** { *; }
-keep class io.github.libxposed.service.** { *; }
-keepclassmembers class * {
    @io.github.libxposed.api.annotations.BeforeInvocation <methods>;
    @io.github.libxposed.api.annotations.AfterInvocation <methods>;
}

# --- Hook 系统：保留所有 hook 类（反射调用） ---
-keep class com.yunzia.hyperstar.hook.** { *; }

# --- Xposed 注解 ---
-keep @com.yunzia.hyperstar.hook.core.annotation.Init class * { *; }

# --- Activity 入口 ---
-keep class com.yunzia.hyperstar.MainActivity { *; }
-keep class com.yunzia.hyperstar.MainActivityAlias { *; }

# --- Kotlin Serialization ---
-keepattributes RuntimeVisibleAnnotations
-keep class kotlinx.serialization.** { *; }
-keepclassmembers @kotlinx.serialization.Serializable class ** {
    *** Companion;
    kotlinx.serialization.KSerializer serializer(...);
}
-keepclasseswithmembers class **$$serializer {
    *** INSTANCE;
}

# --- Parcelize ---
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# --- OkHttp ---
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# --- Gson ---
-keep class com.google.gson.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# --- Compose / AndroidX ---
-dontwarn androidx.**
-keep class androidx.compose.** { *; }

# --- miuix KMP 组件 ---
-dontwarn top.yukonga.miuix.**

# --- 生成的搜索索引 ---
-keep class generated.SearchIndex { *; }
-keep class generated.SearchEntry { *; }

# --- 通用：枚举 ---
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# --- 通用：native 方法 ---
-keepclasseswithmembernames class * {
    native <methods>;
}
