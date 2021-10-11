package com.cvte.ximalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

/**
 * Created by user on 2020/9/8.
 */

public interface IAlbumDetailViewCallback {
    /*专辑详情*/
    void onDetailListLoaded(List<Track> tracks);

    /*将album传给UI*/
    void onAlbumLoaded(Album album);

    /*网络错误*/
    void onNetworkError(int errorCode, String errorMsg);

    //size>0 表示是否有更多数据被加载
    void onLoaderMoreFinished(int size);

    void onRefreshFinished(int size);
}
