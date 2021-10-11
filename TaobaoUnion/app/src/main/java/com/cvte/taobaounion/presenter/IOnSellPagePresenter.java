package com.cvte.taobaounion.presenter;

import com.cvte.taobaounion.base.IBasePresenter;
import com.cvte.taobaounion.view.IOnSellPageCallback;

/**
 * Created by user on 2020/11/10.
 */

public interface IOnSellPagePresenter extends IBasePresenter<IOnSellPageCallback> {


    /**
     * 加载特惠内容
     */
    void getOnSellContent();

    /**
     * 重新加载内容
     */
    void reload();

    /**
     * 加载更多内容
     */
    void loaderMore();
}
