package com.cvte.taobaounion.presenter;

import com.cvte.taobaounion.base.IBasePresenter;
import com.cvte.taobaounion.view.IHomeCallback;

/**
 * Created by user on 2020/10/22.
 */

public interface IHomePresenter extends IBasePresenter<IHomeCallback> {

    /**
     * 获取商品分类
     */
    void getCategories();


}
