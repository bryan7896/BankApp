# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /usr/local/Cellar/android-sdk/24.3.3/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:
# React Native
-keep,allowobfuscation @interface com.facebook.proguard.annotations.DoNotStrip
-keep,allowobfuscation @interface com.facebook.proguard.annotations.KeepGettersAndSetters
-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip

# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.proguard.annotations.DoNotStrip class *
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.proguard.annotations.DoNotStrip *;
    @com.facebook.common.internal.DoNotStrip *;
}

# Keep native methods
-keepclassmembers class * {
    native <methods>;
}

# Keep setters in Views so that animations can still work
-keepclassmembers public class * extends android.view.View {
    void set*(***);
    *** get*();
}

# Keep React Native
-keep class com.facebook.react.** { *; }
-keep class com.facebook.hermes.** { *; }
-keep class com.facebook.jni.** { *; }

# Keep our app classes
-keep class com.bankapp.** { *; }
-keep class com.bankapp.modules.** { *; }
-keep class com.bankapp.session.** { *; }
-keep class com.bankapp.security.** { *; }

# Keep Native Modules
-keep class * extends com.facebook.react.bridge.ReactContextBaseJavaModule { *; }
-keep class * extends com.facebook.react.bridge.BaseJavaModule { *; }
-keep class * implements com.facebook.react.bridge.NativeModule { *; }

# Keep React Native Bridge
-keepclassmembers class * {
    @com.facebook.react.bridge.ReactMethod *;
    @com.facebook.react.bridge.ReactModule *;
}

# Keep JavaScript interfaces
-keep class com.facebook.react.bridge.JavaScriptModule { *; }
-keep class com.facebook.react.bridge.Callback { *; }

# Keep React Native Packages
-keepclassmembers class * extends com.facebook.react.ReactPackage {
    public *;
}

# Keep annotations
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keepattributes Signature

# Keep GCM
-keep class com.google.android.gms.** { *; }

# OkHttp
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**

# Conscrypt
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**

# Keep AndroidX
-keep class androidx.** { *; }
-keep interface androidx.** { *; }

# Keep EncryptedSharedPreferences
-keep class androidx.security.** { *; }

# Keep Kotlin
-keep class kotlin.** { *; }
-keepclassmembers class kotlin.Metadata {
    public *;
}

# Keep our models (Session, User, etc)
-keep class com.bankapp.session.Session { *; }

# Keep native methods in EncryptionManager
-keepclassmembers class com.bankapp.security.EncryptionManager {
    private javax.crypto.SecretKey secretKey;
}

# Keep all classes used by React Native
-keep class com.facebook.** { *; }
-keep class com.facebook.flipper.** { *; }
-dontwarn com.facebook.flipper.**

# Remove logs in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
}