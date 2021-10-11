package com.cvte.ximalaya;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cvte.ximalaya.adapters.PlayerTrackPageViewAdapter;
import com.cvte.ximalaya.base.BaseActivity;
import com.cvte.ximalaya.base.BaseApplication;
import com.cvte.ximalaya.interfaces.IPlayerCallback;
import com.cvte.ximalaya.presenters.PlayerPresenter;
import com.cvte.ximalaya.utils.LogUtil;
import com.cvte.ximalaya.views.PlayerPopWindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;

/**
 * Created by user on 2020/9/10.
 */

public class PlayerActivity extends BaseActivity implements IPlayerCallback, ViewPager.OnPageChangeListener {

    private ImageView mControlBtn;
    private PlayerPresenter mPlayerPresenter;
    private SimpleDateFormat mMinFormat = new SimpleDateFormat("mm:ss");
    private SimpleDateFormat mHourFormat = new SimpleDateFormat("hh:mm:ss");
    private TextView mTotalTime;
    private TextView mCurrentTime;
    private SeekBar mPlayerSeekBar;
    private int mCurrentProgress = 0;
    private boolean mIsUserTouchProgressBar = false;
    private ImageView mPlayerPre;
    private ImageView mPlayerNext;
    private TextView mTitleView;
    private String mTrackTitleText;
    private ViewPager mTrackPageView;
    private static final String TAG = "PlayerActivity";
    private PlayerTrackPageViewAdapter mPlayerTrackPageViewAdapter;
    private boolean mIsUserSlidePager = false;
    private ImageView mPlayMode;

    private static Map<XmPlayListControl.PlayMode,XmPlayListControl.PlayMode> sPlayModeMap = new HashMap<>();
    private XmPlayListControl.PlayMode mCurrentMode=PLAY_MODEL_LIST;


    static {
        /*将上一个与下一个模式分别作为key 与 value*/
        sPlayModeMap.put(PLAY_MODEL_LIST, XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP);
        sPlayModeMap.put(XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP, XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM);
        sPlayModeMap.put(XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM, XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE);
        sPlayModeMap.put(XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE, XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP);
        sPlayModeMap.put(XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP, PLAY_MODEL_LIST);
    }

    private View mPlayListBtn;
    private PlayerPopWindow mPlayerPopWindow;
    private ValueAnimator mEnterBgAnimator;
    private ValueAnimator mExitAnimator;
    private boolean playListOrder = true;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_activity);

        //测试一下播放
        mPlayerPresenter = PlayerPresenter.getInstance();
        //playerPresenter.play();

        initView();
        //界面初始化以后再获取数据

        mPlayerPresenter.registerViewCallback(this);

        mPlayerPresenter.getPlayList();
        initViewListener();
        
        initBgAnimation();
        
        /*大坑 有的歌曲会出现无法播放 需要等待播放器准备好才能再播放*/
        //startPlay();

    }

    private void initBgAnimation() {
        mEnterBgAnimator = ValueAnimator.ofFloat(1.0f,0.7f);
        mEnterBgAnimator.setDuration(500);
        mEnterBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float)animation.getAnimatedValue();
                //LogUtil.d(TAG,"value --> "+animation.getAnimatedValue());
                updateBgAlpha(value);
            }
        });


        //退出动画
        mExitAnimator = ValueAnimator.ofFloat(0.7f,1.0f);
        mExitAnimator.setDuration(500);
        mExitAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float)animation.getAnimatedValue();
                //LogUtil.d(TAG,"value --> "+animation.getAnimatedValue());
                updateBgAlpha(value);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unregisterViewCallback(this);
            mPlayerPresenter=null;
        }
    }

