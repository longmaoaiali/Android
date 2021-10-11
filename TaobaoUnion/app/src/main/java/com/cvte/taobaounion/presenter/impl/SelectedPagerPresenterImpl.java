package com.cvte.taobaounion.presenter.impl;

import com.cvte.taobaounion.model.Api;
import com.cvte.taobaounion.model.domain.SelectedContentNew;
import com.cvte.taobaounion.model.domain.SelectedPageCategory;
import com.cvte.taobaounion.presenter.ISelectedPagePresenter;
import com.cvte.taobaounion.utils.LogUtils;
import com.cvte.taobaounion.utils.RetrofitManager;
import com.cvte.taobaounion.utils.UrlUtils;
import com.cvte.taobaounion.view.ISelectedPageCallback;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by user on 2020/11/6.
 */

public class SelectedPagerPresenterImpl implements ISelectedPagePresenter {

    private static final String TAG = "SelectedPagerPresenterImpl";
    private ISelectedPageCallback mViewCallback;
    private final Api mApi;
    private SelectedPageCategory.DataBean mCurrentCategoryItem;

    public SelectedPagerPresenterImpl(){
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        mApi = retrofit.create(Api.class);

    }

    @Override
    public void registerViewCallback(ISelectedPageCallback callback) {
        this.mViewCallback = callback;
    }

    @Override
    public void unregisterViewCallback(ISelectedPageCallback callback) {
        this.mViewCallback = null;
    }

    @Override
    public void getCategories() {
        if (mViewCallback != null) {
            mViewCallback.onLoading();
        }
        Call<SelectedPageCategory> task = mApi.getSelectedPageCategories();
        task.enqueue(new Callback<SelectedPageCategory>() {
            @Override
            public void onResponse(Call<SelectedPageCategory> call, Response<SelectedPageCategory> response) {
                int code = response.code();
                LogUtils.d(TAG,"code-->"+code);
                if (code == HttpURLConnection.HTTP_OK) {
                    SelectedPageCategory result = response.body();
                    if (mViewCallback != null) {
                        mViewCallback.onCategoriesLoaded(result);
                    }
                } else {
                    if (mViewCallback != null) {
                        mViewCallback.onNetworkError();
                    }
                }
            }

            @Override
            public void onFailure(Call<SelectedPageCategory> call, Throwable t) {
                if (mViewCallback != null) {
                    mViewCallback.onNetworkError();
                }
            }
        });
    }

    @Override
    public void getContentByCategory(SelectedPageCategory.DataBean item) {

        this.mCurrentCategoryItem = item;
        int categoryId = item.getFavorites_id();
        String targetUrl = UrlUtils.getSelectedPageContentUrl(categoryId);

        Call<SelectedContentNew> task = mApi.getSelectedPageContent(targetUrl);
        task.enqueue(new Callback<SelectedContentNew>() {
            @Override
            public void onResponse(Call<SelectedContentNew> call, Response<SelectedContentNew> response) {
                int responseCode = response.code();
                LogUtils.d(TAG,"getContentByCategory request code-->"+responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    SelectedContentNew result = response.body();
                    if (mViewCallback != null) {
                        mViewCallback.onContentLoaded(result);
                    }
                } else {
                    if (mViewCallback != null) {
                        mViewCallback.onNetworkError();
                    }
                }
            }

            @Override
            public void onFailure(Call<SelectedContentNew> call, Throwable t) {
                if (mViewCallback != null) {
                    mViewCallback.onNetworkError();
                }
            }
        });
    }

    //重新加载
    @Override
    public void reloadContent() {
        this.getCategories();
    }
}
