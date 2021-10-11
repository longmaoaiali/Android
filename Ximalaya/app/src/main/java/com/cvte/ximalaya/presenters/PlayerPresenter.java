package com.cvte.ximalaya.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.cvte.ximalaya.api.XimalayaDataApi;
import com.cvte.ximalaya.base.BaseApplication;
import com.cvte.ximalaya.interfaces.IPlayerCallback;
import com.cvte.ximalaya.interfaces.IPlayerPresenter;
import com.cvte.ximalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by user on 2020/9/11.
 */

public class PlayerPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {


    private final XmPlayerManager mPlayerManager;
    private static final String TAG = "PlayerPresenter";
    private List<IPlayerCallback> mIPlayerCallbackList = new ArrayList<>();
    private Track mCurrentTrack;
    private int mCurrentIndex = 0;
    private SharedPreferences mPlayModeSp;
    private static final String PLAY_MODE_SP_KEY = "PlayMode";

    private XmPlayListControl.PlayMode mCurrentPlayMode = XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
    private boolean isReverse = false;
    private int mCurrentProgressPosition = 0;
    private int mCurrentProgressDuration = 0;

    private PlayerPresenter(){
        mPlayerManager = XmPlayerManager.getInstance(BaseApplication.getAppContext());
        //广告的接口
        mPlayerManager.addAdsStatusListener(this);
        //注册播放器状态的接口
        mPlayerManager.addPlayerStatusListener(this);

        /*记住之前设置的播放*/
        mPlayModeSp = BaseApplication.getAppContext().getSharedPreferences(PLAY_MODE_SP_KEY, Context.MODE_PRIVATE);

    }

    private static PlayerPresenter sPlayerPresenter;

    public static PlayerPresenter getInstance(){
        if (sPlayerPresenter == null) {
            synchronized (PlayerPresenter.class){
                sPlayerPresenter = new PlayerPresenter();
            }
        }
        return  sPlayerPresenter;
    }

    private boolean isPlayerListSet = false;
    public void setPlayerList(List<Track> tracks,int playerIndex){
        if (mPlayerManager != null) {
            isPlayerListSet = true;
            mPlayerManager.setPlayList(tracks,playerIndex);
            mCurrentTrack = tracks.get(playerIndex);
            mCurrentIndex = playerIndex;
            //mTrackTitle = track.getTrackTitle();
        }
    }

    /*回调都是Presenter回调显示UI的*/
    @Override
    public void registerViewCallback(IPlayerCallback iPlayerCallback) {
        iPlayerCallback.onTrackUpdate(mCurrentTrack,mCurrentIndex);
        //处理状态
        handlePlayState(iPlayerCallback);
        iPlayerCallback.onPlayProgressChange(mCurrentProgressPosition,mCurrentProgressDuration);

        int PlayModeSpInt =  mPlayModeSp.getInt(PLAY_MODE_SP_KEY, 1);
        LogUtil.d(TAG,"PlayModeSpInt --> "+PlayModeSpInt);
        mCurrentPlayMode = XmPlayListControl.PlayMode.values()[PlayModeSpInt];
        if (mCurrentPlayMode == null) {
            LogUtil.d(TAG,"mCurrentPlayMode == null");
        }
        iPlayerCallback.onPlayModeChange(mCurrentPlayMode);
        if (!mIPlayerCallbackList.contains(iPlayerCallback)) {
            mIPlayerCallbackList.add(iPlayerCallback);
        }

    }

    private void handlePlayState(IPlayerCallback iPlayerCallback) {
        int playState = mPlayerManager.getPlayerStatus();
        if(playState == PlayerConstants.STATE_STARTED){
            iPlayerCallback.onPlayPause();
        }else {
            iPlayerCallback.onPlayStart();
        }
    }

    @Override
    public void unregisterViewCallback(IPlayerCallback iPlayerCallback) {

    }

