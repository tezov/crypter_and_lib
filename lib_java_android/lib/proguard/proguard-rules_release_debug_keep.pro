-keep public class * extends java.lang.Throwable
-keep class android.util.Log {
    *** d(...);
}
-keep class com.tezov.lib_java.debug.DebugLog {
    *;
}
-keep class * {
    *** toDebugLog*(...);
    *** toDebugString*(...);
}
-keep class com.tezov.lib_java.type.runnable.RunnableGroup {
    *** name(...);
}
-keep class com.tezov.lib_java.type.runnable.RunnableGroup$Action {
    *** name(...);
}
