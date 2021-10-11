package com.cvte.ximalaya.data;

import com.ximalaya.ting.android.opensdk.model.track.Track;

/**
 * Created by user on 2020/10/14.
 */

public interface IHistoryDao {
    /**
     * 设置回调接口
     * @param callback
     */
    void setCallback(IHistoryDaoCallback callback);

    /**
     * 添加历史
     * @param track
     */
    void addHistory(Track track);

    /**
     * 删除历史
     * @param track
     */
    void delHistory(Track track);

    /**
     * 清除历史
     */
    void clearHistory();


    /**
     * 获取历史内容
     */
    void listHistory();
}
