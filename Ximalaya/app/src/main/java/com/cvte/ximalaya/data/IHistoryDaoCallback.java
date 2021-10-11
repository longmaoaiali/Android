package com.cvte.ximalaya.data;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

/**
 * Created by user on 2020/10/14.
 */

public interface IHistoryDaoCallback  {

    /**
     * DAO 添加历史回调接口
     * @param isSuccess
     */
    void onHistoryAdd(boolean isSuccess);

    /**
     * DAO 删除历史回调接口
     * @param isSuccess
     */
    void onHistoryDel(boolean isSuccess);

    /**
     * DAO 加载历史回调接口
     * @param tracks
     */
    void onHistoryLoaded(List<Track> tracks);

    /**
     * DAO 清除所有历史回调接口
     * @param isSuccess
     */
    void onHistoryClear(boolean isSuccess);

}
