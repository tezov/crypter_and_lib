-whyareyoukeeping class com.tezov.lib_java.debug.DebugLog
-whyareyoukeeping class com.tezov.lib_java.debug.DebugTrack
-whyareyoukeeping class com.tezov.lib_java.debug.DebugException

#-dontusemixedcaseclassnames
#-dontoptimize
#-dontshrink

#Annotation protection
-keepclassmembers,allowobfuscation class * {
    @com.tezov.lib_java_android.annotation.ProguardFieldKeep *;
}
-keepclassmembers,allowobfuscation interface * {
    @com.tezov.lib_java_android.annotation.ProguardFieldKeep *;
}

#Description Table field
-keep,allowobfuscation interface com.tezov.lib_java_android.database.sqlLite.dbField
-keep,allowobfuscation interface * implements com.tezov.lib_java_android.database.sqlLite.dbField
#-keep,allowobfuscation @interface com.tezov.lib_java_android.database.sqlLite.dbFieldAnnotation
-keepclassmembers,allowobfuscation interface * {
    @com.tezov.lib_java_android.database.sqlLite.dbFieldAnnotation *;
}

#Used by reflection
-keepclassmembers,allowobfuscation class com.tezov.lib_java.socket.prebuild.datagram.DatagramRegister { *; }
-keepclassmembers,allowobfuscation class * extends com.tezov.lib_java.socket.prebuild.datagram.DatagramRegister { *; }
-keepclassmembers,allowobfuscation class com.tezov.lib_java.socket.prebuild.datagram.Datagram {
    public <init>();
}
-keepclassmembers,allowobfuscation class * extends com.tezov.lib_java.socket.prebuild.datagram.Datagram {
    public <init>();
}
-keepclassmembers,allowobfuscation class com.tezov.lib_java_android.factory.Pool {
    <init>(...);
}
-keepclassmembers,allowobfuscation class com.tezov.lib_java_android.factory.FactoryObject {
    <init>(...);
}
-keepclassmembers,allowobfuscation class * extends com.tezov.lib_java_android.factory.FactoryObject {
    <init>(...);
}
-keepclassmembers,allowobfuscation class com.tezov.lib_java_android.camera.CameraDevice {
    <init>(...);
}
-keepclassmembers,allowobfuscation class com.tezov.lib_java_android.database.firebase.fbAuthHelper {
    <init>(...);
}

#BOUNCY CASTLE
-keep class org.bouncycastle.jcajce.provider.** { *; }
-keep class org.bouncycastle.jce.provider.** { *; }
-dontwarn javax.naming.NamingEnumeration
-dontwarn javax.naming.NamingException
-dontwarn javax.naming.directory.Attribute
-dontwarn javax.naming.directory.Attributes
-dontwarn javax.naming.directory.DirContext
-dontwarn javax.naming.directory.InitialDirContext
-dontwarn javax.naming.directory.SearchControls
-dontwarn javax.naming.directory.SearchResult

#Kryo
-keep,allowshrinking class com.esotericsoftware.** {
   <fields>;
   <methods>;
}
-keep,allowshrinking class com.esotericsoftware.kryo.** { *; }
-keep,allowshrinking class com.esotericsoftware.kryo.io.** { *; }

##################  Native
-keep,includedescriptorclasses class com.google.common.**
-dontwarn com.google.common.**
-dontwarn sun.reflect.**
-dontwarn sun.nio.ch.**
-dontwarn sun.misc.**
#-keep class sun.misc.Unsafe { *; }
-dontwarn sun.misc.Unsafe
-keep,allowshrinking class java.beans.** { *; }
-dontwarn java.beans.**