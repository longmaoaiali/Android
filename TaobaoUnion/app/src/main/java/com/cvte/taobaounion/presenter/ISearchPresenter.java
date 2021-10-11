package com.cvte.taobaounion.presenter;

import com.cvte.taobaounion.base.IBasePresenter;
import com.cvte.taobaounion.view.ISearchViewCallback;

/**
 * Created by user on 2020/11/11.
 */

public interface ISearchPresenter extends IBasePresenter<ISearchViewCallback> {

    /**
     * 获取搜索历史内容
     */
    void getHistories();

    /**
     * 删除搜索历史
     */
    void delHistories();

    /**
     * 发起搜索
     * @param keyword
     */
    void doSearch(String keyword);


    /**
     * 重新搜索
     */
    void research();

    /**
     * 获取更多的搜索结果
     */
    void loaderMore();

    /**
     * 获取推荐词
     */
    void getRecommendWords();
}
