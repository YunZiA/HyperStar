# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#保持泛型
-keepattributes Signature

#保持反射不被混淆
-keepattributes EnclosingMethod


#-keep class * extends com.yunzia.hyperstar.hook.base.HookerHelper
#
#-keep class com.yunzia.hyperstar.hook.util.**{*;}
#-keep class com.yunzia.hyperstar.utils.Helper
#-keep class com.yunzia.hyperstar.InitHook
#-keep class com.yunzia.hyperstar.ui.**{*;}
#
#
## 这指定了继承Serizalizable的类的如下成员不被移除混淆
#-keepclassmembers class * implements java.io.Serializable {
#    static final long serialVersionUID;
#    private static final java.io.ObjectStreamField[] serialPersistentFields;
#    private void writeObject(java.io.ObjectOutputStream);
#    private void readObject(java.io.ObjectInputStream);
#    java.lang.Object writeReplace();
#    java.lang.Object readResolve();
#}
-keep class com.yunzia.hyperstar.** { *; }

