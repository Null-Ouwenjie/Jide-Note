package com.ouwenjie.note.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.ouwenjie.note.R;
import com.ouwenjie.note.utils.DisplayAnimUtils;
import com.ouwenjie.note.utils.SysUtils;
import com.umeng.fb.FeedbackAgent;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;

    private TextView versionTv;
    private TextView aboutTv;
    private TextView scoreTv;
    private TextView introduceTv;
    private TextView feedbackTv;

    private FeedbackAgent agent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initToolbar();

        initWidget();

    }

    private void initWidget() {
        versionTv = (TextView) findViewById(R.id.settings_version_tv);
        aboutTv = (TextView) findViewById(R.id.settings_about_tv);
        scoreTv = (TextView) findViewById(R.id.settings_score_tv);
        introduceTv = (TextView) findViewById(R.id.settings_introduce_tv);
        feedbackTv = (TextView) findViewById(R.id.settings_feedback_tv);

        aboutTv.setOnClickListener(this);
        scoreTv.setOnClickListener(this);
        introduceTv.setOnClickListener(this);
        feedbackTv.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            versionTv.setText("V " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle("设置");
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_white_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                DisplayAnimUtils.slideRightOut(SettingsActivity.this);
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.settings_about_tv:
                startActivity(new Intent(SettingsActivity.this, AboutActivity.class));
                break;
            case R.id.settings_score_tv:
                SysUtils sysUtils = SysUtils.getInstance(SettingsActivity.this.getApplicationContext());
                sysUtils.goMarket();
                break;
            case R.id.settings_introduce_tv:
                startActivity(new Intent(SettingsActivity.this, IntroduceActivity.class));
                break;
            case R.id.settings_feedback_tv:
                agent = new FeedbackAgent(SettingsActivity.this);
                agent.startFeedbackActivity();
                break;
            default:
                break;
        }
    }



}
