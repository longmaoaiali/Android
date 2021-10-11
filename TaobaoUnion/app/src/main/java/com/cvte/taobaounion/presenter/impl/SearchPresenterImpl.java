package com.cvte.taobaounion.presenter.impl;

import com.cvte.taobaounion.model.Api;
import com.cvte.taobaounion.model.domain.Histories;
import com.cvte.taobaounion.model.domain.SearchRecommand;
import com.cvte.taobaounion.model.domain.SearchResult;
import com.cvte.taobaounion.presenter.ISearchPresenter;
import com.cvte.taobaounion.utils.JsonCacheUtil;
import com.cvte.taobaounion.utils.RetrofitManager;
import com.cvte.taobaounion.view.ISearchViewCallback;

import java.net.HttpURLConnection;
import java.security.KeyException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by user on 2020/11/11.
 */

public class SearchPresenterImpl implements ISearchPresenter{


    private final Api mApi;
    private ISearchViewCallback mSearchViewCallback = null;

    public static final int DEFAULT_PAGE = 1;

    private int mCurrentPage = DEFAULT_PAGE;
    private String mCurrentSearchKeyword = null;
    private final JsonCacheUtil mJsonCacheUtil;

    public SearchPresenterImpl(){
        RetrofitManager instance = RetrofitManager.getInstance();
        Retrofit retrofit = instance.getRetrofit();
        mApi = retrofit.create(Api.class);
        mJsonCacheUtil = JsonCacheUtil.getInstance();
    }

    @Override
    public void registerViewCallback(ISearchViewCallback callback) {
        this.mSearchViewCallback = callback;
    }

    @Override
    public void unregisterViewCallback(ISearchViewCallback callback) {
        this.mSearchViewCallback = null;
    }

    @Override
    public void getHistories() {
        Histories histories = mJsonCacheUtil.getCacheValue(KEY_HISTORIES,Histories.class);
        if (mSearchViewCallback != null) {
                mSearchViewCallback.onHistoriesLoaded(histories);
        }
    }

    @Override
    public void delHistories() {
        mJsonCacheUtil.delCache(KEY_HISTORIES);
        if (mSearchViewCallback != null) {
            mSearchViewCallback.onHistoriesDeleted();
        }
    }

    public static final String KEY_HISTORIES = "key_histories";
    public static final int COUNT_HISTORIES = 10;

    private void saveHistory(String history) {

        Histories histories = mJsonCacheUtil.getCacheValue(KEY_HISTORIES,Histories.class);
        //如果已经存在则删除后再添加
        List<String> historiesList = null;
        if (histories != null && histories.getHistories() != null) {
            historiesList = histories.getHistories();
            if (historiesList.contains(history)) {
                historiesList.remove(history);
            }
        }

        //去重已经完成，再添加
        if (historiesList == null) {
            historiesList = new ArrayList<>();
        }

        if (histories == null) {
            histories = new Histories();
        }

        histories.setHistories(historiesList);

        if (historiesList.size()>COUNT_HISTORIES) {
            historiesList = historiesList.subList(0,COUNT_HISTORIES);
        }
        //构造好数据
        historiesList.add(history);
        //保存记录
        mJsonCacheUtil.saveCache(KEY_HISTORIES,histories);
    }

    @Override
    public void doSearch(String keyword) {
        this.saveHistory(keyword);
        this.mCurrentSearchKeyword = keyword;

        if (mSearchViewCallback != null) {
            mSearchViewCallback.onLoading();
        }

        Call<SearchResult> task = mApi.doSearch(mCurrentPage, keyword);
        task.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                int code = response.code();
                if(code== HttpURLConnection.HTTP_OK){
                    handleSearchResult(response.body());
                } else {
                    onError();
                }
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                t.printStackTrace();
                onError();
            }
        });
    }

    private void onError() {
        if (mSearchViewCallback != null) {
            mSearchViewCallback.onNetworkError();
        }
    }

    private void handleSearchResult(SearchResult result) {
        if (mSearchViewCallback != null) {
            if (isResultEmpty(result)) {
                mSearchViewCallback.onEmpty();
            } else {
                mSearchViewCallback.onSearchSuccess(result);
            }
        }
    }

    /**
     * 返回true 代表数据不可用
     * @param result
     * @return
     */
    private boolean isResultEmpty(SearchResult result) {
        try {
            return  result == null || result.getData().getTbk_dg_material_optional_response().getResult_list().getMap_data().size() == 0;
        } catch (Exception e){
            return true;
        }
    }

    @Override
    public void research() {
        if (mSearchViewCallback != null) {
            if (mCurrentSearchKeyword == null) {
                mSearchViewCallback.onEmpty();
            } else {
                this.doSearch(mCurrentSearchKeyword);
            }
        }
    }

    @Override
    public void loaderMore() {

    }

    @Override
    public void getRecommendWords() {
        Call<SearchRecommand> task = mApi.getRecommendWorlds();
        task.enqueue(new Callback<SearchRecommand>() {
            @Override
            public void onResponse(Call<SearchRecommand> call, Response<SearchRecommand> response) {
                int code = response.code();
                if (code == HttpURLConnection.HTTP_OK) {
                    if (mSearchViewCallback != null) {
                        mSearchViewCallback.onRecommendWordsLoaded(response.body().getData());
                    }
                }
            }

            @Override
            public void onFailure(Call<SearchRecommand> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
