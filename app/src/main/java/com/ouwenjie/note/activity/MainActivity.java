package com.ouwenjie.note.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;

import com.ouwenjie.note.NoteApplication;
import com.ouwenjie.note.R;
import com.ouwenjie.note.fragment.ContentFragment;
import com.ouwenjie.note.model.BaseNote;
import com.ouwenjie.note.utils.LogUtils;
import com.ouwenjie.note.utils.MyActivityManager;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import yalantis.com.sidemenu.interfaces.Resourceble;
import yalantis.com.sidemenu.interfaces.ScreenShotable;
import yalantis.com.sidemenu.model.SlideMenuItem;
import yalantis.com.sidemenu.util.ViewAnimator;


public class MainActivity extends ActionBarActivity implements ViewAnimator.ViewAnimatorListener {

    public static final String CLOSE = "Close";
    public static final String MAIN = "记得";
    public static final String RECORD = "记事";
//    public static final String LIST = "清单";       // 暂时未开发
    public static final String REMIND = "提醒";
    public static final String ARCHIVE = "已归档";
    public static final String RECYCLE = "回收站";
//    public static final String ACCOUNT = "账号";
    public static final String SETTINGS = "设置";

    private DrawerLayout drawerLayout;      // 整个布局框架
    private ActionBarDrawerToggle drawerToggle;
    private List<SlideMenuItem> menuItemList = new ArrayList<>();   // 左侧 Menu 列表
    private ViewAnimator viewAnimator;      // 页面切换动画
    private LinearLayout leftlayout;        // 左侧 Menu

    private TreeSet<BaseNote> noteSet = new TreeSet<>();    //笔记集合

    private ContentFragment contentFragment;

    private ContentFragment recordFragment;
    private ContentFragment listFragment;      // 未开发
    private ContentFragment remindFragment;
    private ContentFragment archiveFragment;
    private ContentFragment recycleFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyActivityManager.getInstance().addActivity(this);

