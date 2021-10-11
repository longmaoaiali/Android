package com.cvte.taobaounion.view;

import com.cvte.taobaounion.base.IBaseCallback;
import com.cvte.taobaounion.model.domain.SellContent;

/**
 * Created by user on 2020/11/10.
 */

public interface IOnSellPageCallback extends IBaseCallback{

    /**
     * 内容加载成功
     * @param result
     */
    void onContentLoadedSuccess(SellContent result);

    /**
     * 加载更多内容
     * @param moreResult
     */
    void onMoreLoaded(SellContent moreResult);

    /**
     * 加载更多失败
     */
    void onMoreLoadedError();

    /**
     * 没有很多内容
     */
    void onMoreLoadedEmpty();

}
