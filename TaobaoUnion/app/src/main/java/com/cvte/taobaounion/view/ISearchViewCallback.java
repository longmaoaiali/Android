package com.cvte.taobaounion.view;

import com.cvte.taobaounion.base.IBaseCallback;
import com.cvte.taobaounion.model.domain.Histories;
import com.cvte.taobaounion.model.domain.SearchRecommand;
import com.cvte.taobaounion.model.domain.SearchResult;

import java.util.List;

/**
 * Created by user on 2020/11/11.
 */

public interface ISearchViewCallback extends IBaseCallback {

    /**
     * 得到搜索历史
     * @param histories
     */
    void onHistoriesLoaded(Histories histories);

    /**
     * 删除搜索历史
     */
    void onHistoriesDeleted();

    /**
     * 搜索结果
     * @param result
     */
    void onSearchSuccess(SearchResult result);

    void onMoreLoaded(SearchResult result);

    void onMoreLoadedError();

    void onMoreLoadedEmpty();

    /**
     * 搜索推荐词加载
     * @param recommendWoeds
     */
    void onRecommendWordsLoaded(List<SearchRecommand.DataBean > recommendWoeds);


}
