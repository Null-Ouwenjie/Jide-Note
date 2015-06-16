package com.ouwenjie.note.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ouwenjie.note.R;
import com.ouwenjie.note.utils.DisplayAnimUtils;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;

    private TextView aboutOSL;
    private TextView aboutDev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initToolbar();

        aboutOSL = (TextView) findViewById(R.id.about_opensource_licence_tv);
        aboutDev = (TextView) findViewById(R.id.about_developer_tv);

        aboutOSL.setOnClickListener(this);
        aboutDev.setOnClickListener(this);

    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle("关于");
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_white_24dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                DisplayAnimUtils.slideRightOut(AboutActivity.this);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);// 淡化status bar 和 navigation bar

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.about_opensource_licence_tv:
                startActivity(new Intent(AboutActivity.this,ThanksActivity.class));
                break;
            case R.id.about_developer_tv:
                startActivity(new Intent(AboutActivity.this,AboutMeActivity.class));
                break;
            default:
                break;
        }
    }

}
