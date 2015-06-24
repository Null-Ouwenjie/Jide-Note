package com.ouwenjie.note.helper;

import android.content.Context;

import com.ouwenjie.note.R;
import com.ouwenjie.note.model.CloudNote;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.OtherLoginListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 *
 * Created by 文杰 on 2015/6/19.
 */
public class BmobHelper {

    private static BmobQuery<CloudNote> noteQuery;

    /**
     * 初始化Bmob
     * @param context
     */
    public static void initBmob(Context context){
        String bmobId = context.getResources().getString(R.string.bmob_id);
        Bmob.initialize(context,bmobId);
    }


    public static void loginWithAuth(Context context,String snsType,String accessToken,String expiresIn,String userId,
                              OtherLoginListener listener){
        BmobUser.BmobThirdUserAuth authInfo = new BmobUser.BmobThirdUserAuth(snsType,accessToken, expiresIn,userId);
        BmobUser.loginWithAuthData(context, authInfo, listener);

    }


    /**
     * 保存一个note
     * @param note
     * @param context
     * @param saveListener
     */
    public static void saveNote(CloudNote note,Context context,SaveListener saveListener){
        note.save(context,saveListener);
    }

    /**
     * 根据ObjectId查找一个NOTE
     * @param context
     * @param id
     * @return
     */
    public static void findNoteByObjId(final Context context, final String id,GetListener<CloudNote> listener){
        noteQuery = new BmobQuery<>();
        noteQuery.getObject(context, id, listener);
    }

    /**
     * 根据创建时间查找一个NOTE
     * @param context
     * @param createDate
     * @return
     */
    public static void findNoteByCreateDate(final Context context,String createDate,FindListener<CloudNote> listener){
        noteQuery = new BmobQuery<>();
        noteQuery.addWhereEqualTo("createDate",createDate);
        noteQuery.findObjects(context, listener);
    }

    /**
     * 更新一个Note
     * @param context
     * @param note
     * @param id
     * @param listener
     */
    public static void updateNote(Context context,CloudNote note,String id,UpdateListener listener){
        note.update(context,id,listener);

    }

    /**
     * 更新一个NOTE(自动查找ObjID
     * @param context
     * @param note
     * @param listener
     */
    public static void updateNote(final Context context, CloudNote note, final UpdateListener listener){

        findNoteByCreateDate(context, note.getCreateDate(), new FindListener<CloudNote>() {
            @Override
            public void onSuccess(List<CloudNote> list) {
                CloudNote note = list.get(0);
                String id = note.getObjectId();
                if((id != null) && !(id.equals(""))) {
                    updateNote(context, note, id, listener);
                }
            }

            @Override
            public void onError(int i, String s) {
                listener.onFailure(i,s);
            }
        });

    }

    /**
     * 删除数据只能通过objectId来删除，目前不提供查询条件方式的删除方法。
     * @param context
     * @param note
     * @param id
     * @param listener
     */
    public static void deleteNote(Context context,CloudNote note,String id,DeleteListener listener){
        note.setObjectId(id);
        note.delete(context,listener);
    }

    /**
     * 删除数据只能通过objectId来删除，目前不提供查询条件方式的删除方法。
     * @param context
     * @param note
     * @param listener
     */
    public static void deleteNote(final Context context,CloudNote note,final DeleteListener listener){

        findNoteByCreateDate(context, note.getCreateDate(), new FindListener<CloudNote>() {
            @Override
            public void onSuccess(List<CloudNote> list) {
                CloudNote note = list.get(0);
                String id = note.getObjectId();
                if((id != null) && !(id.equals(""))) {
                    deleteNote(context,note,id,listener);
                }
            }

            @Override
            public void onError(int i, String s) {
                listener.onFailure(i,s);
            }
        });

    }


}
