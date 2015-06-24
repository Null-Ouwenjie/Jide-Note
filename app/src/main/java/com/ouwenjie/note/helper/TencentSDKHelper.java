package com.ouwenjie.note.helper;

import android.app.Activity;
import android.content.Context;

import com.ouwenjie.note.R;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

/**
 *
 * Created by 文杰 on 2015/6/23.
 */
public class TencentSDKHelper {


    private volatile static TencentSDKHelper tencentSDKHelper = null;

    private static Tencent mTencent;
    private static Context mContext;

    // 单例
    public static TencentSDKHelper getInstance(Context context) {
        if (tencentSDKHelper == null) {
            synchronized (TencentSDKHelper.class) {
                if (tencentSDKHelper == null) {
                    tencentSDKHelper = new TencentSDKHelper();
                    mContext = context;
                }
            }
        }
        return tencentSDKHelper;
    }

    private TencentSDKHelper() {

    }


    public void implTencent() {
        if (mTencent == null) {
            String appId = mContext.getResources().getString(R.string.tencent_app_id);
            mTencent = Tencent.createInstance(appId, mContext.getApplicationContext());
        }

    }

    public Tencent getTencent() {
        implTencent();
        return mTencent;
    }

    public void login(Activity activity, IUiListener listener) {
        implTencent();
        if(!mTencent.isSessionValid()) {
            mTencent.login(activity, "all", listener);
        }

    }


    public void logout(){
        implTencent();
        mTencent.logout(mContext);
    }

}


