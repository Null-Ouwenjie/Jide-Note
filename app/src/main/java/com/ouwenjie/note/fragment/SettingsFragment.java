package com.ouwenjie.note.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ouwenjie.note.R;
import com.ouwenjie.note.activity.AboutActivity;
import com.ouwenjie.note.activity.IntroduceActivity;
import com.ouwenjie.note.utils.LogUtils;
import com.ouwenjie.note.utils.SysUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;

import yalantis.com.sidemenu.interfaces.ScreenShotable;

/**
 * Create at 2015年2月26日17:52:09 By ouwenjie
 */
public class SettingsFragment extends Fragment implements ScreenShotable, View.OnClickListener {

    private Activity activity;

    private View containerView;
    private Bitmap bitmap;

    private TextView versionTv;
    private TextView aboutTv;
    private TextView scoreTv;
    private TextView introduceTv;
    private TextView feedbackTv;

    private FeedbackAgent agent;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment MemoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.containerView = view.findViewById(R.id.container);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SettingsFragment"); //统计页面
        try {
            versionTv.setText("V " + activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SettingsFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        versionTv = (TextView) view.findViewById(R.id.settings_version_tv);
        aboutTv = (TextView) view.findViewById(R.id.settings_about_tv);
        scoreTv = (TextView) view.findViewById(R.id.settings_score_tv);
        introduceTv = (TextView) view.findViewById(R.id.settings_introduce_tv);
        feedbackTv = (TextView) view.findViewById(R.id.settings_feedback_tv);

        aboutTv.setOnClickListener(this);
        scoreTv.setOnClickListener(this);
        introduceTv.setOnClickListener(this);
        feedbackTv.setOnClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void takeScreenShot() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Bitmap bitmap = Bitmap.createBitmap(containerView.getWidth(),
                        containerView.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                containerView.draw(canvas);
                SettingsFragment.this.bitmap = bitmap;
            }
        };

        thread.start();
    }

    @Override
    public Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.settings_about_tv:
                activity.startActivity(new Intent(activity, AboutActivity.class));
                break;
            case R.id.settings_score_tv:
                SysUtils sysUtils = SysUtils.getInstance(activity);
                sysUtils.goMarket();
                break;
            case R.id.settings_introduce_tv:
                activity.startActivity(new Intent(activity, IntroduceActivity.class));
                break;
            case R.id.settings_feedback_tv:
                LogUtils.e("about_feedback_tv");
                agent = new FeedbackAgent(activity);
                agent.startFeedbackActivity();
                break;
            default:
                break;
        }
    }
}
