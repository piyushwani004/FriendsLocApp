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
# Picasso
-dontwarn com.squareup.okhttp.**
# Picasso Transformations
-dontwarn jp.co.cyberagent.android.gpuimage.**

-dontwarn okio.**
-dontwarn okhttp3.**
-dontwarn retrofit2.**

-dontwarn org.w3c.dom.**
-dontwarn org.joda.time.**
-dontwarn org.shaded.apache.**
-dontwarn org.ietf.jgss.**
-dontwarn com.firebase.**

-keepattributes Signature
-keepattributes *Annotation*
-keepattributes InnerClasses,EnclosingMethod

-keepattributes Signature,InnerClasses
-ignorewarnings
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses
