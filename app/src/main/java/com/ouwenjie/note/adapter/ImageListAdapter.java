package com.ouwenjie.note.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ouwenjie.note.R;
import com.ouwenjie.note.utils.DensityUtils;
import com.ouwenjie.note.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by 文杰 on 2015/4/14.
 */
public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> implements View.OnClickListener {

    private Context context;
    // 图片
    private List<Uri> imageUriList = new ArrayList<>();
    // 缩略图
    private List<Bitmap> previewImageList = new ArrayList<>();

    private View.OnClickListener onClickListener;

    public ImageListAdapter(Context context,List<Uri> imageUriList) {

        this.context = context;
        this.imageUriList = imageUriList;
        if(imageUriList.size() > 0) {
            for (Uri uri : imageUriList) {
                if(uri != null) {
                    previewImageList.add(getPreviewBitmap(uri));
                }
            }
        }
    }


    public void setItemOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    /**
     * 创建ViewHolder
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ImageListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_image, parent, false);

        view.setOnClickListener(this);
        return new ViewHolder(view);
    }

    /**
     * 将数据绑定到ViewHolder 中
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ImageListAdapter.ViewHolder holder, int position) {
        Bitmap bitmap = previewImageList.get(position);
        holder.imageView.setImageBitmap(bitmap);

        holder.itemView.setTag(position);

    }

    @Override
    public int getItemCount() {
        return imageUriList.size();
    }

    /**
     * 获取缩略图
     * @param uri
     * @return
     */
    private Bitmap getPreviewBitmap(Uri uri){
        String path = ImageUtils.getAbsoluteImagePath(context, uri);    // 从uri 中拿到 Path
//        Log.e("URI ==> PATH",path);
//        Log.e("URI ==> ",uri.toString());

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);    // options中已经得到将要拿到的图片的尺寸
//        Log.e("REAL width == height", options.outWidth + "==" + options.outHeight);

        options.inSampleSize = options.outWidth / DensityUtils.dp2px(context, 96f);

        int height = options.outHeight * DensityUtils.dp2px(context, 96f) / options.outWidth;
        options.outWidth = DensityUtils.dp2px(context, 96f);
        options.outHeight = height;
        options.inJustDecodeBounds = false;
//        Log.e("SET width == height", options.outWidth + "==" + options.outHeight);

        options.inPreferredConfig = Bitmap.Config.ARGB_4444;    // 默认是Bitmap.Config.ARGB_8888
        options.inPurgeable = true;
        options.inInputShareable = true;

        return ImageUtils.getBitmapByPath(path, options); // 将想要获得的尺寸的bitmap 拿到

    }


    public void addItem(Uri uri, int position) {
        imageUriList.add(position, uri);

        previewImageList.add(position, getPreviewBitmap(uri));
        notifyItemInserted(position); //Attention!

    }

    public void removeItem(Uri uri) {
        int position = imageUriList.indexOf(uri);
        imageUriList.remove(position);
        previewImageList.remove(position);
        notifyItemRemoved(position);//Attention!
    }

    public void removeItem(int position){
        imageUriList.remove(position);
        previewImageList.remove(position);
        notifyItemRemoved(position);//Attention!
    }

    @Override
    public void onClick(View v) {
        onClickListener.onClick(v);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View itemView;

        public ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            itemView = view;
            imageView = (ImageView) view.findViewById(R.id.list_item_image);

        }
    }


}
