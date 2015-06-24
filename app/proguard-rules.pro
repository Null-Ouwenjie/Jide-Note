# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\adt-bundle-windows-x86_64-20140702\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

-ignorewarnings	# 忽略警告，避免打包时某些警告出现
-optimizationpasses 5	# 指定代码的压缩级别
-dontusemixedcaseclassnames	# 是否使用大小写混合

-dontpreverify                      # 混淆时是否做预校验
-verbose                            # 混淆时是否记录日志
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*        # 混淆时所采用的算法


# 继承activity,application,service,broadcastReceiver,contentprovider....不进行混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

-keepclassmembers class * {
     public <init>(org.json.JSONObject);
}

-keep public class com.ouwenjie.note.R$* {
  public static <fields>;
}

-keep class **.R$* {*;}

-keepclasseswithmembernames class * {	# 保持 native 方法不被混淆
    native <methods>;
}

-keepclasseswithmembers class * {	# 保持自定义控件类不被混淆,对于所有类，有这个构造函数不进行混淆,主要是为了在layout中的，自定义的view
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {	# 保持自定义控件类不被混淆
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers enum * {	# 保持枚举 enum 类不被混淆
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {	# 保持 Parcelable 不被混淆
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class * extends android.app.Activity {  #保持类成员,这个主要是在layout 中写的onclick方法android:onclick="onClick"，不进行混淆
   public void *(android.view.View);
   public boolean *(android.view.View);
}

-keepclassmembers  class  *  extends android.support.v4.app.Fragment {
    public void  *(android.view.View);
    public boolean *(android.view.View);
}

-keepclassmembers class * extends android.support.v7.widget.RecyclerView.Adapter {
    public void *(android.view.View);
    public boolean *(android.view.View);
    public void onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder,int);
}

-dontwarn android.support.v4.**     #缺省proguard 会检查每一个引用是否正确，但是第三方库里面往往有些不会用到的类，没有正确引用。如果不配置的话，系统就会报错。
-dontwarn android.os.**

-keep class com.sina.**{*;}     # 新浪微博
-keep class com.baidu.** {*;}
-keep class com.tencent.**{*;}

-keep class android.support.v4.**{*;}
-keep class android.support.v7.**{*;}
-keep class com.yalantis.**{*;}
-keep class com.github.**{*;}
-keep class jp.wasabeef.**{*;}
-keep class com.umeng.**{*;}
-keep class com.afollestad.**{*;}
-keep class io.codetail.**{*;}
-keep class com.orm.**{*;}
-keep class com.gc.materialdesign.**{*;}

#LeanCloud
-keepattributes Signature
-dontwarn com.jcraft.jzlib.**
-keep class com.jcraft.jzlib.**  { *;}

-dontwarn sun.misc.**
-keep class sun.misc.** { *;}

-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.** { *;}

-dontwarn sun.security.**
-keep class sun.security.** { *; }

-dontwarn com.google.**
-keep class com.google.** { *;}

-dontwarn com.avos.**
-keep class com.avos.** { *;}

-keep public class android.net.http.SslError
-keep public class android.webkit.WebViewClient

-dontwarn android.webkit.WebView
-dontwarn android.net.http.SslError
-dontwarn android.webkit.WebViewClient

-dontwarn android.support.**

-dontwarn org.apache.**
-keep class org.apache.** { *;}

-dontwarn org.jivesoftware.smack.**
-keep class org.jivesoftware.smack.** { *;}

-dontwarn com.loopj.**
-keep class com.loopj.** { *;}

-dontwarn org.xbill.**
-keep class org.xbill.** { *;}

-keepattributes *Annotation*


# Bmob 相关
-ignorewarnings
# 这里根据具体的SDK版本修改
-libraryjars libs/BmobSDK_V3.4.0_0616.jar

-keepattributes Signature
-keep class cn.bmob.v3.** {*;}

# 保证继承自BmobObject、BmobUser类的JavaBean不被混淆
-keep class com.example.bmobexample.Person{*;}
-keep class com.example.bmobexample.MyUser{*;}
-keep class com.example.bmobexample.relationaldata.Weibo{*;}
-keep class com.example.bmobexample.relationaldata.Comment{*;}

