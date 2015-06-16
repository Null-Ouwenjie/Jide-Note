package com.ouwenjie.note.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ouwenjie.note.activity.AlarmActivity;
import com.ouwenjie.note.model.BaseNote;
import com.ouwenjie.note.utils.LogUtils;

/**
 *  Note 闹钟
 * Created by 文杰 on 2015/4/24.
 */
public class NoteAlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        long dbId = intent.getLongExtra(BaseNote.KEY_DB_ID, -1);
        LogUtils.e("Alarm -- receiver -- dbId ==> "+dbId);

        Intent alarmIntent = new Intent(context, AlarmActivity.class);
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        alarmIntent.putExtra(BaseNote.KEY_DB_ID,dbId);

        context.startActivity(alarmIntent);

    }

}