        noteSet = ((NoteApplication)getApplication()).getNoteSet();
        recordFragment = ContentFragment.newInstance(ContentFragment.RECORD);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame,recordFragment)
                .commit();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setScrimColor(Color.TRANSPARENT);
        leftlayout = (LinearLayout) findViewById(R.id.left_drawer);
        leftlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawers();
            }
        });

        setToolBar();
        setTitle(RECORD);

        createMenuList();

        viewAnimator = new ViewAnimator<>(MainActivity.this, menuItemList, recordFragment, drawerLayout, this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);// 淡化status bar 和 navigation bar

        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyActivityManager.getInstance().finishActivity(this.getClass());

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case BaseNote.REQ_NEW_NOTE:
                if(resultCode == RESULT_OK) {   // 用户新建了一个记录，重新拿一遍数据库，将最新添加的记录传递到Fragment，更新列表。
                    updateNoteSet();
                    Iterator<BaseNote> iterator = noteSet.iterator();
                    recordFragment.getAdapter().addItem(iterator.next(),0);
                }
                LogUtils.e("noteList Size == "+noteSet.size()+"");
                break;

            case BaseNote.REQ_NOTE_IN_ARCHIVE:
                if(resultCode == RESULT_OK) {   // 用户新建了一个记录，重新拿一遍数据库，将最新添加的记录传递到Fragment，更新列表。
                    updateNoteSet();
                    Iterator<BaseNote> iterator = noteSet.iterator();
                    archiveFragment.getAdapter().addItem(iterator.next(),0);
                }
                LogUtils.e("noteList Size == "+noteSet.size()+"");
                break;

            case BaseNote.REQ_NEW_NOTE_WITH_ALARM:
                if(resultCode == RESULT_OK) {   // 用户新建了一个记录，重新拿一遍数据库，将最新添加的记录传递到Fragment，更新列表。
                    updateNoteSet();
                    Iterator<BaseNote> iterator = noteSet.iterator();
                    BaseNote note = iterator.next();
                    if(note.getAlarmState()==BaseNote.NOTE_ALARM_STATE_VALID) {
                        remindFragment.getAdapter().addItem(note, 0);
                    }
                }
                LogUtils.e("noteList Size == "+noteSet.size()+"");
                break;
        }
    }

    private void updateNoteSet() {
        NoteApplication application = (NoteApplication) (this.getApplication());
        application.updateNoteList();
        noteSet = application.getNoteSet();
    }

    public TreeSet<BaseNote> getNoteSet() {
        return noteSet;
    }

    public void setNoteSet(TreeSet<BaseNote> noteSet) {
        this.noteSet = noteSet;
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Drawer 的监听
        drawerToggle = new ActionBarDrawerToggle(
                MainActivity.this,                  /* host Activity */
                drawerLayout,           /* DrawerLayout object */
                toolbar,                /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,   /* "open drawer" description */
                R.string.drawer_close   /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                leftlayout.removeAllViews();
                leftlayout.invalidate();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if (slideOffset > 0.6 && leftlayout.getChildCount() == 0)
                    viewAnimator.showMenuContent();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);
    }

    /**
     * 创建侧滑菜单的列表
     */
    private void createMenuList() {
        SlideMenuItem menuItem0 = new SlideMenuItem(CLOSE, R.drawable.icn_close);
        menuItemList.add(menuItem0);
        SlideMenuItem menuItem1 = new SlideMenuItem(RECORD, R.drawable.icon_cn_record_104);
        menuItemList.add(menuItem1);
//        SlideMenuItem menuItem2 = new SlideMenuItem(LIST, R.drawable.icon_cn_list_104);
//        menuItemList.add(menuItem2);
        SlideMenuItem menuItem3 = new SlideMenuItem(REMIND, R.drawable.icon_cn_remind_104);
        menuItemList.add(menuItem3);
        SlideMenuItem menuItem4 = new SlideMenuItem(ARCHIVE,R.drawable.icon_cn_archive_104);
        menuItemList.add(menuItem4);
        SlideMenuItem menuItem5 = new SlideMenuItem(RECYCLE,R.drawable.icon_cn_recycle_104);
        menuItemList.add(menuItem5);
//        SlideMenuItem menuItem6 = new SlideMenuItem(ACCOUNT,R.drawable.icon_cn_account_104);
//        menuItemList.add(menuItem6);
        SlideMenuItem menuItem7 = new SlideMenuItem(SETTINGS, R.drawable.icon_cn_settings_104);
        menuItemList.add(menuItem7);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //该方法会自动和actionBar关联, 将开关的图片显示在了action上，如果不设置，也可以有抽屉的效果，不过是默认的图标
        drawerToggle.syncState();
    }

    /**
     * 设置改变时
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    /**
     * 创建Menu按键的菜单
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("搜索笔记内容");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                LogUtils.e("onQueryTextSubmit  ==  " + query);
                queryNote(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                LogUtils.e("onQueryTextChange  ==  " + newText);
                if (newText.equals("")) {
                    resumeNote();
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) { // 先交给 ActionBarDrawerToggle 处理
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_search:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * 根据条件搜索Note
     * @param query
     */
    private void queryNote(String query) {
        switch (getTitle().toString()){
            case RECORD:
                recordFragment.getAdapter().searchNotes(query);
                break;
//            case LIST:
//                break;
            case REMIND:
                remindFragment.getAdapter().searchNotes(query);
                break;
            case ARCHIVE:
                archiveFragment.getAdapter().searchNotes(query);
                break;
            case RECYCLE:
                recycleFragment.getAdapter().searchNotes(query);
                break;
            case SETTINGS:
                break;
            default:
                break;
        }
    }

    /**
     * 去除搜索结果，恢复原本列表
     */
    private void resumeNote(){
        switch (getTitle().toString()){
            case RECORD:
                recordFragment.getAdapter().removeAllItem();
                recordFragment.initRecyclerView();
                break;
//            case LIST:
//                break;
            case REMIND:
                remindFragment.getAdapter().removeAllItem();
                remindFragment.initRecyclerView();
                break;
            case ARCHIVE:
                archiveFragment.getAdapter().removeAllItem();
                archiveFragment.initRecyclerView();
                break;
            case RECYCLE:
                recycleFragment.getAdapter().removeAllItem();
                recycleFragment.initRecyclerView();
                break;
            case SETTINGS:
                break;
            default:
                break;
        }
    }


    /**
     * 切换Fragment
     * @param screenShotable
     * 一个实现了 ScreenShotable 接口的 Fragment 对象
     * @param topPosition
     * @return
     */
    private ScreenShotable replaceFragment(Resourceble slideMenuItem,ScreenShotable screenShotable, int topPosition) {
        View view = findViewById(R.id.content_frame);
        int finalRadius = Math.max(view.getWidth(), view.getHeight());
        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(view, 0, topPosition, 0, finalRadius);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(ViewAnimator.CIRCULAR_REVEAL_ANIMATION_DURATION);

        findViewById(R.id.content_overlay).setBackgroundDrawable(new BitmapDrawable(getResources(), screenShotable.getBitmap()));
        animator.start();

        switch (slideMenuItem.getName()){

            case RECORD:
                recordFragment = ContentFragment.newInstance(ContentFragment.RECORD);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, recordFragment).commit();
                return recordFragment;
//            case LIST:
//                listFragment = ListFragment.newInstance();
//                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, listFragment).commit();
//                return listFragment;
            case REMIND:
                remindFragment = ContentFragment.newInstance(ContentFragment.REMIND);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, remindFragment).commit();
                return remindFragment;
            case ARCHIVE:
                archiveFragment = ContentFragment.newInstance(ContentFragment.ARCHIVE);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,archiveFragment).commit();
                return archiveFragment;
            case RECYCLE:
                recycleFragment = ContentFragment.newInstance(ContentFragment.RECYCLE);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,recycleFragment).commit();
                return recycleFragment;

            default:
                break;

        }
        return recordFragment;
    }

    /**
     * implements ViewAnimator.ViewAnimatorListener 所需实现的4个方法
     */
    /**
     * @param slideMenuItem
     * @param screenShotable
     * @param position
     * @return
     */
    @Override
    public ScreenShotable onSwitch(Resourceble slideMenuItem, ScreenShotable screenShotable, int position) {
        switch (slideMenuItem.getName()) {
            case CLOSE:
                return screenShotable;
            default:
                if(slideMenuItem.getName().equals(SETTINGS)){
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                    return screenShotable;
                }else {
                    setTitle(slideMenuItem.getName());
                    return replaceFragment(slideMenuItem, screenShotable, position);
                }
        }
    }

    @Override
    public void disableHomeButton() {
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    @Override
    public void enableHomeButton() {
        getSupportActionBar().setHomeButtonEnabled(true);
        drawerLayout.closeDrawers();

    }

    @Override
    public void addViewToContainer(View view) {
        leftlayout.addView(view);
    }

}

