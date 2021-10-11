package com.cvte.taobaounion.presenter;

import com.cvte.taobaounion.base.IBasePresenter;
import com.cvte.taobaounion.model.domain.SelectedPageCategory;
import com.cvte.taobaounion.view.ISelectedPageCallback;

/**
 * Created by user on 2020/11/5.
 */

public interface ISelectedPagePresenter extends IBasePresenter<ISelectedPageCallback> {

    /**
     * 获取分类
     */
    void getCategories();

    /**
     * 根据categoryID获得内容
     * @param item
     */
    void getContentByCategory(SelectedPageCategory.DataBean item);


    /**
     * 重新加载内容
     */
    void reloadContent();

}
