package com.cvte.taobaounion.presenter;

import com.cvte.taobaounion.base.IBasePresenter;
import com.cvte.taobaounion.view.ICategoryCallback;

/**
 * Created by user on 2020/10/26.
 */

public interface ICategoryPagerPresenter extends IBasePresenter<ICategoryCallback>{
    void getContentByCategoryId(int categoryId);

    void loadMore(int categoryId);

    void reload(int categoryId);



}
