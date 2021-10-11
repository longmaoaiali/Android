package com.cvte.taobaounion.presenter.impl;

import android.util.Log;

import com.cvte.taobaounion.model.Api;
import com.cvte.taobaounion.model.domain.TicketParams;
import com.cvte.taobaounion.model.domain.TicketResult;
import com.cvte.taobaounion.presenter.ITicketPresenter;
import com.cvte.taobaounion.utils.LogUtils;
import com.cvte.taobaounion.utils.RetrofitManager;
import com.cvte.taobaounion.utils.UrlUtils;
import com.cvte.taobaounion.view.ITicketPagerCallback;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by user on 2020/11/3.
 */

public class TicketPresenterImpl implements ITicketPresenter {

    private static final String TAG = "TicketPresenterImpl";
    private ITicketPagerCallback mViewCallback;
    private TicketResult mTicketResult;
    private String mCover;

    enum LoadState {
        LOADING,SUCCESS,ERROR,NONE
    }

    private LoadState mCurrentState = LoadState.NONE;

    @Override
    public void registerViewCallback(ITicketPagerCallback callback) {
        this.mViewCallback = callback;
        if(mCurrentState != LoadState.NONE) {
            //说明状态已经改变了
            //更新UI
            if(mCurrentState == LoadState.SUCCESS) {
                onTicketLoadedSuccess();
            } else if(mCurrentState == LoadState.ERROR) {
                onLoadedTicketError();
            } else if(mCurrentState == LoadState.LOADING) {
                onTicketLoading();
            }
        }
    }

    private void onTicketLoadedSuccess() {
        LogUtils.d(TAG,"onTicketLoadedSuccess == > " + mCover);
        LogUtils.d(TAG,"onTicketLoadedSuccess == > " + mTicketResult);
        if(mViewCallback != null) {
            mViewCallback.onTicketLoaded(mCover,mTicketResult);
        } else {
            mCurrentState = LoadState.SUCCESS;
        }
    }

    private void onTicketLoading() {
        if(mViewCallback != null) {
            mViewCallback.onLoading();
        } else {
            mCurrentState = LoadState.LOADING;
        }
    }

    private void onLoadedTicketError() {
        if(mViewCallback != null) {
            mViewCallback.onNetworkError();
        } else {
            mCurrentState = LoadState.ERROR;
        }
    }

    @Override
    public void unregisterViewCallback(ITicketPagerCallback callback) {
        this.mViewCallback = null;
    }

    @Override
    public void getTicket(String title, String url, String cover) {
        this.onTicketLoading();
        this.mCover = cover;
        /*获取淘口令*/
        //url作为post请求的参数，需要确保URL正确
        String ticketUrl = UrlUtils.getTicketUrl(url);

        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        Api api = retrofit.create(Api.class);
        TicketParams ticketParams = new TicketParams(title,ticketUrl);
        Call<TicketResult> task = api.getTicket(ticketParams);
        task.enqueue(new Callback<TicketResult>() {
            @Override
            public void onResponse(Call<TicketResult> call, Response<TicketResult> response) {
                int code = response.code();
                LogUtils.d(TAG,"result code == > " + code);
                if(code == HttpURLConnection.HTTP_OK) {
                    //请求成功
                    mTicketResult = response.body();
                    LogUtils.d(TAG,"result " + mTicketResult);
                    //通知UI更新
                    onTicketLoadedSuccess();
                } else {
                    //请求失败
                    onLoadedTicketError();
                }
            }

            @Override
            public void onFailure(Call<TicketResult> call, Throwable t) {
                Log.d(TAG,"getTicket--> error msg "+t);
                //失败
                onLoadedTicketError();
            }
        });
    }


}
