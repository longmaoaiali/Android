package com.cvte.ximalaya.interfaces;

import com.cvte.ximalaya.base.IBasePresenter;

/**
 * Created by user on 2020/9/8.
 */

public interface IAlbumDetialPresenter extends IBasePresenter<IAlbumDetailViewCallback>{
    /*下拉刷新更多内容*/
    void pull2RefreshMore();

    /*加载更多*/
    void loadMore();

    /*获取专辑详情*/
    void getAlbumDetail(int albumId,int page);


}
