-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**
-keepclassmembers class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
-dontwarn dagger.hilt.**
-keep class dagger.hilt.** { *; }
