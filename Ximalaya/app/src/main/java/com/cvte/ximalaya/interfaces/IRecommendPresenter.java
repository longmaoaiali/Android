package com.cvte.ximalaya.interfaces;

import com.cvte.ximalaya.base.IBasePresenter;

/**
 * Created by user on 2020/9/4.
 */

/*逻辑层的接口*/
public interface IRecommendPresenter extends IBasePresenter<IRecommendViewCallback>{

    /*获得推荐内容*/
    void getRecommendList();

    /*下拉刷新更多内容*/
    void pull2RefreshMore();

    /*加载更多*/
    void loadMore();


}
