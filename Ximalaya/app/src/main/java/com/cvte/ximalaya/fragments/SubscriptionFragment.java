package com.cvte.ximalaya.fragments;

import android.app.Application;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.cvte.ximalaya.DetailActivity;
import com.cvte.ximalaya.R;
import com.cvte.ximalaya.adapters.RecommendListAdapter;
import com.cvte.ximalaya.base.BaseApplication;
import com.cvte.ximalaya.base.BaseFragment;
import com.cvte.ximalaya.interfaces.ISubscriptionCallback;
import com.cvte.ximalaya.interfaces.ISubscriptionPresenter;
import com.cvte.ximalaya.presenters.AlbumDetialPresenter;
import com.cvte.ximalaya.presenters.RecommendPresenter;
import com.cvte.ximalaya.presenters.SubscriptionPresenter;
import com.cvte.ximalaya.utils.LogUtil;
import com.cvte.ximalaya.views.ConfirmDialog;
import com.cvte.ximalaya.views.UILoader;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.Collections;
import java.util.List;

/**
 * Created by user on 2020/9/2.
 */

public class SubscriptionFragment extends BaseFragment implements ISubscriptionCallback, RecommendListAdapter.OnRecommendItemClickListner, RecommendListAdapter.OnRecommendItemLongClickListner {

    private static final String TAG = "SubscriptionFragment";
    private RecyclerView mSubList;
    private ISubscriptionPresenter mSubscriptionPresenter;
    private RecommendListAdapter mRecommendListAdapter;
    private Album mCurrentAlbum = null;
    private UILoader mUiLoader;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        LogUtil.d(TAG,"SubscriptionFragment --> onSubViewLoaded");
        FrameLayout rootView = (FrameLayout)layoutInflater.inflate(R.layout.fragment_subscription,container,false);

        if (mUiLoader == null) {
            mUiLoader = new UILoader(container.getContext()) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView();
                }

                @Override
                protected View getEmptyView() {
                    //todo：自定义覆盖View
                    return super.getEmptyView();
                }
            };

            if (mUiLoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
            }
            rootView.addView(mUiLoader);
        }

        return rootView;
    }

    //mUILoader 创建一个成功的View
    private View createSuccessView() {
        //todo createSuccessView
        View itemView = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.item_subscription,null);

        TwinklingRefreshLayout refreshLayout = itemView.findViewById(R.id.over_scroll_view);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadmore(false);

        mSubList = itemView.findViewById(R.id.sub_list);
        mSubList.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        mSubList.addItemDecoration(new RecyclerView.ItemDecoration() {
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
        mRecommendListAdapter.setOnRecommendItemClickListner(this);
        mRecommendListAdapter.setOnRecommendItemLongClickListner(this);
        mSubList.setAdapter(mRecommendListAdapter);

        mSubscriptionPresenter = SubscriptionPresenter.getInstance();
        mSubscriptionPresenter.registerViewCallback(this);
        mSubscriptionPresenter.getSubscriptionList();

        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        }
        return itemView;
    }

    @Override
    public void onAddResult(boolean isSuccess) {

    }

    @Override
    public void onDeleteResult(boolean isSuccess) {
        //删除时的UI回调
        Toast.makeText(BaseApplication.getAppContext(),isSuccess?R.string.cancel_sub_success:R.string.cancel_sub_failed,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSubscriptionsLoaded(List<Album> albums) {
        if (albums.size()==0) {
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
        }else{
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
            }
        }

        if (mRecommendListAdapter != null) {
            //Collections.reverse(albums);
            mRecommendListAdapter.setData(albums);
        }
    }

    @Override
    public void onItemClick(int cliclPosition, Album album) {
        this.mCurrentAlbum = album;
        /*将专辑数据给到detail实现类*/
        AlbumDetialPresenter.getInstance().setAlbumDetail(album);
        Intent intent = new Intent(getContext(), DetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(int cliclPosition, Album album) {
        this.mCurrentAlbum = album;
        //订阅item的长按点击事件 todo
        LogUtil.d(TAG,"onItemLongClick-->");
        ConfirmDialog confirmDialog = new ConfirmDialog(getActivity());
        confirmDialog.setonDialogListener(new ConfirmDialog.onDialogListener() {
            @Override
            public void onCancelSub() {
                //todo:取消订阅
                LogUtil.d(TAG,"cancel sub");
                if (mCurrentAlbum != null && mSubscriptionPresenter != null) {
                    mSubscriptionPresenter.deleteSubscription(mCurrentAlbum);
                }
            }

            @Override
            public void onGiveUp() {
                //donothing
                LogUtil.d(TAG,"give up");
            }
        });
        confirmDialog.show();
    }
}
