package com.ouwenjie.note.model;

import android.content.Context;

import com.ouwenjie.note.utils.SharedPreferenceUtils;

import cn.bmob.v3.BmobUser;

/**
 *
 * Created by 文杰 on 2015/6/23.
 */
public class JideUser extends BmobUser {


    public static final String KEY_RET = "Tencent_RET";
    public static final String KEY_PAY_TOKEN = "Tencent_PAY_TOKEN";
    public static final String KEY_PF = "Tencent_PF";
    public static final String KEY_QUERY_AUTHORITY_COST = "Tencent_QUERY_AUTHORITY_COST";
    public static final String KEY_AUTHORITY_COST = "Tencent_AUTHORITY_COST";
    public static final String KEY_OPEN_ID = "Tencent_OPEN_ID";             // 关键
    public static final String KEY_EXPIRES_IN = "Tencent_EXPIRES_IN";       // 关键
    public static final String KEY_PFKEY = "Tencent_PFKEY";
    public static final String KEY_MSG = "Tencent_MSG";
    public static final String KEY_ACCESS_TOKEN = "Tencent_ACCESS_TOKEN";   // 关键
    public static final String KEY_LOGIN_COST = "Tencent_LOGIN_COST";

    public static final String KEY_EXPIRE_TIME = "Tencent_EXPIRE_TIME";

    public static final String KEY_NICK_NAME = "NICK_NAME";
    public static final String KEY_PHONE_NUMBER = "PHONE_NUMBER";
    public static final String KEY_EMAIL = "EMAIL";


    private String nickName;
    private String phoneNumber;
    private String email;

    private String expires_in;
    private String access_token;
    private String openid;
    private long expireTime;

    public String getNickName(Context context) {
        nickName = (String) SharedPreferenceUtils.get(context,KEY_NICK_NAME,"");
        return nickName;
    }

    public void setNickName(Context context, String nickName) {
        SharedPreferenceUtils.put(context,KEY_NICK_NAME,nickName);
        this.nickName = nickName;
    }

    public String getPhoneNumber(Context context) {
        phoneNumber = (String) SharedPreferenceUtils.get(context,KEY_PHONE_NUMBER,"");
        return phoneNumber;
    }

    public void setPhoneNumber(Context context, String phoneNumber) {
        SharedPreferenceUtils.put(context,KEY_PHONE_NUMBER,phoneNumber);
        this.phoneNumber = phoneNumber;
    }

    public String getEmail(Context context) {
        email = (String) SharedPreferenceUtils.get(context,KEY_EMAIL,"");
        return email;
    }

    public void setEmail(Context context, String email) {
        SharedPreferenceUtils.put(context,KEY_EMAIL,email);
        this.email = email;
    }

    public String getExpires_in(Context context) {
        expires_in = (String) SharedPreferenceUtils.get(context,KEY_EXPIRES_IN,"-1");
        return expires_in;
    }

    public void setExpires_in(Context context, String expires_in) {
        long time = System.currentTimeMillis()+Long.parseLong(expires_in)*1000;
        setExpireTime(context,time);
        SharedPreferenceUtils.put(context,KEY_EXPIRES_IN,expires_in);
        this.expires_in = expires_in;
    }

    public String getAccess_token(Context context) {
        access_token = (String) SharedPreferenceUtils.get(context,KEY_ACCESS_TOKEN,"");
        return access_token;
    }

    public void setAccess_token(Context context,String access_token) {
        SharedPreferenceUtils.put(context,KEY_ACCESS_TOKEN,access_token);
        this.access_token = access_token;
    }

    public String getOpenid(Context context) {
        openid = (String) SharedPreferenceUtils.get(context,KEY_OPEN_ID,"");
        return openid;
    }

    public void setOpenid(Context context,String openid) {
        SharedPreferenceUtils.put(context,KEY_OPEN_ID,openid);
        this.openid = openid;
    }

    public long getExpireTime(Context context) {
        expireTime = (long) SharedPreferenceUtils.get(context,KEY_EXPIRE_TIME,-1l);
        return expireTime;
    }

    public void setExpireTime(Context context, long expireTime) {
        SharedPreferenceUtils.put(context,KEY_EXPIRE_TIME,expireTime);
        this.expireTime = expireTime;
    }

    public void saveQQloginInfo(Context context, String token, String expiresId, String openId){
        setAccess_token(context, token);
        setExpires_in(context, expiresId);
        setOpenid(context, openId);
    }


    public void removeAllAuth(Context context){
        SharedPreferenceUtils.remove(context,KEY_EXPIRES_IN);
        SharedPreferenceUtils.remove(context,KEY_ACCESS_TOKEN);
        SharedPreferenceUtils.remove(context,KEY_OPEN_ID);
        SharedPreferenceUtils.remove(context,KEY_EXPIRE_TIME);

    }

    // 获取是否有登录状态改变
    public static boolean getLoginStatusChanged(Context context){
        return (boolean) SharedPreferenceUtils.get(context,"loginStatus",false);
    }

    // 设置登录状态的改变是否已被处理
    // false : 登录状态没有改变（或已被处理
    // true : 登录状态改变了（快去处理
    public static void setLoginStatusChanged(Context context, boolean status){
        SharedPreferenceUtils.put(context,"loginStatus",status);
    }

}
