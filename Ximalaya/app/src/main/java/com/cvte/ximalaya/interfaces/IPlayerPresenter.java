package com.cvte.ximalaya.interfaces;

import com.cvte.ximalaya.base.IBasePresenter;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

/**
 * Created by user on 2020/9/11.
 */

public interface IPlayerPresenter extends IBasePresenter<IPlayerCallback> {

    /*播放*/
    void play();

    /*暂停*/
    void pause();

    /*停止播放*/
    void stop();

    /*上一首*/
    void playPre();

    /*下一首*/
    void playNext();

    /*切换播放模式*/
    void switchPlayMode(XmPlayListControl.PlayMode mode);

    /*获取播放列表*/
    void getPlayList();

    /*根据节目列表位置进行播放*/
    void playByIndex(int index);

    /*设置播放进度*/
    void seekTo(int progress);


    /*判断播放器是否播放*/
    boolean isplay();

    //翻转播放列表排序
    void revesePlayList();

    /*播放的专辑ID*/
    void playByAlbumId(long id);

}
