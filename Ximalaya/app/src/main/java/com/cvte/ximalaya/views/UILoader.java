package com.cvte.ximalaya.views;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.cvte.ximalaya.R;
import com.cvte.ximalaya.base.BaseApplication;
import com.cvte.ximalaya.utils.LogUtil;

/**
 * Created by user on 2020/9/4.
 */

/*UI加载器去处理网络请求数据后的各种结果*/
 public abstract class UILoader extends FrameLayout {

    private View mLoadingView;
    private View mSuccessView;
    private View mNetworkErrorView;
    private View mEmptyView;
    private static final String TAG="UILoader";
    private OnRetryClickListener mOnRetryClickListener = null;

    public enum UIStatus{
        LOADING,SUCCESS,NETWORK_ERROR,EMPTY,NONE
    }

    public UIStatus mCurrentStatus = UIStatus.NONE;

    /*将super换成this补全多余的参数，使构造都变成同一个调用*/
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public UILoader(@NonNull Context context) {
        this(context,null,0,0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0,0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init();
    }

    public void updateStatus(UIStatus status){
        mCurrentStatus = status;
        LogUtil.d(TAG,"mCurrentStatus " + mCurrentStatus);
        //更新UI,使用BaseApplication里面创建的Handler
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG,"aseApplication.getHandler().post() -->  switchUIByCurrentStatus");
                switchUIByCurrentStatus();
            }
        });
    }

    //初始化UI
    private void init() {
        switchUIByCurrentStatus();
    }



    private void switchUIByCurrentStatus() {
        LogUtil.d(TAG,"switchUIByCurrentStatus=== mCurrentStatus--> "+mCurrentStatus);
        if (mLoadingView == null) {
            /*ctrl+shift+up 向上移动*/
            mLoadingView = getLoadingView();
            addView(mLoadingView);
        }
        //根据状态设置是否可见
        mLoadingView.setVisibility(mCurrentStatus==UIStatus.LOADING?VISIBLE:GONE);

        /*成功的页面*/
        if (mSuccessView == null) {
            mSuccessView = getSuccessView(this);
            addView(mSuccessView);
        }
        //根据状态设置是否可见
        mSuccessView.setVisibility(mCurrentStatus==UIStatus.SUCCESS?VISIBLE:GONE);

        LogUtil.d(TAG,"will judgemene mNetworkErrorView wether null");
        /*网络错误的页面*/
        if (mNetworkErrorView == null) {
            LogUtil.d(TAG,"mNetworkErrorView == null wiil call getNetworkErrorView");
            mNetworkErrorView = getNetworkErrorView();
            addView(mNetworkErrorView);
        }
        //根据状态设置是否可见
        LogUtil.d(TAG,"will call mNetworkErrorView.setVisibility");
        mNetworkErrorView.setVisibility(mCurrentStatus==UIStatus.NETWORK_ERROR?VISIBLE:GONE);
        //mNetworkErrorView.setVisibility(VISIBLE);


        /*数据为空的页面*/
        if (mEmptyView == null) {
            mEmptyView = getEmptyView();
            addView(mEmptyView);
        }
        //根据状态设置是否可见
        mEmptyView.setVisibility(mCurrentStatus==UIStatus.EMPTY?VISIBLE:GONE);

    }

    protected View getEmptyView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty_view,this,false);
    }

    private View getNetworkErrorView() {
        LogUtil.d(TAG,"getNetworkErrorView");
        View networkErrorView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_network_error,this,false);
        networkErrorView.findViewById(R.id.network_error_icon).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //重新获取数据
                if (mOnRetryClickListener != null) {
                    mOnRetryClickListener.onRetryClick();
                }
            }
        });

        return networkErrorView;
    }

    /*成功的页面需要实际的数据，由子类去实现*/
    protected abstract View getSuccessView(ViewGroup container);

    private View getLoadingView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_loading,this,false);
    }

    public void setOnRetryClickListener(OnRetryClickListener listener){
        this.mOnRetryClickListener = listener;
    }

    public interface OnRetryClickListener{
        void onRetryClick();
    }

}

