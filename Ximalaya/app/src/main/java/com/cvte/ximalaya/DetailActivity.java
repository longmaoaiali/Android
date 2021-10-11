package com.cvte.ximalaya;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cvte.ximalaya.adapters.DetailListAdapter;
import com.cvte.ximalaya.base.BaseActivity;
import com.cvte.ximalaya.base.BaseApplication;
import com.cvte.ximalaya.interfaces.IAlbumDetailViewCallback;
import com.cvte.ximalaya.interfaces.IPlayerCallback;
import com.cvte.ximalaya.interfaces.ISubscriptionCallback;
import com.cvte.ximalaya.presenters.AlbumDetialPresenter;
import com.cvte.ximalaya.presenters.PlayerPresenter;
import com.cvte.ximalaya.presenters.SubscriptionPresenter;
import com.cvte.ximalaya.utils.ImageBlur;
import com.cvte.ximalaya.utils.LogUtil;
import com.cvte.ximalaya.views.UILoader;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.bezierlayout.BezierLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2020/9/7.
 */

public class DetailActivity extends BaseActivity implements IAlbumDetailViewCallback, UILoader.OnRetryClickListener, DetailListAdapter.ItemClickListener, IPlayerCallback, ISubscriptionCallback {

    private ImageView mLargeCover;
    private ImageView mSmallCover;
    private TextView mAlbumTitle;
    private TextView mAlbumAuthor;
    private AlbumDetialPresenter mAlbumDetialPresenter;
    private static final String TAG = "DetailActivity";

    private int mCurrentPage = 1;
    private RecyclerView mRecyclerViewDeatilListView;
    private DetailListAdapter mDetailListAdapter;
    private FrameLayout mDetailListContainer;
    private UILoader mUiLoader;
    private long mCurrentId = -1;
    private ImageView mPlayerControlIcon;
    private TextView mPlayerControlText;
    private PlayerPresenter mPlayerPresenter;
    private List<Track> mCurrentTracks = new ArrayList<>();

    private final static int DEFAULT_PLAY_INDEX = 0;
    private TwinklingRefreshLayout mTwinklingRefreshLayout;
    private boolean mIsLoaderMore = false;
    private String mCurrentTrackTitle;
    private TextView mSubBtn;
    private SubscriptionPresenter mSubscriptionPresenter;
    private Album mCurrentAlbum;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        /*设置顶部透明*/
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        initView();
        //专辑详情的是实现类
        mAlbumDetialPresenter = AlbumDetialPresenter.getInstance();
        mAlbumDetialPresenter.registerViewCallback(this);

        //播放器的实现类
        mPlayerPresenter = PlayerPresenter.getInstance();
        mPlayerPresenter.registerViewCallback(this);

        //订阅相关的类
        mSubscriptionPresenter = SubscriptionPresenter.getInstance();
        mSubscriptionPresenter.registerViewCallback(this);
        //mSubscriptionPresenter.onSubListLoaded();
        initEventListener();

