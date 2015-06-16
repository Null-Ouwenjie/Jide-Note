package com.ouwenjie.note.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;

import java.util.List;

/**
 * 系统级常用方法
 * 1、检测某个intent是否可用
 * 2、检测设备是否安装了GooglePlay\mobileQQ\weChat\BaiduMap\ ??
 * 3、检测某个类是否可用
 * 4、跳转到应用市场
 * 5、检测某个服务是否正在执行
 * 6、检测本应用是否处于可见状态
 * 7、震动
 *
 * Created by 文杰 on 2015/4/2.
 */
public class SysUtils {

    private static SysUtils sysUtils;

    private Context mContext;
    private PackageManager packageManager;

    private SysUtils(Context cxt) {
        this.mContext = cxt;
        this.packageManager = mContext.getPackageManager();
    }

    public static SysUtils getInstance(Context cxt) {
        if (null == sysUtils) {
            sysUtils = new SysUtils(cxt);
        }
        return sysUtils;
    }

    /**
     * 查询某个 Action 是否可用
     *
     * @param action
     * @return
     */
    public boolean isIntentAvailable(String action) {

        Intent intent = new Intent(action);

        return isIntentAvailable(intent);
    }

    /**
     * 查询带参数的 Action 是否可用
     *
     * @param action
     * @param URIs
     * @return
     */
    public boolean isIntentAvailable(String action, String URIs) {
        Intent intent = new Intent(action, Uri.parse(URIs));
        return isIntentAvailable(intent);
    }

    /**
     * 检测某个intent是否可用
     *
     * @param intent
     * @return
     */
    public boolean isIntentAvailable(Intent intent) {

        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * 检测Google Play是否可用
     */
    public boolean hasGooglePlayInstalled() {

        return hasInstalledPackage("com.android.vending");
    }

    /**
     * 是否已经安装了微信应用
     *
     * @return
     */
    public boolean hasWechatInstalled() {

        return hasInstalledPackage("com.tencent.mm");
    }

    /**
     * 是否已经安装了QQ应用
     *
     * @return
     */
    public boolean hasQQInstalled() {

        return hasInstalledPackage("com.tencent.mobileqq");
    }

    /**
     * 是否已经安装了百度地图应用
     *
     * @return
     */
    public boolean hasBaiduMapInstalled() {

        return hasInstalledPackage("com.baidu.BaiduMap");
    }

    /**
     * 查询是否安装了某个包
     *
     * @param packageName
     * @return
     */
    public boolean hasInstalledPackage(String packageName) {

        List<PackageInfo> list = packageManager.getInstalledPackages(0);

        if (null == list || list.size() == 0)
            return false;

        for (PackageInfo info : list) {

            if (info.packageName.equals(packageName))
                return true;
        }

        return false;
    }

    /**
     * 检查所支持的类
     *
     * @param className
     *            类的名称
     * @return 支持返回true,否则false
     */
    public static boolean isClassAvailable(String className) {

        try {

            Class.forName(className);
        } catch (ClassNotFoundException e) {

            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 跳转到应用市场的本应用的页面
     *
     */
    public void goMarket() {

        String strUri = "market://details?id=" + mContext.getPackageName();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(strUri));

        if (isIntentAvailable(intent)) {

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } else {
//          "很抱歉，调用电子市场失败"
        }
    }

    /**
     *
     * 判断某个服务是否正在执行
     *
     * @param cxt
     * @param serviceName
     * @return
     */
    public boolean isServiceRunning(Context cxt, String serviceName) {

        if (null == cxt || null == serviceName || "".equals(serviceName))
            return false;

        ActivityManager mActivityManager;
        List<ActivityManager.RunningServiceInfo> mServiceList;

        mActivityManager = (ActivityManager) cxt
                .getSystemService(Context.ACTIVITY_SERVICE);
        mServiceList = mActivityManager.getRunningServices(30);

        if (null != mServiceList && !mServiceList.isEmpty()) {

            for (int i = 0; i < mServiceList.size(); i++) {

                if (serviceName.equals(mServiceList.get(i).service
                        .getClassName())) {

                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 本应用是否为当前可视状态
     *
     * @param cxt
     * @return
     */
    public boolean isTopActivity(Context cxt) {

        String packageName = null;
        ActivityManager am;
        List<ActivityManager.RunningTaskInfo> tasksInfo;

        packageName = cxt.getPackageName();

        if (null != packageName) {

            am = (ActivityManager) cxt
                    .getSystemService(Context.ACTIVITY_SERVICE);
            tasksInfo = am.getRunningTasks(2);

            if (tasksInfo.size() > 0) {

                if (packageName.equals(tasksInfo.get(0).topActivity
                        .getPackageName())) { // 应用程序位于堆栈的顶层
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 震动（隔100ms，然后震动400ms）
     */
    public void vibrate() {

        long[] pattern = { 100, 400 }; // 震动时间（停止 开启）
        final Vibrator vibrator;

        vibrator = (Vibrator) mContext
                .getSystemService(Context.VIBRATOR_SERVICE); // 震动服务
        vibrator.vibrate(pattern, -1); // 震动一次

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                vibrator.cancel();
            }
        }, 500);
    }




}
