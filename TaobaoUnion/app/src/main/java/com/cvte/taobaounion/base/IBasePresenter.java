package com.cvte.taobaounion.base;

import com.cvte.taobaounion.view.IHomeCallback;

/**
 * Created by user on 2020/10/26.
 */

public interface IBasePresenter<T> {

    void registerViewCallback(T callback);


    void unregisterViewCallback(T callback);

}