        //设置订阅按钮的状态
        updateSubState();
        updatePlayState(mPlayerPresenter.isplay());
    }

    private void updateSubState() {
        if (mSubscriptionPresenter != null) {
            boolean isSub = mSubscriptionPresenter.isSub(mCurrentAlbum);
            mSubBtn.setText(isSub?R.string.cancel_sub_tips_text:R.string.sub_tips_text);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAlbumDetialPresenter != null) {
            mAlbumDetialPresenter.unregisterViewCallback(this);
        }

        if (mPlayerPresenter != null) {
            mPlayerPresenter.unregisterViewCallback(this);
        }

        if (mSubscriptionPresenter != null) {
            mSubscriptionPresenter.unregisterViewCallback(this);
        }
    }

    private void updatePlayState(boolean isplay) {
        if (mPlayerControlIcon != null && mPlayerControlText != null) {
            mPlayerControlIcon.setImageResource(isplay? R.drawable.selector_play_control_pause:R.drawable.selector_play_control_play);
            //mPlayerControlText.setText(isplay?R.string.PLAYER_PLAYING:R.string.PLAYER_PAUSING);
            //todo:显示播放的标题
            if (!isplay) {
                mPlayerControlText.setText(R.string.PLAYER_PAUSING);
            }else{
                if (!TextUtils.isEmpty(mCurrentTrackTitle)) {
                    mPlayerControlText.setText(mCurrentTrackTitle);
                }
            }
        }
    }

    private void initEventListener() {
        mPlayerControlIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    //TODO：判断是否有播放列表
                    boolean has = mPlayerPresenter.hasPlayList();
                    if (has) {
                        handlePLayList();
                    }else{
                        handleNoPLayList();
                    }
                }
            }
        });

        mSubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSubscriptionPresenter != null) {
                    boolean isSub = mSubscriptionPresenter.isSub(mCurrentAlbum);
                    if (isSub) {
                        mSubscriptionPresenter.deleteSubscription(mCurrentAlbum);
                    }else{
                        mSubscriptionPresenter.addSubscription(mCurrentAlbum);
                    }
                }
            }
        });

    }

    private void handleNoPLayList() {
        mPlayerPresenter.setPlayerList(mCurrentTracks,DEFAULT_PLAY_INDEX);
    }

    private void handlePLayList() {
        if (mPlayerPresenter.isplay()) {
            //正在播放就暂停
            mPlayerPresenter.pause();
            mPlayerPresenter.onPlayPause();
        } else{
            mPlayerPresenter.play();
            mPlayerPresenter.onPlayStart();
        }
    }

    private void initView() {
        mDetailListContainer = this.findViewById(R.id.detail_list_container);

        if (mUiLoader == null) {
            mUiLoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }
            };
            mDetailListContainer.removeAllViews();
            mDetailListContainer.addView(mUiLoader);
            mUiLoader.setOnRetryClickListener(DetailActivity.this);
        }

        mLargeCover = this.findViewById(R.id.iv_large_cover);
        mSmallCover = this.findViewById(R.id.iv_small_cover);
        mAlbumTitle = this.findViewById(R.id.tv_album_title);
        mAlbumAuthor = this.findViewById(R.id.tv_album_author);

        mPlayerControlIcon = this.findViewById(R.id.detail_play_control);
        mPlayerControlText = this.findViewById(R.id.detail_play_control_text);
        mPlayerControlText.setSelected(true);

        //订阅按钮
        mSubBtn = this.findViewById(R.id.detail_sub_btn);
    }

    private View createSuccessView(ViewGroup container) {
        View detailListView = LayoutInflater.from(this).inflate(R.layout.itme_detail_list,container,false);
        //RecyclerView 布局管理器 适配器
        mRecyclerViewDeatilListView = detailListView.findViewById(R.id.album_detail_list);

        //TwinklingRefreshLayout下拉或者上拉刷新的实现
        mTwinklingRefreshLayout = detailListView.findViewById(R.id.refresh_layout);


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerViewDeatilListView.setLayoutManager(layoutManager);
        mDetailListAdapter = new DetailListAdapter();
        mDetailListAdapter.setItemClickListener(this);
        mRecyclerViewDeatilListView.setAdapter(mDetailListAdapter);

        //RecyclerView 设置item  间隔
        mRecyclerViewDeatilListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(),2);
                outRect.bottom = UIUtil.dip2px(view.getContext(),2);
                outRect.left = UIUtil.dip2px(view.getContext(),2);
                outRect.right = UIUtil.dip2px(view.getContext(),2);
            }
        });

        //设置刷新的样式
        BezierLayout headerView = new BezierLayout(this);
        mTwinklingRefreshLayout.setHeaderView(headerView);
        mTwinklingRefreshLayout.setMaxHeadHeight(140);
        //设置刷新事件
        mTwinklingRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
//                //todo:加载更多内容
//                if (mAlbumDetialPresenter != null) {
//                    mAlbumDetialPresenter.loadMore();
//                    mIsLoaderMore = true;
//                }
//                BaseApplication.getHandler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(DetailActivity.this,"下拉刷新",Toast.LENGTH_SHORT).show();
//                        mTwinklingRefreshLayout.finishRefreshing();
//                    }
//                },2000);
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                //todo:加载更多内容
                if (mAlbumDetialPresenter != null) {
                    mAlbumDetialPresenter.loadMore();
                    mIsLoaderMore = true;
                }
