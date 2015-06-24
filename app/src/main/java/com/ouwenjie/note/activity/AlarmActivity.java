package com.ouwenjie.note.activity;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.ouwenjie.note.R;
import com.ouwenjie.note.db.NoteDatabaseHelper;
import com.ouwenjie.note.model.BaseNote;
import com.ouwenjie.note.receiver.NoteAlarmReceiver;
import com.ouwenjie.note.helper.MyActivityManager;
import com.ouwenjie.note.utils.TimeUtils;
import com.umeng.analytics.MobclickAgent;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AlarmActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView contentTv;
    private Button closeBtn;
    private Button laterBtn;
    private TextView oneSentence;

    private BaseNote note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_alarm);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);// 淡化status bar 和 navigation bar

        // 突破锁屏
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("");
        keyguardLock.disableKeyguard();

        initWidget();

        initData();

    }

    private void initData() {
        long dbID = getIntent().getLongExtra(BaseNote.KEY_DB_ID,-1);

        if(dbID >= 0) {
            note = new NoteDatabaseHelper().get(dbID);
        }
        if(note != null) {
            contentTv.setText(note.getContent());
        }
    }

    private void initWidget() {
        contentTv = (TextView) findViewById(R.id.alarm_activity_content_tv);
        closeBtn = (Button) findViewById(R.id.alarm_activity_close_btn);
        laterBtn = (Button) findViewById(R.id.alarm_activity_later_btn);
        oneSentence = (TextView) findViewById(R.id.alarm_activity_one_tv);

        closeBtn.setOnClickListener(this);
        laterBtn.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("AlarmActivity"); // 统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("AlarmActivity"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }

    /**
     * 开启一个闹钟
     */
    private void setAlarmReceiver(long time){
        Intent receiverIntent = new Intent(this.getApplicationContext(), NoteAlarmReceiver.class);
        long dbId = note.getId();

        receiverIntent.putExtra(BaseNote.KEY_DB_ID,dbId );

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), note.getAlarmReqCode(), receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }

    /**
     * 取消闹钟
     */
    private void cancelAlarmReceiver(){
        Intent intent = new Intent(this.getApplicationContext(), NoteAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), note.getAlarmReqCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pendingIntent);/* 取消 */
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.alarm_activity_close_btn:
                MyActivityManager.getInstance().finishActivity(EditNoteActivity.class);
                if(MyActivityManager.getInstance().checkActivityExist(MainActivity.class)){
                    MyActivityManager.getInstance().finishActivity(MainActivity.class);
                }

                //  关闭闹钟
                cancelAlarmReceiver();

                // 改变 NOTE 的状态
                note.setAlarmState(BaseNote.NOTE_ALARM_STATE_OLD);
                note.setAlarmDate("");
                note.save();

                Intent intent = new Intent(AlarmActivity.this,EditNoteActivity.class);
                intent.putExtra(BaseNote.KEY_DB_ID,note.getId());

                startActivity(intent);
                finish();
                break;
            case R.id.alarm_activity_later_btn:
                Calendar calendar = Calendar.getInstance();
                long time = calendar.getTimeInMillis()+10*60*1000;
                String timeStr = TimeUtils.getTime(time,new SimpleDateFormat("yyyy-MM-dd HH:mm"));
                note.setAlarmDate(timeStr);
                note.save();

                setAlarmReceiver(time);

                finish();
                break;
            default:
                break;
        }
    }


}
