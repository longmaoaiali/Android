package com.cvte.ximalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * Created by user on 2020/9/4.
 */

/*回调UI显示数据*/
public interface IRecommendViewCallback {

    /*获取推荐内容的结果*/
    void onRecommendListLoaded(List<Album> result);

    /*网络错误*/
    void onNetworkError();

    /*数据为空*/
    void onEmpty();

    /*正在获取请求 加载一个loading图片*/
    void onLoading();

    /*加载更多*/
    void onLoaderMore(List<Album> result);
    void onRefreshMoreMore(List<Album> result);
}
