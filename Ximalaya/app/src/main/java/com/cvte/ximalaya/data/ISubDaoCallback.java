package com.cvte.ximalaya.data;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * Created by user on 2020/10/12.
 */

public interface ISubDaoCallback {

    /**
     * 添加数据库的结果回调
     * @param isSuccess
     */
    void onAddResult(boolean isSuccess);


    /**
     * 删除数据库的结果回调
     * @param isSuccess
     */
    void onDeleteResult(boolean isSuccess);

    /**
     * 获取数据库全部数据的结果回调
     * @param result
     */
    void onSubListLoaded(List<Album> result);

}
