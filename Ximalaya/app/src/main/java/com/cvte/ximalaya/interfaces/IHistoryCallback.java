package com.cvte.ximalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

/**
 * Created by user on 2020/10/14.
 */

public interface IHistoryCallback {

    /**
     * 历史内容加载结果
     * @param tracks
     */
    void onHistoryLoaded(List<Track> tracks);

}
