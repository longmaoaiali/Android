package com.cvte.ximalaya.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cvte.ximalaya.PlayerActivity;
import com.cvte.ximalaya.R;
import com.cvte.ximalaya.adapters.DetailListAdapter;
import com.cvte.ximalaya.adapters.PlayListAdatpter;
import com.cvte.ximalaya.base.BaseApplication;
import com.cvte.ximalaya.base.BaseFragment;
import com.cvte.ximalaya.interfaces.IHistoryCallback;
import com.cvte.ximalaya.presenters.HistoryPresenter;
import com.cvte.ximalaya.presenters.PlayerPresenter;
import com.cvte.ximalaya.utils.LogUtil;
import com.cvte.ximalaya.views.ConfirmCheckBoxDialog;
import com.cvte.ximalaya.views.UILoader;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

/**
 * Created by user on 2020/9/2.
 */

public class HistoryFragment extends BaseFragment implements IHistoryCallback, DetailListAdapter.ItemClickListener, DetailListAdapter.ItemLongClickListener, ConfirmCheckBoxDialog.onDialogListener {
    private static final String TAG = "HistoryFragment";
    private UILoader mUiLoader;
    private HistoryPresenter mHistoryPresenter;
    private DetailListAdapter mPlayListAdatpter;
    private Track mCurrentTrack = null;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        LogUtil.d(TAG,"HistoryFragment --> onSubViewLoaded");
        //此处遇见大坑忘记返回rootView 返回的是一个NULL
        FrameLayout rootView = (FrameLayout)layoutInflater.inflate(R.layout.fragment_history,container,false);

        if(mUiLoader == null) {
            mUiLoader = new UILoader(BaseApplication.getAppContext()) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }

                //todo：复写empty UILoader 

            };
        } else {
            if(mUiLoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
            }
        }
        //HistoryPresenter
        mHistoryPresenter = HistoryPresenter.getInstance();
        mHistoryPresenter.registerViewCallback(this);
        mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        mHistoryPresenter.listHistory();
        rootView.addView(mUiLoader);
        return rootView;
    }

    private View createSuccessView(ViewGroup container) {
        View successView = LayoutInflater.from(container.getContext()).inflate(R.layout.item_history,container,false);
        TwinklingRefreshLayout refreshLayout = successView.findViewById(R.id.over_scroll_view);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setEnableOverScroll(true);
        //recyclerView.
        RecyclerView historyList = successView.findViewById(R.id.history_list);
        historyList.setLayoutManager(new LinearLayoutManager(container.getContext()));
        //设置item的上下间距
        historyList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(),2);
                outRect.bottom = UIUtil.dip2px(view.getContext(),2);
                outRect.left = UIUtil.dip2px(view.getContext(),2);
                outRect.right = UIUtil.dip2px(view.getContext(),2);
            }
        });
        //设置适配器
        mPlayListAdatpter = new DetailListAdapter();
        mPlayListAdatpter.setItemClickListener(this);
        mPlayListAdatpter.setItemLongClickListener(this);
        historyList.setAdapter(mPlayListAdatpter);
        return successView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mHistoryPresenter != null) {
            mHistoryPresenter.unregisterViewCallback(this);
        }
    }

    @Override
    public void onHistoryLoaded(List<Track> tracks) {
        if(tracks == null || tracks.size() == 0) {
            LogUtil.d(TAG,"mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);");
            mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
        } else {
            //更新数据
            mPlayListAdatpter.setData(tracks);
            mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
    }

    @Override
    public void onItemClick(List<Track> detailData, int position) {
        //设置播放器数据，并且跳转播放器界面
        PlayerPresenter playerPresenter = PlayerPresenter.getInstance();
        playerPresenter.setPlayerList(detailData,position);

        Intent intent = new Intent(getActivity(), PlayerActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(Track track) {
        this.mCurrentTrack = track;
        //todo:长按弹出Dialog询问是否删除历史
        ConfirmCheckBoxDialog confirmCheckBoxDialog = new ConfirmCheckBoxDialog(getActivity());
        confirmCheckBoxDialog.setonDialogListener(this);
        confirmCheckBoxDialog.show();
    }

    @Override
    public void onCancelSub() {
        //donothing
    }

    @Override
    public void onGiveUp(boolean checked) {
        if (mHistoryPresenter != null && mCurrentTrack != null) {
            if (!checked) {
                mHistoryPresenter.delHistory(mCurrentTrack);
            }else {
                mHistoryPresenter.cleanHistory();
            }
        }
    }
}
