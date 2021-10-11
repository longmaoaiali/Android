package com.cvte.ximalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.List;

/**
 * Created by user on 2020/9/25.
 */

public interface ISearchCallback {

    /**
     * 搜索结果回调的方法
     * @param result
     */
    void onSearchResultLoaded(List<Album> result);


    /**
     * 获取推荐热词的回调接口
     * @param hotWordList
     */
    void onHotWordLoaded(List<HotWord> hotWordList);

    /**
     * 加载很多的结果返回
     * @param result 返回结果
     * @param isOkay true加载更多成功 false没有加载更多
     */
    void onLoadMoreResult(List<Album> result, boolean isOkay);

    /**
     * 联想关键词的结果回调方法
     * @param keyWordList
     */
    void onRecommendWordLoaded(List<QueryResult> keyWordList);

    /**
     * 错误回调通知
     * @param errorCode
     * @param errorMsg
     */
    void onError(int errorCode, String errorMsg);
}
