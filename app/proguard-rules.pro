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
