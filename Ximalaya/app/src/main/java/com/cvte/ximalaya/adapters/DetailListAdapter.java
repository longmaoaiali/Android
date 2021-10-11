package com.cvte.ximalaya.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cvte.ximalaya.R;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2020/9/9.
 */

public class DetailListAdapter extends RecyclerView.Adapter<DetailListAdapter.InnerHolder> {

    private List<Track> mDetailData = new ArrayList<>();
    private SimpleDateFormat mSimpleDateFormat= new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat mPlayDurationFormat = new SimpleDateFormat("mm:ss");
    private ItemClickListener mItemClickListener = null;
    private ItemLongClickListener mItemLongClickListener;

    @Override
    public InnerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_detail,parent,false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(InnerHolder holder, final int position) {
        final Track track = mDetailData.get(position);

        final View itemVIew = holder.itemView;
        TextView orderTv = itemVIew.findViewById(R.id.order_text);
        TextView detailItemTitle = itemVIew.findViewById(R.id.detail_item_title);
        TextView detailItemPlayCount = itemVIew.findViewById(R.id.detail_item_play_count);
        TextView detailItemPlayDuration = itemVIew.findViewById(R.id.detail_item_play_duration);
        TextView detailItemUpdateTime = itemVIew.findViewById(R.id.detail_item_update_time);

        orderTv.setText((position+1)+"");
        detailItemTitle.setText(track.getTrackTitle());
        detailItemPlayCount.setText(track.getPlayCount()+"");

        int milSeconds = track.getDuration()*1000;
        String duration = mPlayDurationFormat.format(milSeconds);
        detailItemPlayDuration.setText(duration);

        String updateTime = mSimpleDateFormat.format(track.getUpdatedAt());
        detailItemUpdateTime.setText(updateTime);

        itemVIew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"you click "+position+" item",Toast.LENGTH_SHORT).show();
                if (mItemClickListener != null) {
                    //点击Item时需要传入数据
                    mItemClickListener.onItemClick(mDetailData,position);
                }
            }
        });

        itemVIew.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mItemLongClickListener != null) {
                    mItemLongClickListener.onItemLongClick(track);
                }
                return true;
            }
        });


    }




    @Override
    public int getItemCount() {
        return mDetailData.size();
    }

    public void setData(List<Track> tracks) {
        mDetailData.clear();
        mDetailData.addAll(tracks);
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(View itemView) {
            super(itemView);
        }
    }


    public void setItemClickListener(ItemClickListener listener){
        this.mItemClickListener = listener;
    }

    public interface ItemClickListener{
        void onItemClick(List<Track> detailData, int position);
    }
    
    public void setItemLongClickListener(ItemLongClickListener listener){
        this.mItemLongClickListener = listener;
    }
    
    public interface ItemLongClickListener{
        void onItemLongClick(Track track);
    }
    
}
