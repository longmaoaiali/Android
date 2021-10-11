package com.cvte.ximalaya.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cvte.ximalaya.base.BaseApplication;
import com.cvte.ximalaya.utils.Constants;
import com.cvte.ximalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2020/10/15.
 */

public class HistoryDao implements IHistoryDao {

    private static final String TAG = "HistoryDao";
    private final XimalayaDBHelper mDhHelper;
    private IHistoryDaoCallback mCallback;

    public HistoryDao(){
        mDhHelper = new XimalayaDBHelper(BaseApplication.getAppContext());
    }

    @Override
    public void setCallback(IHistoryDaoCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public synchronized void addHistory(Track track) {
        SQLiteDatabase db = null;
        boolean isSuccess = false;
        try {
            db = mDhHelper.getWritableDatabase();
            //添加之前先删除 避免重复
            db.delete(Constants.HISTORY_TB_NAME,Constants.HISTORY_TRACK_ID+"=?",new String[]{track.getDataId()+""});

            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(Constants.HISTORY_TRACK_ID,track.getDataId());
            values.put(Constants.HISTORY_TRACK_title,track.getTrackTitle());
            values.put(Constants.HISTORY_PLAY_COUNT,track.getPlayCount());
            values.put(Constants.HISTORY_DURATION,track.getDuration());
            values.put(Constants.HISTORY_UPDATE_TIME,track.getUpdatedAt());
            values.put(Constants.HISTORY_COVER,track.getCoverUrlLarge());


            db.insert(Constants.HISTORY_TB_NAME,null,values);

            db.setTransactionSuccessful();
            isSuccess = true;
        } catch (Exception e){
            isSuccess = false;
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mCallback != null) {
                mCallback.onHistoryAdd(isSuccess);
            }
        }
    }

    @Override
    public synchronized void delHistory(Track track) {
        SQLiteDatabase db = null;
        boolean isSuccess = false;
        try {
            db = mDhHelper.getWritableDatabase();
            db.beginTransaction();

            int delete = db.delete(Constants.HISTORY_TB_NAME,Constants.HISTORY_TRACK_ID+"=?",new String[]{track.getDataId()+""});
            db.setTransactionSuccessful();
            isSuccess = true;
        } catch (Exception e){
            isSuccess = false;
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mCallback != null) {
                mCallback.onHistoryDel(isSuccess);
            }
        }


    }

    @Override
    public void clearHistory() {
        SQLiteDatabase db = null;
        boolean isSuccess = false;
        try {
            db = mDhHelper.getWritableDatabase();
            db.beginTransaction();
            db.delete(Constants.HISTORY_TB_NAME,null,null);
            db.setTransactionSuccessful();
            isSuccess = true;
        } catch (Exception e){
            isSuccess = false;
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            if (mCallback != null) {
                mCallback.onHistoryClear(isSuccess);
            }
        }
    }

    @Override
    public synchronized void listHistory() {
        SQLiteDatabase db = null;
        List<Track> historyList = new ArrayList<>();
        try {
            db = mDhHelper.getWritableDatabase();
            db.beginTransaction();
            Cursor cursor = db.query(Constants.HISTORY_TB_NAME,null,null,null,null,null,"_id desc");

            while (cursor.moveToNext()) {
                Track track = new Track();

                int trackId = cursor.getInt(cursor.getColumnIndex(Constants.HISTORY_TRACK_ID));
                track.setDataId(trackId);

                int playCount = cursor.getInt(cursor.getColumnIndex(Constants.HISTORY_PLAY_COUNT));
                track.setPlayCount(playCount);

                int duration = cursor.getInt(cursor.getColumnIndex(Constants.HISTORY_DURATION));
                track.setDuration(duration);

                Long updateTime = cursor.getLong(cursor.getColumnIndex(Constants.HISTORY_UPDATE_TIME));
                track.setUpdatedAt(updateTime);

                String title = cursor.getString(cursor.getColumnIndex(Constants.HISTORY_TRACK_title));
                track.setTrackTitle(title);

                String cover = cursor.getString(cursor.getColumnIndex(Constants.HISTORY_COVER));
                track.setCoverUrlLarge(cover);
                track.setCoverUrlMiddle(cover);
                track.setCoverUrlSmall(cover);
                historyList.add(track);
            }
            db.setTransactionSuccessful();
            LogUtil.d(TAG,"history DAO size = "+historyList.size());

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
            //回调应该都在关闭数据库之后，否则会引起数据库访问冲突
            if (mCallback != null) {
                LogUtil.d(TAG,"DAO callback presenter");
                mCallback.onHistoryLoaded(historyList);
            }
        }
    }

}
