package com.ouwenjie.note.db;

import com.ouwenjie.note.model.BaseNote;

import java.util.Iterator;
import java.util.TreeSet;

/**
 * 数据库操作帮助类
 * Created by 文杰 on 2015/3/31.
 */
public class NoteDatabaseHelper {

    public void sava(BaseNote note){
        note.save();
    }

    public void delete(BaseNote note){
        note.delete();
    }

    public void deleteAll(){
        BaseNote.deleteAll(BaseNote.class);
    }

    public void update(BaseNote note){
        note.save();
    }

    public long getId(BaseNote note){
        return note.getId();
    }

    public BaseNote get(Long i){
        return BaseNote.findById(BaseNote.class,i);
    }

    public TreeSet<BaseNote> getAll(){
        Iterator<BaseNote> iterator = BaseNote.findAll(BaseNote.class);
        TreeSet<BaseNote> noteSet = new TreeSet<>();
        while(iterator.hasNext()){
            noteSet.add(iterator.next());
        }
        return noteSet;
    }


}
