package com.cvte.ximalaya.base;

import com.cvte.ximalaya.interfaces.IAlbumDetailViewCallback;

/**
 * Created by user on 2020/9/11.
 */

public interface IBasePresenter<T> {

    void registerViewCallback(T t);

    void unregisterViewCallback(T t);

}
