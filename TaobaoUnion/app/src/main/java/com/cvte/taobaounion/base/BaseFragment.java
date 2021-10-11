package com.cvte.taobaounion.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.cvte.taobaounion.R;

import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by user on 2020/10/21.
 */

public abstract class BaseFragment extends Fragment {

    private Unbinder mBind;
    private FrameLayout mBaseContainer;

    private State currentState = State.NONE;
    private View mSuccessView;
    private View mLoadingView;
    private View mErrorView;
    private View mEmptyView;

    public enum State {
        NONE,LOADING,SUCCESS,ERROR,EMPTY
    }

    @OnClick(R.id.network_tips_text)
    public void retry(){
        /*点击网络错误重试*/
        onRetryClick();
    }


    protected void onRetryClick() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return loadTootView(inflater,container,savedInstanceState);
    }

    protected  View loadTootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView = loadRootView(inflater,container);
        mBaseContainer = rootView.findViewById(R.id.base_container);
        loadStateView(inflater,container);


        mBind = ButterKnife.bind(this,rootView);
        initView(rootView);
        initViewListener();
        initPresenter();
        loadData();
        return rootView;
    }

    protected void initViewListener() {
    }

    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return  inflater.inflate(R.layout.base_fragment_layout,container,false);
    }

    /**
     * 加载各种状态的VIew
     * @param inflater
     * @param container
     */
    private void loadStateView(LayoutInflater inflater, ViewGroup container) {
        mSuccessView = loadSuccessView(inflater,container);
        mBaseContainer.addView(mSuccessView);

        //Loading View
        mLoadingView = loadLoadingView(inflater,container);
        mBaseContainer.addView(mLoadingView);

        //ERROR View
        mErrorView = loadErrorView(inflater,container);
        mBaseContainer.addView(mErrorView);

        //EMPTY View
        mEmptyView = loadEmptyView(inflater,container);
        mBaseContainer.addView(mEmptyView);

        setUpState(State.NONE);
    }

    /*子类通过该方法切换页面状态*/
    public void setUpState(State state){
        this.currentState = state;
        mSuccessView.setVisibility(currentState==State.SUCCESS? View.VISIBLE:View.GONE);
        mLoadingView.setVisibility(currentState==State.LOADING? View.VISIBLE:View.GONE);
        mErrorView.setVisibility(currentState==State.ERROR? View.VISIBLE:View.GONE);
        mEmptyView.setVisibility(currentState==State.EMPTY? View.VISIBLE:View.GONE);
    }

    protected View loadEmptyView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_empty,container,false);
    }

    protected View loadErrorView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_error,container,false);
    }

    protected View loadLoadingView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_loading,container,false);
    }


    protected View loadSuccessView(LayoutInflater inflater,ViewGroup container){
        int resId = getRootVireResId();
        return inflater.inflate(resId,container,false);
    }


    protected void initView(View rootView) {
    }

    protected void initPresenter() {

    }

    protected  void loadData(){
        //subClass override
        //并非所有子类都需要loadData 不需要定义抽象方法
    }

    protected abstract int getRootVireResId();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mBind != null) {
            mBind.unbind();
        }
    }
}
