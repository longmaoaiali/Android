package com.cvte.ximalaya.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cvte.ximalaya.DetailActivity;
import com.cvte.ximalaya.R;
import com.cvte.ximalaya.adapters.RecommendListAdapter;
import com.cvte.ximalaya.base.BaseFragment;
import com.cvte.ximalaya.interfaces.IRecommendViewCallback;
import com.cvte.ximalaya.presenters.AlbumDetialPresenter;
import com.cvte.ximalaya.presenters.RecommendPresenter;
import com.cvte.ximalaya.utils.LogUtil;
import com.cvte.ximalaya.views.UILoader;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

/**
 * Created by user on 2020/9/2.
 */

public class RecommendFragment extends BaseFragment implements IRecommendViewCallback, UILoader.OnRetryClickListener, RecommendListAdapter.OnRecommendItemClickListner {

    private static final String TAG = "RecommendFragment";
    private View mRootView;
    private RecyclerView mRecommendRv;
    private RecommendListAdapter mRecommendListAdapter;
    private RecommendPresenter mRecommendPresenter;
    private UILoader mUiLoader;

    @Override
    protected View onSubViewLoaded(final LayoutInflater layoutInflater, ViewGroup container) {
        LogUtil.d(TAG,"RecommendFragment --> onSubViewLoaded");

        mUiLoader = new UILoader(getContext()) {
            @Override
            protected View getSuccessView(ViewGroup container) {
                return createSuccessView(layoutInflater,container);
            }
        };
        /*使用XAMALAYA SDK拿数据 SDK 3.10.6 获取猜你喜欢专辑*/
        /*view绘制里面请求远程数据可能会出现，后面在处理*/
        //getRecommendData();
        /*获取逻辑层的实例，注册回调，实现回调方法 调用逻辑层get数据的方法*/
        mRecommendPresenter = RecommendPresenter.getInstance();
        mRecommendPresenter.registerViewCallback(this);
        mRecommendPresenter.getRecommendList();

        if (mUiLoader.getParent() instanceof ViewGroup) {
            ((ViewGroup)mUiLoader.getParent()).removeView(mUiLoader);
        }

        mUiLoader.setOnRetryClickListener(this);
        //返回View
        /*应该返回UiLoader，而不是mRootView 这里也是被坑了好久*/
        return mUiLoader;
        //return mRootView;

    }

    private View createSuccessView(LayoutInflater layoutInflater, ViewGroup container) {
        /*加载View*/
        mRootView = layoutInflater.inflate(R.layout.fragment_recommand,container,false);
        /*1.找到控件
        * 2,设置布局管理器 垂直方向
        * 3.创建适配器
        * 4.向适配器设置数据
        * */
        mRecommendRv = mRootView.findViewById(R.id.recommend_list);

        TwinklingRefreshLayout twinklingRefreshLayout = mRootView.findViewById(R.id.over_scroll_view);
        twinklingRefreshLayout.setPureScrollModeOn();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecommendRv.setLayoutManager(linearLayoutManager);

        mRecommendRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                /*这里是需要像素值 使用magic*/
                outRect.top = UIUtil.dip2px(view.getContext(),5);
                outRect.bottom = UIUtil.dip2px(view.getContext(),5);
                outRect.left = UIUtil.dip2px(view.getContext(),5);
                outRect.right = UIUtil.dip2px(view.getContext(),5);
            }
        });

        mRecommendListAdapter = new RecommendListAdapter();
        mRecommendRv.setAdapter(mRecommendListAdapter);
        mRecommendListAdapter.setOnRecommendItemClickListner(this);
        return mRootView;
    }



    //private void updateRecommendUI(List<Album> albumList) {}

    /*逻辑层得到数据以后，回调UI显示数据的实现接口*/
    @Override
    public void onRecommendListLoaded(List<Album> result) {
        //当我们获取到推荐内容时，方法会被回调
        LogUtil.d(TAG,"onRecommendListLoaded");
        mRecommendListAdapter.setData(result);
        mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
    }

    @Override
    public void onNetworkError() {
        LogUtil.d(TAG,"onNetworkError");
        mUiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
    }

    @Override
    public void onEmpty() {
        LogUtil.d(TAG,"onEmpty");
        mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
    }

    @Override
    public void onLoading() {
        LogUtil.d(TAG,"onLoading");
        mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
    }

    @Override
    public void onLoaderMore(List<Album> result) {

    }

    @Override
    public void onRefreshMoreMore(List<Album> result) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //取消回调接口的注册
        if (mRecommendPresenter != null) {
            mRecommendPresenter.unregisterViewCallback (this);
        }
    }

    @Override
    public void onRetryClick() {
        //网络不佳，用户点击重试
        if (mRecommendPresenter != null) {
            mRecommendPresenter.getRecommendList();
        }
    }

    @Override
    public void onItemClick(int position,Album album) {
        //recyclerView item被点击了 20个Item,序号是0-19
        /*将专辑数据给到detail实现类*/
        AlbumDetialPresenter.getInstance().setAlbumDetail(album);
        Intent intent = new Intent(getContext(), DetailActivity.class);
        startActivity(intent);
    }
}