    @Override
    public void play() {
        LogUtil.d(TAG,"isPlayerListSet--> "+isPlayerListSet);
        if (isPlayerListSet) {
            int playerStatus = mPlayerManager.getPlayerStatus();
            LogUtil.d(TAG,"playerStatus --> "+playerStatus);
            //Log.d(TAG,Log.getStackTraceString(new Throwable()));
            //LogUtil.d(TAG.);
            mPlayerManager.play();
        }
    }

    @Override
    public void pause() {
        if (mPlayerManager != null) {
            mPlayerManager.pause();
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void playPre() {
        if (mPlayerManager != null) {
            mPlayerManager.playPre();
        }
    }

    @Override
    public void playNext() {
        if (mPlayerManager != null) {
            mPlayerManager.playNext();
        }
    }

    @Override
    public void switchPlayMode(XmPlayListControl.PlayMode mode) {
        if (mPlayerManager != null) {
            mCurrentPlayMode = mode;
            mPlayerManager.setPlayMode(mode);
            for (IPlayerCallback iPlayerCallback : mIPlayerCallbackList) {
                iPlayerCallback.onPlayModeChange(mode);
            }
            //保存到SharedPreferences
            SharedPreferences.Editor editor = mPlayModeSp.edit();
            editor.putInt(PLAY_MODE_SP_KEY,mode.ordinal());
            editor.commit();
        }
    }

    @Override
    public void getPlayList() {
        if (mPlayerManager != null) {
            List<Track> playList = mPlayerManager.getPlayList();
            for (IPlayerCallback iPlayerCallback : mIPlayerCallbackList) {
                iPlayerCallback.onListLoaded(playList);
            }
        }
        //mPlayerManager.getPlayList();
    }

    @Override
    public void playByIndex(int index) {
        if (mPlayerManager != null) {
            mPlayerManager.play(index);
        }
    }

    @Override
    public void seekTo(int progress) {
        //手动拖动进度条跳跃播放内容
        mPlayerManager.seekTo(progress);
    }

    @Override
    public boolean isplay() {
        return mPlayerManager.isPlaying();
    }

    @Override
    public void revesePlayList() {
        //size=10 下标由1-->8  size-1-当前下标
        List<Track> playList = mPlayerManager.getPlayList();
        Collections.reverse(playList);
        isReverse = !isReverse;
        //设置回去
        mCurrentIndex = playList.size() - 1 - mCurrentIndex;
        mPlayerManager.setPlayList(playList,mCurrentIndex);

        //更新播放列表的顺序 DetailActivity的顺序
        mCurrentTrack = (Track) mPlayerManager.getCurrSound();
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbackList) {
            iPlayerCallback.onListLoaded(playList);
            iPlayerCallback.onTrackUpdate(mCurrentTrack,mCurrentIndex);
            iPlayerCallback.updateListOrder(isReverse);
        }
    }

    @Override
    public void playByAlbumId(long id) {
        //todo:播放专辑的第一首歌
        //获取专辑列表
        XimalayaDataApi ximalayaDataApi = XimalayaDataApi.getXimalayaDataApi();
        ximalayaDataApi.getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(@Nullable TrackList trackList) {
                //播放专辑的第一首歌
                List<Track> tracks = trackList.getTracks();
                if (tracks != null && tracks.size()>0) {
                    mPlayerManager.setPlayList(tracks,0);
                    isPlayerListSet = true;
                    mCurrentTrack = tracks.get(0);
                    mCurrentIndex = 0;
                }
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(BaseApplication.getAppContext(),"请求数据错误",Toast.LENGTH_SHORT).show();
            }
        },(int)id,1);
    }

    /*广告物料相关的实现-->*/
    @Override
    public void onStartGetAdsInfo() {
        LogUtil.d(TAG,"onStartGetAdsInfo");
    }

    @Override
    public void onGetAdsInfo(AdvertisList advertisList) {
        LogUtil.d(TAG,"onGetAdsInfo");
    }

    @Override
    public void onAdsStartBuffering() {
        LogUtil.d(TAG,"onAdsStartBuffering");
    }

    @Override
    public void onAdsStopBuffering() {
        LogUtil.d(TAG,"onAdsStopBuffering");
    }

    @Override
    public void onStartPlayAds(Advertis advertis, int i) {
        LogUtil.d(TAG,"onStartPlayAds");
    }

    @Override
    public void onCompletePlayAds() {
        LogUtil.d(TAG,"onCompletePlayAds");
    }

    @Override
    public void onError(int what, int extra) {
        LogUtil.d(TAG,"onError: what--> "+what+"extra msg--> "+extra);
    }

    /*播放器相关的回调接口*/
    @Override
    public void onPlayStart() {
        LogUtil.d(TAG,"onPlayStart");
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbackList) {
            iPlayerCallback.onPlayStart();
        }
    }

    @Override
    public void onPlayPause() {
        LogUtil.d(TAG,"onPlayPause");
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbackList) {
            iPlayerCallback.onPlayPause();
        }
    }

    @Override
    public void onPlayStop() {
        LogUtil.d(TAG,"onPlayStop");
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbackList) {
            iPlayerCallback.onPlayStop();
        }
    }

    @Override
    public void onSoundPlayComplete() {
        LogUtil.d(TAG,"onSoundPlayComplete");
    }

    @Override
    public void onSoundPrepared() {
        LogUtil.d(TAG,"onSoundPrepared");
        LogUtil.d(TAG,"current state --> "+mPlayerManager.getPlayerStatus());
        //播放器准备好了再开始play
        if (mPlayerManager.getPlayerStatus() == PlayerConstants.STATE_PREPARED) {
            mPlayerManager.play();
        }
    }

    //切歌
    @Override
    public void onSoundSwitch(PlayableModel lastModel, PlayableModel curModel) {
        LogUtil.d(TAG,"onSoundSwitch");
//        if (curModel != null) {
//            LogUtil.d(TAG,"current Model is -->" + curModel.getKind());
//        }

        /*不推荐直接对比字段*/
//        if ("track".equals(curModel.getKind())) {
//            Track currentTrack = (Track) curModel;
//            LogUtil.d(TAG,"title == > " + currentTrack.getTrackTitle());
//        }

        mCurrentIndex = mPlayerManager.getCurrentIndex();
        if (curModel instanceof Track) {
            Track mCurrentTrack = (Track) curModel;
            LogUtil.d(TAG,"currentTrack title is --> " + mCurrentTrack.getTrackTitle());

            /*保存播放记录历史*/
            HistoryPresenter historyPresenter = HistoryPresenter.getInstance();
            historyPresenter.addHistory(mCurrentTrack);

            if (mIPlayerCallbackList != null) {
                for (IPlayerCallback iPlayerCallback : mIPlayerCallbackList) {
                    iPlayerCallback.onTrackUpdate(mCurrentTrack,mCurrentIndex);
                }
            }
        }

    }

    @Override
    public void onBufferingStart() {
        LogUtil.d(TAG,"onBufferingStart");
    }

    @Override
    public void onBufferingStop() {
        LogUtil.d(TAG,"onBufferingStop");
    }

    @Override
    public void onBufferProgress(int i) {
        LogUtil.d(TAG,"onBufferProgress");
    }

    @Override
    public void onPlayProgress(int curPos, int duration) {

        this.mCurrentProgressPosition = curPos;
        this.mCurrentProgressDuration = duration;


        LogUtil.d(TAG,"onPlayProgress");
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbackList) {
            iPlayerCallback.onPlayProgressChange(curPos,duration);
        }
    }

    @Override
    public boolean onError(XmPlayerException e) {
        LogUtil.d(TAG,"onError");
        return false;
    }

    public boolean hasPlayList() {
        return  isPlayerListSet;
    }


}
