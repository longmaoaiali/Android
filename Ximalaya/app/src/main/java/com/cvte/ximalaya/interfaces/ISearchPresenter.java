package com.cvte.ximalaya.interfaces;

import com.cvte.ximalaya.base.IBasePresenter;

/**
 * Created by user on 2020/9/25.
 */

public interface ISearchPresenter extends IBasePresenter<ISearchCallback>{

    /**
     * 搜索接口
     * @param keyword 搜索关键词
     */
    void doSearch(String keyword);

    /**
     * 重新搜索
     */
    void reSearch();

    /**
     * 加载很多搜索结果
     */
    void loadMore();

    /**
     * 获取热词
     */
    void getHotWord();

    /**
     * 根据输入的关键词获取推荐的关键词
     * @param keyword
     */
    void getRecommendWord(String keyword);
}
