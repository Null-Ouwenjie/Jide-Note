package com.ouwenjie.note.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理已存在的activity
 * Created by 文杰 on 2015/4/29.
 */
public class MyActivityManager {

    private static MyActivityManager activityManager = null;

    private static List<Activity> activities = new ArrayList<>();

    private MyActivityManager(){

    }

    public static MyActivityManager getInstance(){
        if(activityManager == null){
            activityManager = new MyActivityManager();
        }
        return activityManager;
    }


    public void addActivity(Activity activity){
        activities.add(activity);
        LogUtils.e("addActivity");
    }

    public void finishActivity(Class c){
        for(Activity activity : activities){
            if(activity.getClass() == c) {
                activity.finish();
                activities.remove(activity);
                LogUtils.e("remove Activity");
            }
        }
    }

    // 检测某 Activity 是否在列表
    public boolean checkActivityExist(Class c){
        for(Activity activity : activities){
            if(activity.getClass() == c){
                return true;
            }
        }
        return false;
    }

}
