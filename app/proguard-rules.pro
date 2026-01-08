# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep Adobe SDK classes
-keep class com.adobe.marketing.mobile.** { *; }

# If you use Gson for JSON parsing (Adobe SDK may use it internally)
-keepattributes Signature
-keepattributes *Annotation*
