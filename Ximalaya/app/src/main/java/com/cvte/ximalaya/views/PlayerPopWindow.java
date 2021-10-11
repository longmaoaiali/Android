package com.cvte.ximalaya.views;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cvte.ximalaya.R;
import com.cvte.ximalaya.adapters.PlayListAdatpter;
import com.cvte.ximalaya.base.BaseApplication;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

/**
 * Created by user on 2020/9/16.
 */

public class PlayerPopWindow  extends PopupWindow {

    private final View mPopView;
    private View mCloseBtn;
    private RecyclerView mTrackList;
    private PlayListAdatpter mPlayListAdatpter;
    private TextView mPlayModeTv;
    private ImageView mPlayModeIv;
    private View mPlayModeContainer;
    private PlayActionListener mPlayModeListener=null;
    private View mPlayListOrderContainer;
    private ImageView mPlayListOrderIcon;
    private TextView mPlayListOrderText;

    public PlayerPopWindow() {
        super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //弹出POP界面后再点击可以收回 需要设置如下两个
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);

        mPopView = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.pop_play_list, null, false);

        setContentView(mPopView);
        //设置窗口弹出与退出的动画
        setAnimationStyle(R.style.pop_animation);

        //初始化view的控件
        initView();
        initEventListener();
    }

    private void initEventListener() {
        //点击以后调用dismiss 窗口消失
        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayerPopWindow.this.dismiss();
            }
        });

        mPlayModeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换播放模式
                if (mPlayModeListener != null) {
                    mPlayModeListener.onPlayModeClick();
                }
            }
        });

        mPlayListOrderContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo:切换播放顺序 正序/逆序
                if (mPlayModeListener != null) {
                    mPlayModeListener.onPlayOrderClick();
                }

            }
        });
    }

    private void initView() {
        mCloseBtn = mPopView.findViewById(R.id.play_list_close_btn);
        mTrackList = mPopView.findViewById(R.id.play_list_recycler);
        //找到控件 设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(BaseApplication.getAppContext());
        mTrackList.setLayoutManager(layoutManager);
        //设置适配器
        mPlayListAdatpter = new PlayListAdatpter();
        mTrackList.setAdapter(mPlayListAdatpter);

        //播放模式相关
        mPlayModeTv = mPopView.findViewById(R.id.play_list_play_mode_tv);
        mPlayModeIv = mPopView.findViewById(R.id.player_list_play_mode_iv);

        //mPlayModeContainer 线性布局设置点击事件
        mPlayModeContainer = mPopView.findViewById(R.id.play_list_control_container);

        mPlayListOrderContainer = mPopView.findViewById(R.id.play_list_order_container);

        mPlayListOrderIcon = mPopView.findViewById(R.id.play_list_order_iv);
        mPlayListOrderText = mPopView.findViewById(R.id.play_list_order_tv);

    }

    public void setListData(List<Track> data){
        if (mPlayListAdatpter != null) {
            mPlayListAdatpter.setData(data);
        }
    }

    public void setCurrentPlayPosition(int position){
        if (mPlayListAdatpter != null) {
            mPlayListAdatpter.setCurrentPlayPosition(position);
            //当前item移动至范围之类
            mTrackList.scrollToPosition(position);
        }
    }

    //PlayerActivity 设置监听器
    public void setPlayListItemClickListener(PlayListItemClickListener listener){
        mPlayListAdatpter.setOnItemClickListener(listener);
    }

    public void updatePlayModeImg(XmPlayListControl.PlayMode currentMode) {
        switch (currentMode){
            case PLAY_MODEL_LIST:
                mPlayModeTv.setText(R.string.PLAY_MODEL_LIST);
                mPlayModeIv.setImageResource(R.drawable.selector_player_mode_list_order);
                break;
            case PLAY_MODEL_RANDOM:
                mPlayModeTv.setText(R.string.PLAY_MODEL_RANDOM);
                mPlayModeIv.setImageResource(R.drawable.selector_player_mode_random);
                break;
            case PLAY_MODEL_SINGLE:
                mPlayModeTv.setText(R.string.PLAY_MODEL_SINGLE);
                mPlayModeIv.setImageResource(R.drawable.selector_player_mode_one_loop);
                break;
            case PLAY_MODEL_LIST_LOOP:
                mPlayModeTv.setText(R.string.PLAY_MODEL_LIST_LOOP);
                mPlayModeIv.setImageResource(R.drawable.selector_player_mode_loop);
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                mPlayModeTv.setText(R.string.PLAY_MODEL_SINGLE_LOOP);
                mPlayModeIv.setImageResource(R.drawable.selector_player_mode_one_loop);
                break;
        }
    }

    //todo:更新顺序图标
    public void updateOrderIcon(boolean isOrder){
        mPlayListOrderIcon.setImageResource(isOrder?R.drawable.selector_player_list_order:R.drawable.selector_player_list_order_asc);
        mPlayListOrderText.setText(isOrder?"顺序":"逆序");
    }

    public interface PlayListItemClickListener{
        void onItemClick(int position);
    }

    public void setPlayActionListener(PlayActionListener listener){
        mPlayModeListener = listener;
    }

    public interface PlayActionListener{
        //播放模式点击接口
        void onPlayModeClick();

        //播放列表排序
        void onPlayOrderClick();
    }

}
