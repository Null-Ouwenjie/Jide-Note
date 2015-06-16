package com.ouwenjie.note;

import android.content.Context;
import android.text.TextUtils;

import com.orm.SugarApp;
import com.ouwenjie.note.db.NoteDatabaseHelper;
import com.ouwenjie.note.model.BaseNote;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import java.util.TreeSet;

/**
 *
 * Created by 文杰 on 2015/3/31.
 */
public class NoteApplication extends SugarApp {

    private TreeSet<BaseNote> noteSet = new TreeSet<>();

    @Override
    public void onCreate() {

        super.onCreate();

        umengConfig();
        updateNoteList();

    }

    /**
     * 友盟配置
     */
    private void umengConfig() {
        MobclickAgent.openActivityDurationTrack(false); // 禁止默认的页面统计方式
        /** 设置是否对日志信息进行加密, 默认false(不加密). */
        AnalyticsConfig.enableEncrypt(true);
    }


    /**
     * 重新拿一遍数据库
     */
    public void updateNoteList(){
        noteSet = new NoteDatabaseHelper().getAll();
    }

    /**
     * 返回当前的笔记列表
     * @return
     */
    public TreeSet<BaseNote> getNoteSet() {

        return noteSet;
    }

    /**
     * 获取设备信息
     * @param context
     * @return
     */
    public static String getDeviceInfo(Context context) {
        try{
            org.json.JSONObject json = new org.json.JSONObject();
            android.telephony.TelephonyManager tm = (android.telephony.TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            String device_id = tm.getDeviceId();

            android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            String mac = wifi.getConnectionInfo().getMacAddress();
            json.put("mac", mac);

            if( TextUtils.isEmpty(device_id) ){
                device_id = mac;
            }

            if( TextUtils.isEmpty(device_id) ){
                device_id = android.provider.Settings.Secure.getString(context.getContentResolver(),android.provider.Settings.Secure.ANDROID_ID);
            }

            json.put("device_id", device_id);

            return json.toString();
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
