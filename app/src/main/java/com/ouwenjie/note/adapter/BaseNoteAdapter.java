package com.ouwenjie.note.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ouwenjie.note.R;
import com.ouwenjie.note.db.NoteDatabaseHelper;
import com.ouwenjie.note.model.BaseNote;
import com.ouwenjie.note.utils.LogUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** 笔记列表 适配器
 * Created by 文杰 on 2015/4/30.
 */
public class BaseNoteAdapter extends RecyclerView.Adapter<BaseNoteAdapter.ViewHolder>implements View.OnClickListener ,View.OnLongClickListener{

    private Context context;
    private List<BaseNote> noteList = new ArrayList<>();

    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    private NoteDatabaseHelper dbHelper = new NoteDatabaseHelper();

    public BaseNoteAdapter(Context context, List<BaseNote> noteList){
        super();
        this.context = context;
        this.noteList = noteList;
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener){
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public BaseNoteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_item_note,parent,false);
        // U can set the view's size, margins, paddings and layout parameters
        // ...
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        BaseNote note = noteList.get(position);
        viewHolder.mContent.setText(note.getContent());
        viewHolder.mDate.setText(note.getLastChangeDate());
        int bgColor = note.getBgColor();
        switch (bgColor){
            case BaseNote.LIGHT_BLUE:
                viewHolder.mCardView.setCardBackgroundColor(context.getResources().getColor(R.color.light_blue));
                break;
            case BaseNote.LIGHT_YELLOW:
                viewHolder.mCardView.setCardBackgroundColor(context.getResources().getColor(R.color.light_yellow));
                break;
            case BaseNote.LIGHT_RED:
                viewHolder.mCardView.setCardBackgroundColor(context.getResources().getColor(R.color.light_red));
                break;
            case BaseNote.LIGHT_GREEN:
                viewHolder.mCardView.setCardBackgroundColor(context.getResources().getColor(R.color.light_green));
                break;
        }

        long dbId = dbHelper.getId(note);
        viewHolder.itemView.setTag(String.valueOf(dbId));  // 将当前的note 的 id 保存起来
        LogUtils.e("Note ID == " + String.valueOf(dbId));
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public void addItem(BaseNote note, int position) {
        noteList.add(position, note);
        notifyItemInserted(position); //Attention!
    }

    public void removeItem(BaseNote note) {
        BaseNote rNote = null;
        for(BaseNote baseNote : noteList){
            if(dbHelper.getId(baseNote) == dbHelper.getId(note) ){
                rNote = baseNote;
            }
        }
        if(rNote != null) {
            int position = noteList.indexOf(rNote); // 根据 dbID 来选取Note ,是因为传进来了的note 可能是改变后的note,在 noteList 里面很可能找不到
            noteList.remove(position);
            notifyItemRemoved(position);//Attention!

        }
    }

    public void searchNotes(String string){
        List<BaseNote> otherNote = new ArrayList<>();
        Iterator<BaseNote> iterator = noteList.iterator();
        while(iterator.hasNext()) {
            BaseNote note = iterator.next();
            if (!note.getContent().contains(string)) {
                otherNote.add(note);
            }
        }
        for(BaseNote note : otherNote){
            removeItem(note);
        }
    }

    public void removeAllItem(){
        noteList.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if(onItemClickListener != null){
            onItemClickListener.onItemClick((String) v.getTag());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if(onItemLongClickListener != null){
            onItemLongClickListener.onItemLongClick((String) v.getTag());
        }
        return false;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public View itemView;

        public TextView mContent;
        public TextView mDate;
        public CardView mCardView;

        public ViewHolder(View view) {
            super(view);
            itemView = view;
            mContent = (TextView) view.findViewById(R.id.note_content_tv);
            mDate = (TextView) view.findViewById(R.id.note_last_date_tv);
            mCardView = (CardView) view.findViewById(R.id.note_cardview);
        }
    }

    public interface OnItemClickListener {

        public void onItemClick(String dbID);
    }

    public interface OnItemLongClickListener{
        public void onItemLongClick(String dbID);
    }

}
