package com.ouwenjie.note.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ouwenjie.note.R;
import com.ouwenjie.note.model.BaseNote;
import com.ouwenjie.note.utils.ImageUtils;
import com.ouwenjie.note.utils.LogUtils;
import com.ouwenjie.note.utils.ScreenUtils;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片列表查看Activity
 * Created by 文杰 on 2015/4/17.
 */
public class ImageViewPagerActivity extends AppCompatActivity implements View.OnClickListener {

    private BaseNote note;

    private ImageView backImg;
    private TextView imgCountTv;
    private ImageView removeImg;

    private ViewPager viewPager;
    private ImageAlbumAdapter imageAlbumAdapter;

    private List<ImageView> imageViewList = new ArrayList<>();
    private List<Uri> uriList = new ArrayList<>();

    private int curPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);

        initWidget();
        getUriList();
        initImageViewList();
        curPosition = getIntent().getIntExtra("ImageListPosition",0);
        initViewPager();
    }

    private void initWidget() {
        backImg = (ImageView) findViewById(R.id.nav_back_img);
        imgCountTv = (TextView) findViewById(R.id.nav_count_tv);
        removeImg = (ImageView) findViewById(R.id.nav_remove_img);

        backImg.setOnClickListener(this);
        removeImg.setOnClickListener(this);

        viewPager = (ViewPager) findViewById(R.id.nav_image_viewpager);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);// 淡化status bar 和 navigation bar

        MobclickAgent.onPageStart("ImageViewPagerActivity"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写)
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ImageViewPagerActivity"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
        MobclickAgent.onPause(this);
    }


    private void initViewPager() {
        imageAlbumAdapter = new ImageAlbumAdapter();
        viewPager.setAdapter(imageAlbumAdapter);
        viewPager.setCurrentItem(curPosition);
        setImgCountTv(curPosition+1,imageViewList.size());
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                curPosition = position;
                setImgCountTv(position+1,imageViewList.size());
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * 从上一个Activity 的 Intent 中获取 UriString 的字符串
     * 再拆分成每一个URI
     */
    private void getUriList() {
        String uriString = getIntent().getStringExtra("UriString");
        String[] uris = uriString.split(" ");   // 获取每一个URI 字符串
        for(String s : uris){
            Uri uri = Uri.parse(s);
            uriList.add(uri);                   // 转化为 URI 添加到 uriList
        }
    }

    /**
     * 将当前的UriList 合成 URIString
     */
    private String setUriString(List<Uri> uriList){

        String uriString = "";
        for(Uri uri : uriList){
            if(uri==null||uri.toString().equals("")){
                return "";
            }
            if(uriString.length() == 0){
                uriString = uri.toString();
            }else {
                uriString = uriString + " " + uri.toString();
            }
        }
        return uriString;
    }

    /**
     * 根据每个图片的URI，初始化ImageView 列表
     */
    private void initImageViewList(){
        ImageView imageView;

        for(int i=0; i<uriList.size(); i++) {

            imageView = new ImageView(this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT));
            imageView.setImageBitmap(getBigBitmapFromUri(uriList.get(i)));
            imageViewList.add(imageView);
        }
    }

    private Bitmap getBigBitmapFromUri(Uri uri){

        String path = ImageUtils.getAbsoluteImagePath(this, uri);    // 从uri 中拿到 Path
//        Log.e("URI ==> PATH", path);
//        Log.e("URI ==> ",uri.toString());

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);    // options中已经得到将要拿到的图片的尺寸
//        Log.e("REAL width == height", options.outWidth + "==" + options.outHeight);

        options.inSampleSize = options.outWidth / ScreenUtils.getScreenWidth(this);

        int height = options.outHeight * ScreenUtils.getScreenWidth(this) / options.outWidth;
        options.outWidth = ScreenUtils.getScreenWidth(this);
        options.outHeight = height;
        options.inJustDecodeBounds = false;
//        Log.e("SET width == height", options.outWidth + "==" + options.outHeight);

        options.inPurgeable = true;
        options.inInputShareable = true;

        return ImageUtils.getBitmapByPath(path, options); // 将想要获得的尺寸的bitmap 拿到

    }


    private void setImgCountTv(int currentItem,int totalItem){
        imgCountTv.setText(currentItem + " / " + totalItem);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.nav_back_img:
                exitImageListActivity();
                break;
            case R.id.nav_remove_img:
                imageViewList.remove(curPosition);
                uriList.remove(curPosition);
                initViewPager();
                break;
            default:
                break;
        }
    }

    private void exitImageListActivity() {
        Intent intent = new Intent();
        intent.putExtra("UriString", setUriString(uriList));
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                exitImageListActivity();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 图片列表适配器
     */
    public class ImageAlbumAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return imageViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            LogUtils.e("destroyItem position = "+position);
            LogUtils.e("destroyItem imageViewList = "+imageViewList.size());

            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(imageViewList.get(position));
            return imageViewList.get(position);
        }
    }
}
