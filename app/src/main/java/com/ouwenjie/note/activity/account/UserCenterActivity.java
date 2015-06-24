package com.ouwenjie.note.activity.account;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import com.ouwenjie.note.NoteApplication;
import com.ouwenjie.note.R;
import com.ouwenjie.note.helper.TencentSDKHelper;
import com.ouwenjie.note.model.JideUser;
import com.ouwenjie.note.utils.DisplayAnimUtils;

/**
 *
 * Created by 文杰 on 2015/6/19.
 */
public class UserCenterActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private ViewGroup userInfoLayout;
    private ViewGroup userNoteCountLayout;
    private ViewGroup userLogOutLayout;

    private TencentSDKHelper tencentSDKHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);
        initToolbar();

        tencentSDKHelper = TencentSDKHelper.getInstance(this.getApplicationContext());

        userInfoLayout = (ViewGroup) findViewById(R.id.user_info_layout);
        userNoteCountLayout = (ViewGroup) findViewById(R.id.user_note_count_layout);
        userLogOutLayout = (ViewGroup) findViewById(R.id.user_logout_layout);
        userInfoLayout.setOnClickListener(this);
        userNoteCountLayout.setOnClickListener(this);
        userLogOutLayout.setOnClickListener(this);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle("账号");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                DisplayAnimUtils.slideRightOut(UserCenterActivity.this);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.user_info_layout:
                break;
            case R.id.user_note_count_layout:
                break;
            case R.id.user_logout_layout:
                tencentSDKHelper.logout();
                NoteApplication.getNoteApplication(this).setVisitor(true);

                JideUser.setLoginStatusChanged(this.getApplicationContext(), true);  // 登录状态改变了

                finish();
                DisplayAnimUtils.slideRightOut(this);
                break;
            default:
                break;
        }
    }

}
