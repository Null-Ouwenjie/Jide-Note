package com.ouwenjie.note.utils;

import android.app.Activity;

import com.ouwenjie.note.R;


/**
 * 动画效果工具类
 * Created by 文杰 on 2015/1/15.
 */
public class DisplayAnimUtils {

    /* Activity切换效果需配合startActivity(), startActivityForResult(), finish()方法使用 */

    /**
     * 滑动，右边退出
     */
    public static void slideRightOut(Activity activity){
        activity.overridePendingTransition(-1, R.anim.out_to_right);
    }

    /**
     * 滑动，左边退出
     */
    public static void slideleftOut(Activity activity){
        activity.overridePendingTransition(-1, R.anim.out_to_left);
    }

    /**
     * 滑动，顶部退出
     */
    public static void slideTopOut(Activity activity){
        activity.overridePendingTransition(-1, R.anim.out_to_top);
    }

    /**
     * 滑动，底部退出
     */
    public static void slideButtomOut(Activity activity){
        activity.overridePendingTransition(-1, R.anim.out_to_bottom);
    }

    /**
     * 滑动，新界面从左边进入，当前画面从右边退出
     * @param activity
     */
    public static void slideLeftInRightOut(Activity activity){
        activity.overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
    }

    /**
     * 滑动，新界面从右边进入，当前画面从左边退出
     * @param activity
     */
    public static void slideRightInLeftOut(Activity activity){
        activity.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    /**
     * 退出应用，新界面从2倍缩小到正常大小，当前画面缩小到0 退出
     * @param activity
     */
    public static void exitApp(Activity activity){
        activity.overridePendingTransition(R.anim.exit_in, R.anim.exit_out);
    }

}
