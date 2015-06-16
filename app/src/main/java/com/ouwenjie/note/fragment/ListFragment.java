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

import com.gc.materialdesign.views.ButtonFloat;
import com.ouwenjie.note.R;
import com.ouwenjie.note.activity.MainActivity;
import com.ouwenjie.note.adapter.ListAdapter;
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
public class ListFragment extends Fragment implements ScreenShotable, View.OnClickListener, ListAdapter.OnItemClickListener, ListAdapter.OnItemLongClickListener {

    // 切换动画
    private View containerView;
    private Bitmap bitmap;

    private Activity activity;

    private RecyclerView recyclerView;
    private ListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ButtonFloat fab;

    private List<BaseNote> noteList = new ArrayList<>();

    private NoteDatabaseHelper dbHelper = new NoteDatabaseHelper();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListFragment newInstance() {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ListFragment() {
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
        MobclickAgent.onPageStart("ListFragment"); //统计页面
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ListFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        fab = (ButtonFloat) view.findViewById(R.id.new_edit_fab);
        fab.setOnClickListener(this);

        recyclerView = (RecyclerView) view.findViewById(R.id.list_recyclerview);
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
//        mLayoutManager = new LinearLayoutManager(activity);        // 创建一个线性布局管理器
//        mRecyclerView.setHasFixedSize(true);        // 提高性能（假如你能确定当你的内容改变时，item布局的大小不会改变时，可使用该方法提高性能）
//        mRecyclerView.setLayoutManager(mLayoutManager);        // 设置布局管理器

        layoutManager = new GridLayoutManager(activity,2);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ListAdapter(activity,myDataset());
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
            if (anAllNote.getNoteType()==BaseNote.NOTE_TYPE_LIST) {
                noteList.add(anAllNote);
            }
        }
        return noteList;
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
            ListFragment.this.bitmap = bitmap;
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

    }

    @Override
    public void onItemLongClick(String dbID) {

    }
}
