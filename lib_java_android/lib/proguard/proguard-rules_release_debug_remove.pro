-assumenosideeffects class android.util.Log {
    *** d(...);
}
-assumenosideeffects class com.tezov.lib_java.debug.DebugLog {
    *** allow(...);
    *** isEnable*(...);
    *** findFilter*(...);
}
-assumenosideeffects class * {
    *** toDebugLog*(...);
    *** toDebugString*(...);
}
-assumenosideeffects class com.tezov.lib_java.type.runnable.RunnableGroup {
    *** name(...);
}
-assumenosideeffects class com.tezov.lib_java.type.runnable.RunnableGroup$Action {
    *** name(...);
}
