# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to the flags specified
# in getDefaultProguardFile('proguard-android-optimize.txt')

# Keep Room generated code and annotations
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Keep Moshi models and adapters
-keep @com.squareup.moshi.JsonClass class * { *; }
-keep class *JsonAdapter { *; }
-keepclassmembers class * {
    @com.squareup.moshi.Json <fields>;
}

# Keep Health Connect
-keep class androidx.health.connect.client.** { *; }

# Keep Dagger / Hilt
-keep class com.streakly.di.** { *; }
-keep class com.streakly.StreaklyApplication { *; }
