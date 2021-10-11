package com.cvte.ximalaya.presenters;

import android.app.ActivityManager;
import android.database.Observable;
import android.util.Log;

import com.cvte.ximalaya.base.BaseApplication;
import com.cvte.ximalaya.data.HistoryDao;
import com.cvte.ximalaya.data.IHistoryDaoCallback;
import com.cvte.ximalaya.interfaces.IHistoryCallback;
import com.cvte.ximalaya.interfaces.IHistoryPresenter;
import com.cvte.ximalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by user on 2020/10/15.
 */

public class HistoryPresenter implements IHistoryPresenter, IHistoryDaoCallback {

    private static final String TAG = "HistoryPresenter";
    private List<IHistoryCallback> mCallbacks = new ArrayList<>();
    private final HistoryDao mHistoryDao;
    private List<Track> mCurrentHistory = null;

    private HistoryPresenter(){
        mHistoryDao = new HistoryDao();
        mHistoryDao.setCallback(this);
        //UI第一通过这里加载数据数据库数据进行显示
        listHistory();
    }

    private static HistoryPresenter sInstance = new HistoryPresenter();

    public static HistoryPresenter getInstance(){
        if (sInstance == null) {
            synchronized (HistoryPresenter.class){
                if (sInstance==null) {
                    sInstance = new HistoryPresenter();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void registerViewCallback(IHistoryCallback iHistoryCallback) {
        //UI回调的接口
        if (!mCallbacks.contains(iHistoryCallback)) {
            mCallbacks.add(iHistoryCallback);
        }
    }

    @Override
    public void unregisterViewCallback(IHistoryCallback iHistoryCallback) {
        mCallbacks.remove(iHistoryCallback);
    }

    @Override
    public void listHistory() {
        io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (mHistoryDao != null) {
                    mHistoryDao.listHistory();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void addHistory(final Track track) {
        LogUtil.d(TAG,"addHistory");
        //todo:判断是否超过100条
//        if (mCurrentHistory != null && mCurrentHistory.size()>100) {
//
//        }
        io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (mHistoryDao != null) {
                    mHistoryDao.addHistory(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void delHistory(final Track track) {
        io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (mHistoryDao != null) {
                    mHistoryDao.delHistory(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void cleanHistory() {
        io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (mHistoryDao != null) {
                    mHistoryDao.clearHistory();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    //DAO 回调接口
    @Override
    public void onHistoryAdd(boolean isSuccess) {
        listHistory();
    }

    @Override
    public void onHistoryDel(boolean isSuccess) {
        listHistory();
    }

    @Override
    public void onHistoryLoaded(final List<Track> tracks) {
        this.mCurrentHistory = tracks;
        LogUtil.d(TAG,"onHistoryLoaded notify UI change");
        //通知UI更新数据 目前通过RXjava是在IO子线程
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (IHistoryCallback callback : mCallbacks) {
                    callback.onHistoryLoaded(tracks);
                }
            }
        });
    }

    @Override
    public void onHistoryClear(boolean isSuccess) {
        listHistory();
    }
}
