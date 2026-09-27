# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to the flags specified
# in getDefaultProguardFile('proguard-android-optimize.txt')

# Keep Room generated code and annotations
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Keep Moshi models
-keepclassmembers class * {
    @com.squareup.moshi.Json *;
}
-keep class * {
    @com.squareup.moshi.JsonClass(generateAdapter = true) *;
}

# Keep Health Connect
-keep class androidx.health.connect.client.** { *; }

# Keep Dagger / Hilt
-keep class com.streakly.di.** { *; }
-keep class com.streakly.StreaklyApplication { *; }
