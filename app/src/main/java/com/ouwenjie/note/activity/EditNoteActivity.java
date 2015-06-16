package com.ouwenjie.note.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.ouwenjie.note.R;
import com.ouwenjie.note.adapter.ImageListAdapter;
import com.ouwenjie.note.db.NoteDatabaseHelper;
import com.ouwenjie.note.model.BaseNote;
import com.ouwenjie.note.receiver.NoteAlarmReceiver;
import com.ouwenjie.note.utils.ImageUtils;
import com.ouwenjie.note.utils.LogUtils;
import com.ouwenjie.note.utils.MyActivityManager;
import com.ouwenjie.note.utils.TimeUtils;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditNoteActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private static final String TAG = "EditNoteActivity";

    private static final int REQ_PICK_PHOTO = 100;
    private static final int REQ_SHOW_IMAGE = 101;
    private static final int REQ_TAKE_PHOTO = 102;

    public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "timepicker";

    public static final boolean SHOW = true;
    public static final boolean HIDE = false;

    private RelativeLayout editNoteLayout;    // 编辑页面
    private LinearLayout editNoteToolsBar;  // 工具栏

    public static List<Uri> imageUriList = new ArrayList<>();
    private RecyclerView imageListRecyclerView;     // 图片列表
    private LinearLayoutManager linearLayoutManager;
    private ImageListAdapter adapter;

    private ImageView backImg;          // 工具栏 -- 返回
    private TextView dateTxt;           // 工具栏 -- 日期
    private TextView timeTxt;           // 工具栏 -- 时间
    private ImageView paletteImg;       // 工具栏 -- 调色板
    private ImageView alarmImg;         // 工具栏 -- 提醒
    private ImageView addedImg;         // 工具栏 -- 附件

    private PopupWindow selectColorWindow;    // 调色板 -- 弹出的窗口
    private PopupWindow insertPicWindow;        // 插入图片 -- 弹出的窗口

    private EditText contentEt;         // 内容编辑

    private BaseNote note;              // 正在编辑的NOTE

    private int bgColor = BaseNote.LIGHT_YELLOW;    // 当前的背景颜色

    private DatePickerDialog datePickerDialog;      // 日期选择器
    private TimePickerDialog timePickerDialog;      // 时间选择器

    private TextView alarmDate;                     // 已选择的日期
    private TextView alarmTime;                     // 已选择的时间

    private boolean imageListState = SHOW;          // 图片列表显示状态
    private ImageView hideImageList;
    private TextView imageListStateTxt;
    private ImageView showImageList;

    private LinearLayout alarmInfoLayout;       // 下方的信息栏
    private TextView alarmDateTime;             // 信息栏的内容
    private ImageView closeAlarm;               // 关闭信息栏

    private Uri photoUri;               // 拍照时保存的URI

    private NoteDatabaseHelper dbHelper = new NoteDatabaseHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // If the Android version is lower than Jellybean, use this call to hide
        // the status bar.
        if(Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setContentView(R.layout.activity_edit_note);

        MyActivityManager.getInstance().addActivity(this);

        initWidget();
        initData();

        initImageListData();
        initRecyclerView();

        initDateTimePicker(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);// 淡化status bar 和 navigation bar

        MobclickAgent.onPageStart("EditNoteActivity"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("EditNoteActivity"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }

    /**
     * 初始化布局控件
     */
    private void initWidget(){

        editNoteLayout = (RelativeLayout) findViewById(R.id.edit_note_layout);
        editNoteToolsBar = (LinearLayout) findViewById(R.id.edit_note_tools_bar);

        backImg = (ImageView) findViewById(R.id.back_img);
        dateTxt = (TextView) findViewById(R.id.note_date_tv);
        timeTxt = (TextView) findViewById(R.id.note_time_tv);
        paletteImg = (ImageView) findViewById(R.id.note_palette_img);
        alarmImg = (ImageView) findViewById(R.id.note_alarm_clock_img);
        addedImg = (ImageView) findViewById(R.id.note_added_img);

        imageListRecyclerView = (RecyclerView) findViewById(R.id.note_image_list_layout);

        contentEt = (EditText) findViewById(R.id.note_edit_content_et);

        hideImageList = (ImageView) findViewById(R.id.note_image_hide_img);
        imageListStateTxt = (TextView) findViewById(R.id.note_image_tip_tv);
        showImageList = (ImageView) findViewById(R.id.note_image_show_img);

        alarmInfoLayout = (LinearLayout) findViewById(R.id.alarm_info_layout);
        alarmDateTime = (TextView) findViewById(R.id.alarm_date_time_tv);
        closeAlarm = (ImageView) findViewById(R.id.close_alarm_img);

        backImg.setOnClickListener(this);
        paletteImg.setOnClickListener(this);
        alarmImg.setOnClickListener(this);
        addedImg.setOnClickListener(this);

        hideImageList.setOnClickListener(this);
        imageListStateTxt.setOnClickListener(this);
        showImageList.setOnClickListener(this);

        closeAlarm.setOnClickListener(this);

    }

    /**
     * 初始化画面数据，根据Note展示数据
     */
    private void initData(){

        long dbId = getIntent().getLongExtra(BaseNote.KEY_DB_ID,-1);
        if(dbId >= 0){
            note = dbHelper.get(dbId);
        }else{ // 新建的Note
            note = new BaseNote("");
            // 如是新建的“提醒”，则一开始就弹出设置时间的窗口
            if(getIntent().getIntExtra(BaseNote.KEY_NOTE_TYPE,BaseNote.NOTE_TYPE_RECORD) == BaseNote.NOTE_TYPE_REMIND){
                showCustomView();
            }
        }

        setBgColor(note.getBgColor());              // 背景颜色
        contentEt.setText(note.getContent());       // 内容
        String[] lastChangedTime = note.getLastChangeDate().split(" ");
        dateTxt.setText(lastChangedTime[0]);        // 日期
        timeTxt.setText(lastChangedTime[1]);        // 时间

        if(note.getNoteState() == BaseNote.NOTE_STATE_INVALID){ // 如果是归档状态，则不可修改。
            contentEt.setFocusable(false);
        }else{
            contentEt.setFocusable(true);
        }

        if(note.getAlarmState() == BaseNote.NOTE_ALARM_STATE_VALID) {
            openAlarmLayout();
        } else{
            closeAlarmLayout();
        }
    }

    /**
     * 初始化 RecyclerView 列表
     */
    private void initRecyclerView() {

        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        imageListRecyclerView.setLayoutManager(linearLayoutManager);

        adapter = new ImageListAdapter(this, imageUriList);
        adapter.setItemOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNoteState();

                int position = (int) v.getTag();
                Intent intent = new Intent(EditNoteActivity.this, ImageViewPagerActivity.class);
                intent.putExtra("ImageListPosition", position);
                intent.putExtra("UriString", note.getImageSet());
                startActivityForResult(intent, REQ_SHOW_IMAGE);
            }
        });
        imageListRecyclerView.setAdapter(adapter);
    }

    /**
     * 初始化 图片列表
     */
    private List<Uri> initImageListData() {
        imageUriList = new ArrayList<>();
        String imageUriSet = note.getImageSet();
        if(imageUriSet != null && !(imageUriSet.equals(""))) {
            String[] imageUriString = note.getImageSet().split(" ");
            if (imageUriString.length != 0) {
                showImageListLayout();
                for (String s : imageUriString) {
                    imageUriList.add(Uri.parse(s));
                }
            }
        }else {
            hideImageListLayout();
        }
        return imageUriList;
    }

    /**
     * 显示图片列表布局
     */
    private void showImageListLayout(){
        imageListRecyclerView.setVisibility(View.VISIBLE);
        findViewById(R.id.note_image_show_img).setVisibility(View.VISIBLE);
        findViewById(R.id.note_image_tip_tv).setVisibility(View.VISIBLE);
        showImageList();
    }

    /**
     * 隐藏图片列表布局
     */
    private void hideImageListLayout(){
        findViewById(R.id.note_image_list_layout).setVisibility(View.GONE);
        findViewById(R.id.note_image_hide_img).setVisibility(View.GONE);
        findViewById(R.id.note_image_tip_tv).setVisibility(View.GONE);
        findViewById(R.id.note_image_show_img).setVisibility(View.GONE);
    }

    /**
     * 显示闹钟信息栏
     */
    private void openAlarmLayout() {
        alarmImg.setImageResource(R.drawable.ic_alarm_on_black_24dp);   // 提醒Img
        alarmDateTime.setText(note.getAlarmDate());
        alarmInfoLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏闹钟信息栏
     */
    private void closeAlarmLayout() {
        alarmImg.setImageResource(R.drawable.ic_alarm_off_black_24dp);
        alarmDateTime.setText("");
        alarmInfoLayout.setVisibility(View.INVISIBLE);
    }

    /**
     * 设置背景颜色
     * @param bgColor
     */
    private void setBgColor(int bgColor){
        this.bgColor = bgColor;

        switch (bgColor){
            case BaseNote.LIGHT_YELLOW:
                editNoteLayout.setBackgroundColor(getResources().getColor(R.color.light_yellow));
                editNoteToolsBar.setBackgroundColor(getResources().getColor(R.color.light_yellow_dark));
                break;
            case BaseNote.LIGHT_BLUE:
                editNoteLayout.setBackgroundColor(getResources().getColor(R.color.light_blue));
                editNoteToolsBar.setBackgroundColor(getResources().getColor(R.color.light_blue_dark));
                break;
            case BaseNote.LIGHT_GREEN:
                editNoteLayout.setBackgroundColor(getResources().getColor(R.color.light_green));
                editNoteToolsBar.setBackgroundColor(getResources().getColor(R.color.light_green_dark));
                break;
            case BaseNote.LIGHT_RED:
                editNoteLayout.setBackgroundColor(getResources().getColor(R.color.light_red));
                editNoteToolsBar.setBackgroundColor(getResources().getColor(R.color.light_red_dark));
                break;
            default:
                break;
        }
    }

    /**
     * 初始化日期时间选择器
     */
    private void initDateTimePicker(Bundle savedInstanceState){
        final Calendar calendar = Calendar.getInstance();

        datePickerDialog = DatePickerDialog.newInstance(this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                true);

        timePickerDialog = TimePickerDialog.newInstance(this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true,
                true);

        if (savedInstanceState != null) {
            DatePickerDialog dpd = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag(DATEPICKER_TAG);
            if (dpd != null) {
                dpd.setOnDateSetListener(this);
            }

            TimePickerDialog tpd = (TimePickerDialog) getSupportFragmentManager().findFragmentByTag(TIMEPICKER_TAG);
            if (tpd != null) {
                tpd.setOnTimeSetListener(this);
            }
        }

    }


    /**
     * 弹出颜色选择的小窗口
     * @param v
     */
    private void showSelectColorWindow(View v) {

        if(selectColorWindow == null){
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View selectColorView = inflater.inflate(R.layout.layout_select_color, null);

            // 获取 View 的宽高
            int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
            int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
            selectColorView.measure(w, h);
            int height = selectColorView.getMeasuredHeight();
            int width = selectColorView.getMeasuredWidth();

            // 4种颜色块 提供选择
            View b = selectColorView.findViewById(R.id.blue);
            View y = selectColorView.findViewById(R.id.yellow);
            View r = selectColorView.findViewById(R.id.red);
            View g = selectColorView.findViewById(R.id.green);

            b.setOnClickListener(this);
            y.setOnClickListener(this);
            r.setOnClickListener(this);
            g.setOnClickListener(this);

            selectColorWindow = new PopupWindow(selectColorView,width,height);
        }

        selectColorWindow.setFocusable(true);
        selectColorWindow.setOutsideTouchable(true);
        selectColorWindow.setAnimationStyle(R.style.AnimationFade);

        selectColorWindow.setBackgroundDrawable(new BitmapDrawable());

        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        int xPos = windowManager.getDefaultDisplay().getWidth() - selectColorWindow.getWidth();

        selectColorWindow.showAsDropDown(v, 0, 10);

    }


    /**
     * 弹出选择提醒日期的Dialog
     */
    private void showCustomView() {

        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("选择提醒的日期和时间")
                .customView(R.layout.dialog_select_alarm_clock, true)
                .positiveText("保存")
                .negativeText("取消")
                .neutralText("放弃")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        cancelAlarmReceiver();  // 先取消上一个闹钟

                        //  保存 闹钟 的状态和时间日期
                        note.setNoteType(BaseNote.NOTE_TYPE_REMIND);    // 修改NOTE 的类型为 提醒
                        note.setAlarmState(BaseNote.NOTE_ALARM_STATE_VALID);    // 修改闹钟状态为 有效
                        note.setAlarmDate(alarmDate.getText().toString() + " " + alarmTime.getText().toString());
                        openAlarmLayout();

                        String[] ymd = alarmDate.getText().toString().split("-");
                        String[] hms = alarmTime.getText().toString().split(":");
                        setAlarmReceiver(ymd[0],ymd[1],ymd[2],hms[0],hms[1]);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        note.setNoteType(BaseNote.NOTE_TYPE_RECORD);    // 修改NOTE 的类型为 记事
                        note.setAlarmState(BaseNote.NOTE_ALARM_STATE_INVALID);
                        closeAlarmLayout();
                        cancelAlarmReceiver();
                    }
                }).build();

        // 设置弹出的自定义窗口中的控件的属性和事件
        setAlarmText(dialog);

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        saveNoteState();
    }

    /**
     * 设置dialog 中显示的提醒时间
     * @param dialog
     */
    private void setAlarmText(MaterialDialog dialog) {

        alarmDate = (TextView) dialog.getCustomView().findViewById(R.id.alarm_date_tv);
        alarmTime = (TextView) dialog.getCustomView().findViewById(R.id.alarm_time_tv);

        if( (note.getAlarmState() == BaseNote.NOTE_ALARM_STATE_INVALID) || (note.getAlarmState() == BaseNote.NOTE_ALARM_STATE_OLD) ) {
            alarmDate.setText(TimeUtils.getCurrentTimeInString(TimeUtils.DEFAULT_DATE_FORMAT));
            alarmTime.setText(TimeUtils.getCurrentTimeInString(new SimpleDateFormat("HH:mm")));
        }else if (note.getAlarmState() == BaseNote.NOTE_ALARM_STATE_VALID){
            String[] dates = note.getAlarmDate().split(" ");
            alarmDate.setText(dates[0]);
            alarmTime.setText(dates[1]);
        }

        alarmDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.setVibrate(true);
                datePickerDialog.setYearRange(1985, 2028);
                datePickerDialog.setCloseOnSingleTapDay(false);
                datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
            }
        });

        alarmTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.setVibrate(true);
                timePickerDialog.setCloseOnSingleTapMinute(false);
                timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);
            }
        });

    }

    /**
     * 弹出选择插入图片的小窗口
     * @param v
     */
    private void showInsertPicWindow(View v) {

        if(insertPicWindow == null){
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View insertPicView = inflater.inflate(R.layout.layout_insert_pic, null);

            // 获取 View 的宽高
            int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            insertPicView.measure(w, h);
            int height = insertPicView.getMeasuredHeight();
            int width = insertPicView.getMeasuredWidth();

            insertPicView.findViewById(R.id.take_photo_tv).setOnClickListener(this);
            insertPicView.findViewById(R.id.album_tv).setOnClickListener(this);

            insertPicWindow = new PopupWindow(insertPicView,width,height);
        }

        insertPicWindow.setFocusable(true);
        insertPicWindow.setOutsideTouchable(true);
        insertPicWindow.setAnimationStyle(R.style.AnimationFade);
        insertPicWindow.setBackgroundDrawable(new BitmapDrawable());

        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int xPos = windowManager.getDefaultDisplay().getWidth() - insertPicWindow.getWidth();

        insertPicWindow.showAsDropDown(v, 0, 10);

    }


    /**
     * 展开图片列表
     */
    private void showImageList(){

        ObjectAnimator scaleYanimator =  ObjectAnimator.ofFloat(imageListRecyclerView, "scaleY", 0,1);
        ObjectAnimator.ofFloat(imageListRecyclerView, "pivotY", 0);
        scaleYanimator.setDuration(500);
        scaleYanimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                imageListRecyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                hideImageList.setVisibility(View.VISIBLE);
                showImageList.setVisibility(View.GONE);
                imageListStateTxt.setText("隐藏图片列表");

            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        scaleYanimator.start();
    }

    /**
     * 收起图片列表的动画
     */
    private void hideImageList(){

        ObjectAnimator scaleYanimator =  ObjectAnimator.ofFloat(imageListRecyclerView, "scaleY", 1, 0);
        ObjectAnimator.ofFloat(imageListRecyclerView, "pivotY", 0);
        scaleYanimator.setDuration(500);
        scaleYanimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                hideImageList.setVisibility(View.GONE);
                showImageList.setVisibility(View.VISIBLE);
                imageListStateTxt.setText("显示图片列表");

                imageListRecyclerView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        scaleYanimator.start();
    }


    private void takePhotoFromCamera() {

        String SDCardRoot = Environment.getExternalStorageDirectory().getPath() + File.separator;
        File fileDir = new File(SDCardRoot + "jideNote" + File.separator + "img" + File.separator);
        if (!fileDir.exists()) {
            fileDir.mkdir();  //如果不存在则创建
        }
        File file = new File(fileDir,ImageUtils.getTempFileName()+".jpg");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        photoUri = Uri.fromFile(file);
        LogUtils.e("get photoUri = " + photoUri.toString());

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(cameraIntent, REQ_TAKE_PHOTO);
    }

    private void pickPhotoFromAlbum(){

        Intent albumIntent = new Intent();
        albumIntent.setAction(Intent.ACTION_PICK);
        albumIntent.setType("image/*");

        startActivityForResult(albumIntent, REQ_PICK_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        LogUtils.e("requestCode = " + requestCode);
        switch (requestCode){
            case REQ_PICK_PHOTO:
                if(intent != null){
                    Uri uri = intent.getData();
                    LogUtils.e("REQ_PICK_PHOTO==",uri.toString());
                    note.addImageSet(uri);
                    initImageListData();
                    initRecyclerView();
//                    adapter.addItem(uri,imageUriList.size());
//                    showImageListLayout();
                }
                break;
            case REQ_SHOW_IMAGE:
                if(intent != null){
                    String uriString = intent.getStringExtra("UriString");
                    note.setImageSet(uriString);
                    initImageListData();
                    initRecyclerView();
                }
                break;
            case REQ_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    LogUtils.e("Result REQ_TAKE_PHOTO = " + photoUri.toString());

                    note.addImageSet(photoUri);
                    initImageListData();
                    initRecyclerView();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 保存当前note 到数据库
     */
    private void saveNoteState(){
        note.setContent(contentEt.getText().toString());
        note.setLastChangeDate(TimeUtils.getCurrentTimeInString());
        note.setBgColor(bgColor);
        note.save();
        LogUtils.e("Sava Note");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                exitNoteEditor();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveNoteState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyActivityManager.getInstance().finishActivity(this.getClass());
    }

    /**
     * 退出编辑器Activity
     */
    private void exitNoteEditor(){

        String contentStr = contentEt.getText().toString();
        if(!contentStr.equals("")) {
            saveNoteState();
            setResult(RESULT_OK);
        }else{
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    /**
     * 开启一个闹钟
     */
    private void setAlarmReceiver(String year,String month,String day,String hour,String minute ){

        Intent receiverIntent = new Intent(getApplicationContext(), NoteAlarmReceiver.class);
        long dbId = note.getId();
        LogUtils.e(TAG,"dbId ==> "+dbId);

        receiverIntent.putExtra(BaseNote.KEY_DB_ID,dbId );

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), note.getAlarmReqCode(), receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(year), Integer.parseInt(month)-1, Integer.parseInt(day), Integer.parseInt(hour), Integer.parseInt(minute));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        LogUtils.e("YEAR MONTH DAY "+calendar.get(Calendar.YEAR)+"-"+calendar.get(Calendar.MONTH)+"-"+calendar.get(Calendar.DAY_OF_MONTH));
        LogUtils.e("HOUR MINUTE "+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE));

        long time = calendar.getTimeInMillis();
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
    }

    /**
     * 取消闹钟
     */
    private void cancelAlarmReceiver(){
        Intent intent = new Intent(getApplicationContext(), NoteAlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), note.getAlarmReqCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        /* 获取闹钟管理的实例 */
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        /* 取消 */
        am.cancel(pendingIntent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_img:     // 退出
                exitNoteEditor();
                break;
            case R.id.note_palette_img:     // 调节背景颜色
                showSelectColorWindow(v);
                break;
            case R.id.blue:
                setBgColor(BaseNote.LIGHT_BLUE);    // 设置背景颜色为 蓝
                selectColorWindow.dismiss();
                break;
            case R.id.yellow:
                setBgColor(BaseNote.LIGHT_YELLOW);  // 设置背景颜色为 黄
                selectColorWindow.dismiss();
                break;
            case R.id.red:
                setBgColor(BaseNote.LIGHT_RED);     // 设置背景颜色为 红
                selectColorWindow.dismiss();
                break;
            case R.id.green:
                setBgColor(BaseNote.LIGHT_GREEN);   // 设置背景颜色为 绿
                selectColorWindow.dismiss();
                break;

            case R.id.note_alarm_clock_img:         // 设置闹钟
                showCustomView();
                break;
            case R.id.note_added_img:               // 添加附件（图片）
                pickPhotoFromAlbum();
//                showInsertPicWindow(v);   // 弹出popupWindow ，选择多种图片来源（暂时不开放）
                break;
            case R.id.note_image_tip_tv:            // 图片列表下方的文字，根据状态来改变图片列表的显示和隐藏
                if(imageListState == SHOW){
                    hideImageList();
                    imageListState = HIDE;
                }else {
                    showImageList();
                    imageListState = SHOW;
                }
                break;
//            case R.id.note_image_hide_img:
//                hideImageList();
//                break;
//            case R.id.note_image_show_img:
//                showImageList();
//                break;
            case R.id.take_photo_tv:                //拍照获取图片（该功能暂时屏蔽）
//                takePhotoFromCamera();
                insertPicWindow.dismiss();
                break;
            case R.id.album_tv:                     // 打开相册-选择图片
                pickPhotoFromAlbum();
                insertPicWindow.dismiss();
                break;
            case R.id.close_alarm_img:              // 闹钟状态栏的关闭按钮
                cancelAlarmReceiver();
                note.setNoteType(BaseNote.NOTE_TYPE_RECORD);    // 修改NOTE 的类型为 记事
                note.setAlarmState(BaseNote.NOTE_ALARM_STATE_INVALID);
                note.save();
                closeAlarmLayout();
                break;
            default:
                break;

        }
    }

    /**
     * 选择Alarm 日期时间的 回调方法
     * @param datePickerDialog  控件
     * @param year  选择的年份
     * @param month 选择的月份
     * @param day   选择的日期
     */
    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        int realMonth = month+1;
        alarmDate.setText(year+"-"+realMonth+"-"+day);
    }

    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
        alarmTime.setText(hourOfDay+":"+minute);
    }
}