//    private void startPlay() {
//        mPlayerPresenter.play();
//    }


    private void initViewListener() {
        mControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断状态，对点击事件进行改变
                if (mPlayerPresenter.isplay()) {
                    mPlayerPresenter.pause();
                }else {
                    mPlayerPresenter.play();
                }
            }
        });

        mPlayerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    //如果是用户点击
                    mCurrentProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsUserTouchProgressBar = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mIsUserTouchProgressBar = false;
                //手离开进度条更新进度
                mPlayerPresenter.seekTo(mCurrentProgress);

            }
        });

        mPlayerPre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayerPresenter.playPre();
            }
        });

        mPlayerNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayerPresenter.playNext();
            }
        });

        //ViewPager 滑动监听事件
        mTrackPageView.addOnPageChangeListener(this);

        //mIsUserSlidePager
        mTrackPageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mIsUserSlidePager = true;
                        break;
                }
                return false;
            }
        });

        mPlayMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换播放模式 TODO:
                XmPlayListControl.PlayMode  playMode = sPlayModeMap.get(mCurrentMode);
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.switchPlayMode(playMode);
                    //mCurrentMode = playMode;
                    //updatePlayModeImg();
                }
            }
        });

        mPlayListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:弹出列表 POPWindow实现
                /*展示播放列表*/
                mPlayerPopWindow.showAtLocation(v, Gravity.BOTTOM,0,0);
                //处理背景变灰 修改透明度
                //updateBgAlpha(0.8f);
                //背景透明度变化
                mEnterBgAnimator.start();

            }
        });

        mPlayerPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //pop窗体消失以后恢复透明度
                //updateBgAlpha(1.0f);
                mExitAnimator.start();
            }
        });

        mPlayerPopWindow.setPlayListItemClickListener(new PlayerPopWindow.PlayListItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //说明播放列表的item被点击
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playByIndex(position);
                }
            }
        });


        mPlayerPopWindow.setPlayActionListener(new PlayerPopWindow.PlayActionListener() {
            @Override
            public void onPlayModeClick() {
                XmPlayListControl.PlayMode  playMode = sPlayModeMap.get(mCurrentMode);
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.switchPlayMode(playMode);
                    //mCurrentMode = playMode;
                    //updatePlayModeImg();
                }
            }

            @Override
            public void onPlayOrderClick() {
                Toast.makeText(PlayerActivity.this,"切换列表顺序",Toast.LENGTH_SHORT).show();

                if (mPlayerPresenter != null) {
                    mPlayerPresenter.revesePlayList();
                }
                //mPlayerPopWindow.updateOrderIcon(playListOrder);
                //playListOrder = !playListOrder;

            }
        });
    }

    //更新透明度
    private void updateBgAlpha(float alpha) {
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.alpha = alpha;
        window.setAttributes(attributes);
    }

    private void updatePlayModeImg() {
        switch (mCurrentMode){
            case PLAY_MODEL_LIST:
                mPlayMode.setImageResource(R.drawable.selector_player_mode_list_order);
                break;
            case PLAY_MODEL_RANDOM:
                mPlayMode.setImageResource(R.drawable.selector_player_mode_random);
                break;
            case PLAY_MODEL_SINGLE:
                mPlayMode.setImageResource(R.drawable.selector_player_mode_one_loop);
                break;
            case PLAY_MODEL_LIST_LOOP:
                mPlayMode.setImageResource(R.drawable.selector_player_mode_loop);
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                mPlayMode.setImageResource(R.drawable.selector_player_mode_one_loop);
                break;
        }
    }

    private void initView() {
        mControlBtn = this.findViewById(R.id.play_or_stop_btn);
        mCurrentTime = this.findViewById(R.id.current_position);
        mTotalTime = this.findViewById(R.id.totalTime);
        mPlayerSeekBar = this.findViewById(R.id.player_progress);
        mPlayerPre = this.findViewById(R.id.play_pre);
        mPlayerNext = this.findViewById(R.id.play_next);
        mTitleView = this.findViewById(R.id.track_title);
        if (mTitleView != null) {
            mTitleView.setText(mTrackTitleText);
        }

        mTrackPageView = this.findViewById(R.id.track_pager_view);
        //ViewPage显示需要创建适配器，设置适配器
        mPlayerTrackPageViewAdapter = new PlayerTrackPageViewAdapter();
        mTrackPageView.setAdapter(mPlayerTrackPageViewAdapter);
        mPlayMode = this.findViewById(R.id.player_mode_switch);

        mPlayListBtn = this.findViewById(R.id.player_list);
        mPlayerPopWindow = new PlayerPopWindow();
    }

    @Override
    public void onPlayStart() {
        //开始播放以后将图片换成暂停的图片
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.drawable.selector_player_stop);
        }
    }

    @Override
    public void onPlayStop() {
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.drawable.selector_player_play);
        }
    }

    @Override
    public void onPlayPause() {
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.drawable.selector_player_play);
        }
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
        LogUtil.d(TAG,"list size is -->"+list.size());
        //Track List回传给UI
        if (mPlayerTrackPageViewAdapter != null) {
            mPlayerTrackPageViewAdapter.setData(list);
        }

        //TODO: 需要将数据给到节目列表一份
        if (mPlayerPopWindow != null) {
            mPlayerPopWindow.setListData(list);
        }
    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {
        mCurrentMode = playMode;

        updatePlayModeImg();
        //也需要POPWindow里面的icon
        mPlayerPopWindow.updatePlayModeImg(mCurrentMode);
    }

    @Override
    public void onPlayProgressChange(int currentProgress, int total) {
        mPlayerSeekBar.setMax(total);
        //更新进度条 更新时间
        String totalDuration;
        String currentDuration;

        if (total>1000*60*60) {
            totalDuration = mHourFormat.format(total);
            currentDuration = mHourFormat.format(currentProgress);
        }else{
            totalDuration = mMinFormat.format(total);
            currentDuration = mMinFormat.format(currentProgress);
        }

        if (mTotalTime != null && "00:00".equals(mTotalTime.getText())) {
            mTotalTime.setText(totalDuration);
        }

        if (mCurrentTime != null) {
            mCurrentTime.setText(currentDuration);
        }
        //随着播放自动更新进度条
        if (!mIsUserTouchProgressBar) {
            //int present = (int) (currentProgress / total * 100);
            mPlayerSeekBar.setProgress(currentProgress);
        }


    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdLoadFinshed() {

    }

    @Override
    public void onTrackUpdate(Track track, int index) {
        LogUtil.d(TAG,"onTrackUpdate update track");
        this.mTrackTitleText = track.getTrackTitle();
        if (mTitleView != null) {
            LogUtil.d(TAG,"set Sound Title"+mTrackTitleText);
            mTitleView.setText(mTrackTitleText);
        }
        //上下切换歌曲时，需要联动切换图片
        if (mTrackPageView != null) {
            mTrackPageView.setCurrentItem(index,true);
        }
        //TODO:设置播放列表的当前位置
        if (mPlayerPopWindow != null) {
            mPlayerPopWindow.setCurrentPlayPosition(index);
        }

    }

    @Override
    public void updateListOrder(boolean isReverse) {
        mPlayerPopWindow.updateOrderIcon(isReverse);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        /*ViewPager页面切换时都会调用到这里来*/
        /* 切歌来切换图片 或者切换图片来切歌都会调用这里 */
        //页面选中时播放切换的内容
        if (mPlayerPresenter != null && mIsUserSlidePager) {
            mPlayerPresenter.playByIndex(position);
        }

        mIsUserSlidePager=false;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
