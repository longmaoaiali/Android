package com.cvte.ximalaya.data;

import com.ximalaya.ting.android.opensdk.model.album.Album;

/**
 * Created by user on 2020/10/12.
 */

public interface ISubDao {

    void setCallback(ISubDaoCallback callback);

    /**
     * 添加订阅专辑
     * @param album
     */
    void addAlbum(Album album);

    /**
     * 删除订阅专辑
     * @param album
     */
    void delAlbum(Album album);

    /**
     * 获取订阅内容
     */
    void listAlbums();
}
