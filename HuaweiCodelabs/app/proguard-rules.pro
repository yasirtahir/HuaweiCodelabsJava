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


-ignorewarnings
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keep class com.hianalytics.android.*{*;}
-keep class com.huawei.updatesdk.*{*;}
-keep class com.huawei.hms.*{*;}
-keep class com.huawei.openalliance.ad.*{ *; }
-keep class com.huawei.hms.ads.* { *; }
-keep agconnect-services.json
-dontwarn com.huawei.**
-keep class com.huawei.* {*;}
-dontwarn org.slf4j.**
-keep class org.slf4j.* {*;}
-dontwarn org.springframework.**
-keep class org.springframework.* {*;}
-dontwarn com.fasterxml.jackson.**
-keep class com.fasterxml.jackson.* {*;}

# This is for DexGuard
#-keepresources string/agc_*
#-keepresources string/upsdk_store_url
#-keepresources string/hms_update_title
#-keepresourcefiles assets/hmsrootcas.bks
#-keepresourcefiles assets/grs_*

