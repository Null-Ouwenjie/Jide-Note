package com.ouwenjie.note.model;

import android.net.Uri;

import com.ouwenjie.note.utils.TimeUtils;

import cn.bmob.v3.BmobObject;

/**
 * 基础的 note 类，具有
 * 内容
 * 创建的时间
 * 最后修改的时间
 * 背景颜色
 * note类型
 * 是否开启提醒功能
 * 提醒时间日期
 * ···
 * Created by 文杰 on 2015/2/27.
 */
public class CloudNote extends BmobObject implements Comparable {

    public static final int LIGHT_YELLOW = 0;
    public static final int LIGHT_RED = 1;
    public static final int LIGHT_GREEN = 2;
    public static final int LIGHT_BLUE = 3;

    public static final String KEY_DB_ID = "DB_ID";
    public static final String KEY_NOTE_TYPE = "NOTE_TYPE";
    public static final String KEY_CREATE_DATE = "CREATE_DATE";
    public static final String KEY_CREATE_TIME = "CREATE_TIME";
    public static final String KEY_LAST_CHANGE_DATE = "LAST_CHANGE_DATE";

    public static final int NOTE_TYPE_RECORD = 100;
    public static final int NOTE_TYPE_LIST = 101;
    public static final int NOTE_TYPE_REMIND = 102;

    public static final int NOTE_ALARM_STATE_VALID = 10;
    public static final int NOTE_ALARM_STATE_INVALID = 11;
    public static final int NOTE_ALARM_STATE_OLD = 12;
    public static final int NOTE_STATE_VALID = 13;
    public static final int NOTE_STATE_INVALID = 14;
    public static final int NOTE_STATE_DELETE = 15;

    public static final int REQ_NEW_NOTE = 1000;
    public static final int REQ_NEW_NOTE_WITH_ALARM = 1001;
    public static final int REQ_NOTE_IN_ARCHIVE = 1002;

    private String content;         // 笔记内容
    private String createDate;      // 创建日期
    private String lastChangeDate;  // 最后的修改时间
    private int bgColor;            // 笔记的背景颜色

    private int noteType;           // 笔记的类型
    private int noteState;          // 笔记的状态

    private int alarmState;         // 闹钟的状态
    private String alarmDate;       // 闹钟的日期

    private String imageSet;        // 图片的Uri.toString 的集合，以空格分隔

    public CloudNote(){}

    public CloudNote(String content){

        this.content = content;
        this.createDate = TimeUtils.getCurrentTimeInString();
        lastChangeDate = TimeUtils.getCurrentTimeInString();
        bgColor = LIGHT_YELLOW;

        this.noteType = NOTE_TYPE_RECORD;
        noteState = NOTE_STATE_VALID;

        alarmState = NOTE_ALARM_STATE_INVALID;
        alarmDate = "";

        imageSet = "";
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getLastChangeDate() {
        return lastChangeDate;
    }

    public void setLastChangeDate(String lastChangeDate) {
        this.lastChangeDate = lastChangeDate;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public int getNoteType() {
        return noteType;
    }

    public void setNoteType(int noteType) {
        this.noteType = noteType;
    }


    public int getAlarmState() {
        return alarmState;
    }

    public void setAlarmState(int alarmState) {
        this.alarmState = alarmState;
    }

    public String getAlarmDate() {
        return alarmDate;
    }

    public void setAlarmDate(String alarmDate) {
        this.alarmDate = alarmDate;
    }


    public String getImageSet() {
        return imageSet;
    }

    public void setImageSet(String imageSet) {
        this.imageSet = imageSet;
    }

    /**
     * 添加笔记内的图片列表
     * @param uri 图片的uri
     */
    public void addImageSet(Uri uri){
        if(uri==null||uri.toString().equals("")){
            return;
        }
        if(imageSet.length() == 0){
            imageSet = uri.toString();
        }else {
            imageSet = imageSet + " " + uri.toString();
        }
    }

    public void addImageSet(String string){
        if(string==null||string.equals("")){
            return;
        }
        if(imageSet.length() == 0){
            imageSet = string;
        }else {
            imageSet = imageSet + " " + string;
        }

    }


    public int getNoteState() {
        return noteState;
    }

    public void setNoteState(int noteState) {
        this.noteState = noteState;
    }

    public int getAlarmReqCode(){
        if(!getAlarmDate().equals("")) {
            String[] alarmDateStr = getAlarmDate().split(" ");
            String[] ymd = alarmDateStr[0].split("-");
            String[] hm = alarmDateStr[1].split(":");
            return Integer.parseInt(ymd[0]) * Integer.parseInt(ymd[1]) * Integer.parseInt(ymd[2]) * Integer.parseInt(hm[0]) * Integer.parseInt(hm[1]);
        }
        return 0;
    }

    @Override
    public String toString() {
        return "BaseNote{" +
                "content='" + content + '\'' +
                ", lastChangeDate='" + lastChangeDate + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CloudNote baseNote = (CloudNote) o;

        if (bgColor != baseNote.bgColor) return false;
        if (!content.equals(baseNote.content)) return false;
        if (createDate != null ? !createDate.equals(baseNote.createDate) : baseNote.createDate != null)
            return false;
        if (lastChangeDate != null ? !lastChangeDate.equals(baseNote.lastChangeDate) : baseNote.lastChangeDate != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = content.hashCode();
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        result = 31 * result + (lastChangeDate != null ? lastChangeDate.hashCode() : 0);
        result = 31 * result + bgColor;
        return result;
    }

    @Override
    public int compareTo(Object another) {
        if(another instanceof CloudNote){
            CloudNote note = (CloudNote) another;
            if(this.lastChangeDate.compareTo(((CloudNote) another).lastChangeDate) > 0){
                return -1;
            }else if(this.lastChangeDate.compareTo(((CloudNote) another).lastChangeDate) < 0){
                return 1;
            }
        }
        return 0;
    }

}
