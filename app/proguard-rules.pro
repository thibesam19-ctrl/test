-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

# Room
-keep class * extends androidx.room.RoomDatabase
-keepclassmembers @androidx.room.Entity class * { *; }
-keepclassmembers @androidx.room.Dao interface * { *; }

# Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# Moshi
-keepclassmembers class ** { @com.squareup.moshi.FromJson *; @com.squareup.moshi.ToJson *; }

# ML Kit
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.** { *; }

# Kotlin serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# Firebase
-keep class com.google.firebase.** { *; }

# RevenueCat
-keep class com.revenuecat.purchases.** { *; }

# Retrofit
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * { @retrofit2.http.* <methods>; }

# TensorFlow Lite
-keep class org.tensorflow.** { *; }
-keep class org.tensorflow.lite.** { *; }
-dontwarn org.tensorflow.**

# Google Generative AI (Gemini)
-keep class com.google.ai.client.** { *; }
-keep class com.google.generativeai.** { *; }
-dontwarn com.google.ai.client.**

# OkHttp SSE
-keep class okhttp3.sse.** { *; }
-dontwarn okhttp3.sse.**

# Axiom AI module — keep all interfaces and data classes
-keep class com.axiom.aicoach.ai.** { *; }
-keepclassmembers class com.axiom.aicoach.ai.** { *; }

# Kotlinx Serialization (used by AI providers for JSON)
-keep @kotlinx.serialization.Serializable class * { *; }
-keepclassmembers class * { @kotlinx.serialization.* <fields>; }

# OkHttp (general)
-dontwarn okhttp3.**
-dontwarn okio.**