//                BaseApplication.getHandler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(DetailActivity.this,"上拉加载更多",Toast.LENGTH_SHORT).show();
//                        mTwinklingRefreshLayout.finishLoadmore();
//                    }
//                }, 2000);
            }
        });

        return detailListView;
    }


    @Override
    public void onDetailListLoaded(List<Track> tracks) {

        if (mIsLoaderMore) {
            mTwinklingRefreshLayout.finishLoadmore();
            mIsLoaderMore = false;
        }

        this.mCurrentTracks = tracks;
        /*查看数据结果，根据结果显示*/
        if (tracks==null || tracks.size()==0) {
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
        }

        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        mDetailListAdapter.setData(tracks);

    }

    @Override
    public void onAlbumLoaded(Album album) {
        
        this.mCurrentAlbum = album;
        /*根据album的ID与页码进行 */
        long id = album.getId();
        mCurrentId = id;
        /*获取专辑列表*/
        mAlbumDetialPresenter.getAlbumDetail((int)id, mCurrentPage);
        //拿数据时显示loading状态
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        }

        if (mAlbumTitle != null) {
            mAlbumTitle.setText(album.getAlbumTitle());
        }

        if (mAlbumAuthor != null) {
            mAlbumAuthor.setText(album.getAnnouncer().getNickname());
        }

        //毛玻璃效果
        if (mLargeCover != null) {
            Picasso.get().load(album.getCoverUrlLarge()).into(mLargeCover, new Callback() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                @Override
                public void onSuccess() {
                    Drawable drawable = mLargeCover.getDrawable();
                    if (drawable != null) {
                        ImageBlur.makeBlur(mLargeCover,DetailActivity.this);
                    }
                }

                @Override
                public void onError(Exception e) {
                    LogUtil.d(TAG,"Picasso onError");
                }
            });
        }

        if (mSmallCover != null) {
            Picasso.get().load(album.getCoverUrlSmall()).into(mSmallCover);
        }
    }

    /*网络错误回调的实现*/
    @Override
    public void onNetworkError(int errorCode, String errorMsg) {
        mUiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
    }

    @Override
    public void onLoaderMoreFinished(int size) {
        if (size>0) {
            Toast.makeText(this,"成功加载"+size+"条节目",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"没有更多节目",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefreshFinished(int size) {

    }

    @Override
    public void onRetryClick() {
        LogUtil.d(TAG,"Network error onRetryClick...");
        //网络不佳的重试按钮
        mAlbumDetialPresenter.getAlbumDetail((int)mCurrentId, mCurrentPage);
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        }
    }

    @Override
    public void onItemClick(List<Track> detailData, int position) {
        //先设置播放数据
        PlayerPresenter playerPresenter = PlayerPresenter.getInstance();
        playerPresenter.setPlayerList(detailData,position);

        /*跳转到播放器*/
        //TODO:跳转到播放器
        Intent intent = new Intent(this,PlayerActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPlayStart() {
        /*设置ICON为暂停*/
        updatePlayState(true);
    }

    @Override
    public void onPlayStop() {
        /*设置ICON为播放*/
        updatePlayState(false);
    }

    @Override
    public void onPlayPause() {
        updatePlayState(false);
    }

    @Override
    public void onPlayNext(Track track) {

    }

    @Override
    public void onPlayPre(Track track) {

    }

    @Override
    public void onPlayError() {

    }

    @Override
    public void onListLoaded(List<Track> list) {

    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {

    }

    @Override
    public void onPlayProgressChange(int currentProgress, int total) {

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdLoadFinshed() {

    }

    @Override
    public void onTrackUpdate(Track track, int index) {
        if (track != null) {
            mCurrentTrackTitle = track.getTrackTitle();
            if (!TextUtils.isEmpty(mCurrentTrackTitle) && mPlayerControlText != null) {
                mPlayerControlText.setText(mCurrentTrackTitle);
            }
        }

    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }

    @Override
    public void onAddResult(boolean isSuccess) {
        //如果成功订阅就文字显示取消订阅
        if (isSuccess) {
            mSubBtn.setText(R.string.cancel_sub_tips_text);
        }
        String tipsText = isSuccess?"订阅成功":"订阅失败";
        Toast.makeText(this,tipsText,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteResult(boolean isSuccess) {
        //如果成功订阅就文字显示取消订阅
        if (isSuccess) {
            mSubBtn.setText(R.string.sub_tips_text);
        }
        String tipsText = isSuccess?"取消订阅成功":"取消订阅失败";
        Toast.makeText(this,tipsText,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSubscriptionsLoaded(List<Album> albums) {
        //detail界面不需要处理
    }
}
