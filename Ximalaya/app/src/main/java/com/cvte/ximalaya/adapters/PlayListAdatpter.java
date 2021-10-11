package com.cvte.ximalaya.adapters;

import android.nfc.Tag;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cvte.ximalaya.R;
import com.cvte.ximalaya.base.BaseApplication;
import com.cvte.ximalaya.utils.LogUtil;
import com.cvte.ximalaya.views.PlayerPopWindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2020/9/17.
 */

public class PlayListAdatpter extends RecyclerView.Adapter<PlayListAdatpter.InnerHolder> {

    private List<Track> mDatas = new ArrayList<>();
    private int mPlayingIndex = 0;
    private PlayerPopWindow.PlayListItemClickListener mItemListener = null;
    private static final String TAG="PlayListAdatpter";

    @Override
    public InnerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //得到Recycler 的itemView
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_play_list,parent,false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(InnerHolder holder, final int position) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //调用PlayerActivity的实现的onItemClick方法
                mItemListener.onItemClick(position);
            }
        });

        Track track = mDatas.get(position);
        TextView titleTv = holder.itemView.findViewById(R.id.play_list_item_title);
        //设置数据的title
        titleTv.setText(track.getTrackTitle());
        titleTv.setTextColor(BaseApplication.getAppContext().getColor(mPlayingIndex==position ?
            R.color.colorAccent : R.color.colorGravy));

        //设置图片
        View playingImage = holder.itemView.findViewById(R.id.play_list_item_image);
        playingImage.setVisibility(mPlayingIndex == position ? View.VISIBLE : View.GONE);


    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void setData(List<Track> data) {
        mDatas.clear();
        mDatas.addAll(data);
        notifyDataSetChanged();
    }

    public void setCurrentPlayPosition(int position) {
        LogUtil.d(TAG,"setCurrentPlayPosition --> "+position);
        mPlayingIndex = position;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(PlayerPopWindow.PlayListItemClickListener listener) {
        this.mItemListener = listener;
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(View itemView) {
            super(itemView);
        }
    }
}
