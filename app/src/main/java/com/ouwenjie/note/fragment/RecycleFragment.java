package com.ouwenjie.note.fragment;

import android.app.Activity;
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
import com.ouwenjie.note.NoteApplication;
import com.ouwenjie.note.R;
import com.ouwenjie.note.activity.MainActivity;
import com.ouwenjie.note.adapter.RecycleAdapter;
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
public class RecycleFragment extends Fragment implements ScreenShotable, View.OnClickListener, RecycleAdapter.OnItemClickListener, RecycleAdapter.OnItemLongClickListener {

    // 切换动画
    private View containerView;
    private Bitmap bitmap;

    private Activity activity;

    private RecyclerView recyclerView;
    private RecycleAdapter adapter;
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
    public static RecycleFragment newInstance() {
        RecycleFragment fragment = new RecycleFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public RecycleFragment() {
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
        MobclickAgent.onPageStart("RecycleFragment"); //统计页面
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("RecycleFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recycle, container, false);
        fab = (ButtonFloat) view.findViewById(R.id.new_edit_fab);
        fab.setOnClickListener(this);
        fab.setVisibility(View.GONE);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_recyclerview);
        initRecyclerView();

        LogUtils.e("onCreateView");
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

    public void initRecyclerView() {
//        layoutManager = new LinearLayoutManager(activity);        // 创建一个线性布局管理器
//        mRecyclerView.setHasFixedSize(true);        // 提高性能（假如你能确定当你的内容改变时，item布局的大小不会改变时，可使用该方法提高性能）
//        recyclerView.setLayoutManager(layoutManager);        // 设置布局管理器

        layoutManager = new GridLayoutManager(activity,2);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RecycleAdapter(activity,myDataset());
        adapter.setOnItemClickListener(this);
        adapter.setOnItemLongClickListener(this);
        recyclerView.setAdapter(adapter);

        recyclerView.setItemAnimator(new SlideInLeftAnimator());

        recyclerView.getItemAnimator().setAddDuration(300);
        recyclerView.getItemAnimator().setRemoveDuration(300);
        recyclerView.getItemAnimator().setMoveDuration(300);
        recyclerView.getItemAnimator().setChangeDuration(300);
    }

    private List<BaseNote> myDataset() {
        TreeSet<BaseNote> allNote = ((MainActivity) activity).getNoteSet();
        for (BaseNote anAllNote : allNote) {
            // 获取删除状态的NOTE
            if (anAllNote.getNoteState()==BaseNote.NOTE_STATE_DELETE) {
                noteList.add(anAllNote);
            }
        }
        return noteList;
    }

    public RecycleAdapter getAdapter(){
        return adapter;
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
            RecycleFragment.this.bitmap = bitmap;
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

    }

    @Override
    public void onItemClick(String dbID) {
        // 无法修改回收站里的NOTE
//        BaseNote note = dbHelper.get(Long.valueOf(dbID));
//
//        if(note != null) {
//            Intent editIntent = new Intent(activity, EditNoteActivity.class);
//            editIntent.putExtra(BaseNote.KEY_DB_ID, note.getId());
//
//            adapter.removeItem(note);
//            activity.startActivityForResult(editIntent, BaseNote.REQ_NEW_NOTE);
//        }
    }

    @Override
    public void onItemLongClick(final String dbID) {
        MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .title("恢复或清除？")
                .positiveText("恢复")
                .negativeText("清除")
                .neutralText("取消")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        BaseNote note = dbHelper.get(Long.valueOf(dbID));
                        note.setNoteState(BaseNote.NOTE_STATE_VALID);   // 恢复NOTE 为正常状态
                        note.save();
                        adapter.removeItem(note);
                        // Note 从 列表和数据库中删除后，需要更新 Application 和 Activity 中保存的 NoteSet ，避免再次显示。
                        refreshData();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
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
                    }
                }).build();

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void refreshData() {
        NoteApplication application = (NoteApplication) (activity.getApplication());
        application.updateNoteList();
        ((MainActivity) activity).setNoteSet(application.getNoteSet());
    }
}
