package com.cvte.ximalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * Created by user on 2020/10/9.
 */

public interface ISubscriptionCallback {

    /**
     * 调用添加后 更新UI效果
     * @param isSuccess
     */
    void onAddResult(boolean isSuccess);

    /**
     * 调用删除后 更新UI效果
     * @param isSuccess
     */
    void onDeleteResult(boolean isSuccess);


    /**
     * 订阅专辑加载的内容
     * @param albums
     */
    void onSubscriptionsLoaded(List<Album> albums);
}
