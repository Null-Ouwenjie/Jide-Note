package com.ouwenjie.note.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.views.ButtonFloat;
import com.ouwenjie.note.R;
import com.ouwenjie.note.activity.EditNoteActivity;
import com.ouwenjie.note.activity.MainActivity;
import com.ouwenjie.note.adapter.BaseNoteAdapter;
import com.ouwenjie.note.db.NoteDatabaseHelper;
import com.ouwenjie.note.model.BaseNote;
import com.ouwenjie.note.utils.LogUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import yalantis.com.sidemenu.interfaces.ScreenShotable;

/**
 * Create at 2015年2月26日17:52:09 By ouwenjie
 */
public class ContentFragment extends Fragment implements View.OnClickListener ,ScreenShotable,
        BaseNoteAdapter.OnItemClickListener, BaseNoteAdapter.OnItemLongClickListener {

    public static final String KEY_CONTENT_TYPE = "Content_Type";
    public static final String RECORD = "Record";
    public static final String REMIND = "Remind";
    public static final String ARCHIVE = "Archive";
    public static final String RECYCLE = "Recycle";

    public String Content_Type = "";

    // 切换动画 所需的两个成员
    private View containerView;
    private Bitmap bitmap;

    private Activity activity;

    private RecyclerView recyclerView;
    private BaseNoteAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ButtonFloat fab;

    private List<BaseNote> noteList = new ArrayList<>();

    private NoteDatabaseHelper dbHelper = new NoteDatabaseHelper();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment MemoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContentFragment newInstance(String type) {
        ContentFragment fragment = new ContentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_CONTENT_TYPE,type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
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
            Content_Type = getArguments().getString(KEY_CONTENT_TYPE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        umengPageStart();
        switchFabState();
    }

    @Override
    public void onPause() {
        super.onPause();
        umengPageEnd();

    }

    private void umengPageStart() {
        switch (Content_Type){
            case RECORD:
                MobclickAgent.onPageStart("RecordFragment"); //统计页面
                break;
            case REMIND:
                MobclickAgent.onPageStart("RemindFragment"); //统计页面
                break;
            case ARCHIVE:
                MobclickAgent.onPageStart("ArchiveFragment"); //统计页面
                break;
            case RECYCLE:
                MobclickAgent.onPageStart("RecycleFragment"); //统计页面
                break;
            default:
                break;
        }
    }

    private void umengPageEnd() {
        switch (Content_Type){
            case RECORD:
                MobclickAgent.onPageEnd("RecordFragment"); //统计页面
                break;
            case REMIND:
                MobclickAgent.onPageEnd("RemindFragment"); //统计页面
                break;
            case ARCHIVE:
                MobclickAgent.onPageEnd("ArchiveFragment"); //统计页面
                break;
            case RECYCLE:
                MobclickAgent.onPageEnd("RecycleFragment"); //统计页面
                break;
            default:
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_content, container, false);

        fab = (ButtonFloat) view.findViewById(R.id.new_edit_fab);
        fab.setOnClickListener(this);

        recyclerView = (RecyclerView) view.findViewById(R.id.content_recyclerview);
        initRecyclerView();

        LogUtils.e("onCreateView");
        return view;
    }

    private void switchFabState() {
        switch (Content_Type){
            case RECORD:
                fab.setVisibility(View.VISIBLE);
                break;
            case REMIND:
                fab.setVisibility(View.VISIBLE);
                break;
            case ARCHIVE:
                fab.setVisibility(View.GONE);
                break;
            case RECYCLE:
                fab.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    public void clearNoteList(){
        noteList.clear();
    }

    public void initRecyclerView() {

        layoutManager = new GridLayoutManager(activity,2);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new BaseNoteAdapter(activity,myDataset());
        adapter.setOnItemClickListener(this);
        adapter.setOnItemLongClickListener(this);
        recyclerView.setAdapter(adapter);

        recyclerView.setItemAnimator(new SlideInLeftAnimator());

        recyclerView.getItemAnimator().setAddDuration(300);
        recyclerView.getItemAnimator().setRemoveDuration(300);
        recyclerView.getItemAnimator().setMoveDuration(300);
        recyclerView.getItemAnimator().setChangeDuration(300);
    }

    public List<BaseNote> myDataset() {
        TreeSet<BaseNote> allNote = ((MainActivity) activity).getNoteSet();
        switch (Content_Type){
            case RECORD:
                for (BaseNote anNote : allNote) {
                    if ((anNote.getNoteType() == BaseNote.NOTE_TYPE_RECORD || anNote.getNoteType() == BaseNote.NOTE_TYPE_REMIND)
                            && (anNote.getNoteState()==BaseNote.NOTE_STATE_VALID)) {
                        noteList.add(anNote);
                    }
                }
                break;
            case REMIND:
                for (BaseNote anNote : allNote) {
                    // 获取当前状态为‘有效的’ 和 ‘提醒有效期的’的note
                    if (anNote.getAlarmState() == BaseNote.NOTE_ALARM_STATE_VALID && (anNote.getNoteState()==BaseNote.NOTE_STATE_VALID)) {
                        noteList.add(anNote);
                    }
                }
                break;
            case ARCHIVE:
                for (BaseNote anNote : allNote) {
                    if (anNote.getNoteState()==BaseNote.NOTE_STATE_INVALID) {
                        noteList.add(anNote);
                    }
                }
                break;
            case RECYCLE:
                for (BaseNote anNote : allNote) {
                    // 获取删除状态的NOTE
                    if (anNote.getNoteState()==BaseNote.NOTE_STATE_DELETE) {
                        noteList.add(anNote);
                    }
                }
                break;
            default:
                break;
        }
        return noteList;
    }

    public BaseNoteAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(BaseNoteAdapter adapter) {
        this.adapter = adapter;
    }

    /**
     * 菜单栏转换栏目时的动画效果所需的方法之一
     */
    @Override
    public void takeScreenShot() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Bitmap bitmap = Bitmap.createBitmap(containerView.getWidth(),
                        containerView.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                containerView.draw(canvas);
                ContentFragment.this.bitmap = bitmap;
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

            case R.id.new_edit_fab:
                createNewNote();
                break;
            default:
                break;
        }
    }

    private void createNewNote() {
        Intent intent = new Intent(activity, EditNoteActivity.class);
        intent.putExtra(BaseNote.KEY_DB_ID, -1);
        if(Content_Type.equals(RECORD)) {
            intent.putExtra(BaseNote.KEY_NOTE_TYPE, BaseNote.NOTE_TYPE_RECORD);
            activity.startActivityForResult(intent, BaseNote.REQ_NEW_NOTE);
        }else if(Content_Type.equals(REMIND)){
            intent.putExtra(BaseNote.KEY_NOTE_TYPE, BaseNote.NOTE_TYPE_REMIND);
            activity.startActivityForResult(intent, BaseNote.REQ_NEW_NOTE_WITH_ALARM);    // 新建一个‘提醒’类型的NOTE
        }else{

        }
    }

    // 当列表中的Item 被点击事，返回一个noteList 的 数据库的id
    @Override
    public void onItemClick(String dbID ) {
        BaseNote note = dbHelper.get(Long.valueOf(dbID));

        if(note != null) {

            Intent editIntent = new Intent(activity, EditNoteActivity.class);
            long dbId = dbHelper.getId(note);
            editIntent.putExtra(BaseNote.KEY_DB_ID, dbId);

            adapter.removeItem(note);

            switch (Content_Type) {
                case RECORD:
                    activity.startActivityForResult(editIntent, BaseNote.REQ_NEW_NOTE);
                    break;
                case REMIND:
                    activity.startActivityForResult(editIntent, BaseNote.REQ_NEW_NOTE_WITH_ALARM);
                    break;
                case ARCHIVE:
                    activity.startActivityForResult(editIntent, BaseNote.REQ_NOTE_IN_ARCHIVE);
                    break;
                default:

                    break;
            }

        }

    }


    @Override
    public void onItemLongClick(final String dbID) {
        switch(Content_Type){
            case RECORD:
                showRecordNoteDialog(dbID);
                break;
            case REMIND:
                showRemindNoteDialog(dbID);
                break;
            case ARCHIVE:
                showArchiveNoteDialog(dbID);
                break;
            case RECYCLE:
                showRecycleNoteDialog(dbID);
                break;
            default:
                break;
        }
    }

    private void showRecordNoteDialog(final String dbID) {
        MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .title("归档或删除？")
                .positiveText("归档")
                .negativeText("删除")
                .neutralText("取消")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        dialog.dismiss();
                        BaseNote note = dbHelper.get(Long.valueOf(dbID));
                        note.setNoteState(BaseNote.NOTE_STATE_INVALID); // 归档，NOTE状态为 INVALID,进入归档，并从当前列表中除去
                        dbHelper.sava(note);
//                        note.save();
                        adapter.removeItem(note);
                        // Note 从 列表和数据库中删除后，需要更新 Application 和 Activity 中保存的 NoteSet ，避免再次显示。
                        refreshData();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                        BaseNote note = dbHelper.get(Long.valueOf(dbID));
                        note.setNoteState(BaseNote.NOTE_STATE_DELETE);// 删除，NOTE 状态为 DELETE，进入回收站
                        dbHelper.sava(note);
//                        note.save();
                        adapter.removeItem(note);
                        // Note 从 列表和数据库中删除后，需要更新 Application 和 Activity 中保存的 NoteSet ，避免再次显示。
                        refreshData();
                    }

                    @Override
                    public void onNeutral(MaterialDialog dialog){
                        dialog.dismiss();
                    }
                }).build();

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void showRemindNoteDialog(final String dbID){
        MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .title("归档或删除？")
                .positiveText("归档")
                .negativeText("删除")
                .neutralText("取消")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        dialog.dismiss();
                        BaseNote note = dbHelper.get(Long.valueOf(dbID));
                        note.setNoteState(BaseNote.NOTE_STATE_INVALID);
                        dbHelper.sava(note);
//                        note.save();
                        adapter.removeItem(note);
                        // Note 从 列表和数据库中删除后，需要更新 Application 和 Activity 中保存的 NoteSet ，避免再次显示。
                        refreshData();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                        BaseNote note = dbHelper.get(Long.valueOf(dbID));
                        note.setNoteState(BaseNote.NOTE_STATE_DELETE);
                        dbHelper.sava(note);
//                        note.save();
//                        note.delete();
                        adapter.removeItem(note);
                        // Note 从 列表和数据库中删除后，需要更新 Application 和 Activity 中保存的 NoteSet ，避免再次显示。
                        refreshData();
                    }

                    @Override
                    public void onNeutral(MaterialDialog dialog){
                        dialog.dismiss();
                    }

                }).build();

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void showArchiveNoteDialog(final String dbID){

        MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .title("恢复或删除？")
                .positiveText("恢复")
                .negativeText("删除")
                .neutralText("取消")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        dialog.dismiss();
                        BaseNote note = dbHelper.get(Long.valueOf(dbID));
                        note.setNoteState(BaseNote.NOTE_STATE_VALID);   // 将归档的NOTE 恢复为 正常状态
                        dbHelper.sava(note);
//                        note.save();
                        adapter.removeItem(note);
                        // Note 从 列表和数据库中删除后，需要更新 Application 和 Activity 中保存的 NoteSet ，避免再次显示。
                        refreshData();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                        BaseNote note = dbHelper.get(Long.valueOf(dbID));
                        note.setNoteState(BaseNote.NOTE_STATE_DELETE);
                        dbHelper.sava(note);
//                        note.save();
                        adapter.removeItem(note);
                        // Note 从 列表和数据库中删除后，需要更新 Application 和 Activity 中保存的 NoteSet ，避免再次显示。
                        refreshData();
                    }

                    @Override
                    public void onNeutral(MaterialDialog dialog){
                        dialog.dismiss();
                    }
                }).build();

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void showRecycleNoteDialog(final String dbID){
        MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .title("恢复或清除？")
                .positiveText("恢复")
                .negativeText("清除")
                .neutralText("取消")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        dialog.dismiss();
                        BaseNote note = dbHelper.get(Long.valueOf(dbID));
                        note.setNoteState(BaseNote.NOTE_STATE_VALID);   // 恢复NOTE 为正常状态
                        note.save();
                        adapter.removeItem(note);
                        // Note 从 列表和数据库中删除后，需要更新 Application 和 Activity 中保存的 NoteSet ，避免再次显示。
                        refreshData();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                        BaseNote note = dbHelper.get(Long.valueOf(dbID));
//                        note.setNoteState(BaseNote.NOTE_STATE_DELETE);
//                        note.save();
                        note.delete();          // 将NOTE 从数据库中彻底删除
                        adapter.removeItem(note);
                        // Note 从 列表和数据库中删除后，需要更新 Application 和 Activity 中保存的 NoteSet ，避免再次显示。
                        refreshData();
                    }

                    @Override
                    public void onNeutral(MaterialDialog dialog){
                        dialog.dismiss();
                    }
                }).build();

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    private void refreshData() {
        ((MainActivity) activity).setNoteSet(new NoteDatabaseHelper().getAll());
    }

}
