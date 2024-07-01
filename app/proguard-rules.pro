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
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

# Repackage classes into the top-level.
-repackageclasses

# Rome reflectively loads classes referenced in com/rometools/rome/rome.properties.
-adaptresourcefilecontents com/rometools/rome/rome.properties
-keep,allowobfuscation class * implements com.rometools.rome.feed.synd.Converter
-keep,allowobfuscation class * implements com.rometools.rome.io.ModuleParser
-keep,allowobfuscation class * implements com.rometools.rome.io.WireFeedParser

# Disable warnings for missing classes from OkHttp.
-dontwarn org.conscrypt.ConscryptHostnameVerifier

# Disable warnings for missing classes from JDOM.
-dontwarn org.jaxen.DefaultNavigator
-dontwarn org.jaxen.NamespaceContext
-dontwarn org.jaxen.VariableContext

# Keep SLF4J Logger
-dontwarn org.slf4j.impl.StaticLoggerBinder
-dontwarn org.slf4j.impl.StaticMDCBinder
-dontwarn org.slf4j.impl.StaticMarkerBinder
-keep class org.slf4j.** { *; }
-keep class ch.qos.logback.** { *; }

# For SLF4J Simple
-keep class org.slf4j.impl.** { *; }

# Retrofit
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep class okio.** { *; }
# This is generated automatically by the Android Gradle plugin.
-dontwarn org.slf4j.impl.StaticLoggerBinder
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE
-keep class com.rometools.** { *; }
