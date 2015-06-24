package com.ouwenjie.note.activity.account;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.ouwenjie.note.NoteApplication;
import com.ouwenjie.note.R;
import com.ouwenjie.note.helper.BmobHelper;
import com.ouwenjie.note.helper.TencentSDKHelper;
import com.ouwenjie.note.model.JideUser;
import com.ouwenjie.note.utils.DisplayAnimUtils;
import com.ouwenjie.note.utils.LogUtils;
import com.ouwenjie.note.utils.ToastUtils;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import cn.bmob.v3.listener.OtherLoginListener;

/**
 *
 * Created by 文杰 on 2015/6/19.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    public static final String TAG = "LoginActivity";

    public static final int MSG_LOGIN_SUCCESS = 1002;
    public static final int MSG_LOGIN_FAILURE = 1003;

    private Toolbar toolbar;

    private Button qqLoginBtn;
    private Button notLoginBtn;

    private TencentSDKHelper tencentSDKHelper;

    private Handler handler = new LoginHandler(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initToolbar();
        initWidget();

        tencentSDKHelper = TencentSDKHelper.getInstance(this.getApplicationContext());
        tencentSDKHelper.implTencent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(handler != null){
            handler.removeCallbacksAndMessages(null);
        }
    }

    private void initWidget(){
        qqLoginBtn = (Button) findViewById(R.id.qq_login_btn);
        notLoginBtn = (Button) findViewById(R.id.not_login_btn);
        qqLoginBtn.setOnClickListener(this);
        notLoginBtn.setOnClickListener(this);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle("登录");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                DisplayAnimUtils.slideRightOut(LoginActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 应用调用Andriod_SDK接口时，如果要成功接收到回调，
        // 需要在调用接口的Activity的onActivityResult方法中增加如下代码
        tencentSDKHelper.getTencent().onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);

    }

    private void qqLogin(){
        tencentSDKHelper.login(this, new IUiListener() {
            @Override
            public void onComplete(Object obj) {
                LogUtils.e("QQ Login Result == ",obj.toString());
                JSONObject jsonObject = (JSONObject) obj;
                try {
                    String token = jsonObject.getString(com.tencent.connect.common.Constants.PARAM_ACCESS_TOKEN);
                    String expires = jsonObject.getString(com.tencent.connect.common.Constants.PARAM_EXPIRES_IN);
                    String openId = jsonObject.getString(com.tencent.connect.common.Constants.PARAM_OPEN_ID);
                    BmobHelper.loginWithAuth(LoginActivity.this.getApplicationContext(),
                            "qq", token, expires, openId,
                            new OtherLoginListener() {
                                @Override
                                public void onSuccess(JSONObject jsonObject) {
                                    LogUtils.e("Third Login Result == ",jsonObject.toString());
                                    Message msg = new Message();
                                    msg.what = MSG_LOGIN_SUCCESS;
                                    msg.obj = jsonObject;
                                    handler.sendMessage(msg);

                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    LogUtils.e("Third Login Failure  "+ s);
                                }
                            });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(UiError uiError) {
                handler.sendEmptyMessage(MSG_LOGIN_FAILURE);
            }

            @Override
            public void onCancel() {

            }
        });

    }

    private void thirdLoginSuccess(JSONObject jsonObject) {

        try {
            JSONObject qqObj = jsonObject.getJSONObject("qq");
            String accessToken = qqObj.getString("access_token");
            String expiresIn = String.valueOf(qqObj.getLong("expires_in"));
            String openId = qqObj.getString("openid");

            JideUser user;
            user = ((NoteApplication) (LoginActivity.this.getApplication())).getUser();
            user.saveQQloginInfo(LoginActivity.this.getApplicationContext(),accessToken,expiresIn,openId);
            NoteApplication.getNoteApplication(this).setVisitor(false);// 清除游客状态

            JideUser.setLoginStatusChanged(this.getApplicationContext(),true);  // 登录状态改变了
            gotoUserCenter();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    /**
     * 登录失败
     */
    private void thirdLoginFailure() {
        ToastUtils.showShort(LoginActivity.this, "登录失败，请稍后再试。");
    }

    /**
     * 登录成功之后的页面跳转
     */
    private void gotoUserCenter(){

        finish();
        startActivity(new Intent(LoginActivity.this,UserCenterActivity.class));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.qq_login_btn:
                qqLogin();
                break;
            default:
                break;
        }
    }

    private static class LoginHandler extends Handler{
        private final WeakReference<LoginActivity> mActivity;
        public LoginHandler(LoginActivity activity) {
            mActivity = new WeakReference<LoginActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_LOGIN_SUCCESS:
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    mActivity.get().thirdLoginSuccess(jsonObject);
                    break;
                case MSG_LOGIN_FAILURE:
                    mActivity.get().thirdLoginFailure();
                    break;
                default:
                    break;
            }
        }
    }


}
