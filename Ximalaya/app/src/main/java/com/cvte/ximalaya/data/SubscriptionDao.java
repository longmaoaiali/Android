package com.cvte.ximalaya.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.LocaleDisplayNames;

import com.cvte.ximalaya.base.BaseApplication;
import com.cvte.ximalaya.utils.Constants;
import com.cvte.ximalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2020/10/12.
 */

public class SubscriptionDao  implements ISubDao{

    public static final SubscriptionDao instance = new SubscriptionDao();
    private static final String TAG = "SubscriptionDao";
    private final XimalayaDBHelper mXimalayaDBHelper;
    private ISubDaoCallback mcallback = null;

    public static SubscriptionDao getInstance(){
        return instance;
    }

    private SubscriptionDao(){
        mXimalayaDBHelper = new XimalayaDBHelper(BaseApplication.getAppContext());
    }

    @Override
    public void setCallback(ISubDaoCallback callback) {
        this.mcallback = callback;
    }

    @Override
    public void addAlbum(Album album) {
        boolean isAddSuccess = false;
        SQLiteDatabase db = null;
        try {
            db = mXimalayaDBHelper.getWritableDatabase();
            db.beginTransaction();

            ContentValues contentValues = new ContentValues();
            //封装数据表的字段数据
            contentValues.put(Constants.SUB_COVERURL,album.getCoverUrlLarge());
            contentValues.put(Constants.SUB_TITLE,album.getAlbumTitle());
            contentValues.put(Constants.SUB_DESCRIPTION,album.getAlbumIntro());
            contentValues.put(Constants.SUB_TRACKSCOUNT,album.getIncludeTrackCount());
            contentValues.put(Constants.SUB_PLAYCOUNT,album.getPlayCount());
            contentValues.put(Constants.SUB_AUTHORNAME,album.getAnnouncer().getNickname());
            contentValues.put(Constants.SUB_ALBUMID,album.getId());
            /*插入数据*/
            db.insert(Constants.SUB_TB_NAME,null,contentValues);
            db.setTransactionSuccessful();
            isAddSuccess = true;
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
        if (mcallback != null) {
            mcallback.onAddResult(isAddSuccess);
        }
        
    }

    @Override
    public void delAlbum(Album album) {
        boolean isDeleteSuccess = false;
        SQLiteDatabase db = null;
        try {
            db = mXimalayaDBHelper.getWritableDatabase();
            db.beginTransaction();

            /*删除数据*/
            int delete = db.delete(Constants.SUB_TB_NAME,Constants.SUB_ALBUMID+"=?",new String[] {album.getId()+""});
            LogUtil.d(TAG,"delete-->" + delete);
            db.setTransactionSuccessful();
            isDeleteSuccess = true;
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }

        if (mcallback != null) {
            mcallback.onDeleteResult(isDeleteSuccess);
        }
    }

    @Override
    public synchronized void listAlbums() {
        SQLiteDatabase db = null;
        List<Album> result = new ArrayList<>();
        try {
            db = mXimalayaDBHelper.getReadableDatabase();
            db.beginTransaction();
            Cursor query = db.query(Constants.SUB_TB_NAME,null,null,null,null,null,"_id desc");
            while (query.moveToNext()) {
                Album album = new Album();
                //设置album封面图片
                String coverUrl = query.getString(query.getColumnIndex(Constants.SUB_COVERURL));
                album.setCoverUrlLarge(coverUrl);

                //设置album标题
                String title = query.getString(query.getColumnIndex(Constants.SUB_TITLE));
                album.setAlbumTitle(title);

                //设置album描述
                String description = query.getString(query.getColumnIndex(Constants.SUB_DESCRIPTION));
                album.setAlbumIntro(description);

                //设置album专辑数量
                int trackCount = query.getInt(query.getColumnIndex(Constants.SUB_TRACKSCOUNT));
                album.setIncludeTrackCount(trackCount);

                //设置album播放数量
                int playCount = query.getInt(query.getColumnIndex(Constants.SUB_PLAYCOUNT));
                album.setPlayCount(playCount);

                //设置作者
                String authorName = query.getString(query.getColumnIndex(Constants.SUB_AUTHORNAME));
                Announcer announcer = new Announcer();
                announcer.setNickname(authorName);
                album.setAnnouncer(announcer);

                //设置album封面图片
                int albumId = query.getInt(query.getColumnIndex(Constants.SUB_ALBUMID));
                album.setId(albumId);

                result.add(album);
            }
            query.close();
            db.setTransactionSuccessful();

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
        /*查询的数据放在Album List 将数据通知出去*/
        if (mcallback != null) {
            mcallback.onSubListLoaded(result);
        }
    }
}
