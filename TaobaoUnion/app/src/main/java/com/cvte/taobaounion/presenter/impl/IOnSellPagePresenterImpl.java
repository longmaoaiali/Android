package com.cvte.taobaounion.presenter.impl;

import com.cvte.taobaounion.model.Api;
import com.cvte.taobaounion.model.domain.SellContent;
import com.cvte.taobaounion.presenter.IOnSellPagePresenter;
import com.cvte.taobaounion.utils.RetrofitManager;
import com.cvte.taobaounion.utils.UrlUtils;
import com.cvte.taobaounion.view.IOnSellPageCallback;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by user on 2020/11/10.
 */

public class IOnSellPagePresenterImpl implements IOnSellPagePresenter {

    public static final int DEFAULT_PAGE = 1;
    private int mCurrentPage = DEFAULT_PAGE;
    private IOnSellPageCallback mSellPageCallback = null;
    private final Api mApi;

    public IOnSellPagePresenterImpl(){
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        mApi = retrofit.create(Api.class);

    }

    @Override
    public void registerViewCallback(IOnSellPageCallback callback) {
        this.mSellPageCallback = callback;
    }

    @Override
    public void unregisterViewCallback(IOnSellPageCallback callback) {
        this.mSellPageCallback = null;
    }

    @Override
    public void getOnSellContent() {
        //UI loading
        if (mSellPageCallback != null) {
            mSellPageCallback.onLoading();
        }
        //发起特惠界面的网络请求
        //Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        //Api api = retrofit.create(Api.class);
        String targetUrl = UrlUtils.getOnSellPageUrl(mCurrentPage);
        Call<SellContent> task = mApi.getOnSellPageContent(targetUrl);
        task.enqueue(new Callback<SellContent>() {
            @Override
            public void onResponse(Call<SellContent> call, Response<SellContent> response) {
                int code = response.code();
                if (code == HttpURLConnection.HTTP_OK) {
                    //todo:success
                    SellContent result = response.body();
                    onSuccess(result);
                } else {
                    onError();
                }

            }

            @Override
            public void onFailure(Call<SellContent> call, Throwable t) {
                onError();
            }
        });

    }

    private void onError() {
        if (mSellPageCallback != null) {
            mSellPageCallback.onNetworkError();
        }
    }

    private void onEmpty() {
        if (mSellPageCallback != null) {
            mSellPageCallback.onEmpty();
        }
    }

    private void onSuccess(SellContent result) {
        if (mSellPageCallback != null) {
            try {
                int size = result.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data().size();
                if (size == 0) {
                    onEmpty();
                } else {
                    mSellPageCallback.onContentLoadedSuccess(result);
                }
            } catch (Exception e) {
                e.printStackTrace();
                onEmpty();
            }
        }
    }

    @Override
    public void reload() {
        //重新加载
        this.getOnSellContent();
    }

    @Override
    public void loaderMore() {
        mCurrentPage++;
        String targetUrl = UrlUtils.getOnSellPageUrl(mCurrentPage);
        Call<SellContent> task = mApi.getOnSellPageContent(targetUrl);
        task.enqueue(new Callback<SellContent>() {
            @Override
            public void onResponse(Call<SellContent> call, Response<SellContent> response) {
                int code = response.code();
                if (code == HttpURLConnection.HTTP_OK) {
                    //todo:success
                    SellContent result = response.body();
                    onLoaderMore(result);
                } else {
                    onMoreLoaderError();
                }

            }

            @Override
            public void onFailure(Call<SellContent> call, Throwable t) {
                onMoreLoaderError();
            }
        });
    }

    private void onMoreLoaderError() {
        mCurrentPage--;
        mSellPageCallback.onMoreLoadedError();
    }

    private void onLoaderMore(SellContent result) {
        try {
            int size = result.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data().size();
            if (size == 0) {
                mSellPageCallback.onMoreLoadedError();
            } else {
                mSellPageCallback.onMoreLoaded(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mSellPageCallback.onMoreLoadedError();
        }
    }
}
