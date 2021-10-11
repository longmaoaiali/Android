package com.cvte.ximalaya.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cvte.ximalaya.R;
import com.cvte.ximalaya.utils.LogUtil;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2020/9/3.
 */

public class RecommendListAdapter extends RecyclerView.Adapter<RecommendListAdapter.InnerHolder> {
    private static final String TAG = "RecommendListAdapter";
    private List<Album> mData = new ArrayList<>();
    private OnRecommendItemClickListner mOnRecommendItemClickListner;
    private OnRecommendItemLongClickListner mLongClickListener;

    @Override
    public InnerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        /*找到ItemView*/
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend,parent,false);

        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(InnerHolder holder, final int position) {
        /*设置数据*/
        holder.itemView.setTag(position);
        /*给每个Item设置监听器*/
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //LogUtil.d(TAG,"holder.itemView click--->"+position);
                LogUtil.d(TAG,"holder.itemView click--->"+v.getTag());
                if (mOnRecommendItemClickListner != null) {
                    int cliclPosition = (Integer) v.getTag();
                    mOnRecommendItemClickListner.onItemClick(cliclPosition,mData.get(cliclPosition));
                }
            }
        });
        /*将请求到的数据设置到对应的item中*/
        holder.setData(mData.get(position));

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mLongClickListener != null) {
                    int cliclPosition = (Integer) v.getTag();
                    mLongClickListener.onItemLongClick(cliclPosition,mData.get(cliclPosition));
                }
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public void setData(List<Album> albumList) {
        if (mData != null) {
            //清楚上次的数据
            mData.clear();
            mData.addAll(albumList);
        }
        //更新UI
        /*会调用自己的方法
        * 1.onCreateViewHolder 得到itemView
        * 2.绑定数据与对应的itemView,设置好数据.*/
        notifyDataSetChanged();
    }

    public int getDatasize() {
        return mData.size();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(View itemView) {
            super(itemView);
        }

        public void setData(Album album) {
            //找到item控件，设置数据
            //专辑的封面 标题 描述 播放数量 专辑集数
            ImageView albumCoverIv = (ImageView) itemView.findViewById(R.id.album_cover);
            TextView albumTitleIv = itemView.findViewById(R.id.album_title_tv);
            TextView albumDescriptionIv = itemView.findViewById(R.id.album_description_tv);
            TextView albumPlayCountIv = itemView.findViewById(R.id.play_count_tv);
            TextView albumContentSizeIv = itemView.findViewById(R.id.album_content_size);

            albumTitleIv.setText(album.getAlbumTitle());
            albumDescriptionIv.setText(album.getAlbumIntro());
            albumPlayCountIv.setText(album.getPlayCount()+"");
            albumContentSizeIv.setText(album.getIncludeTrackCount()+"");
            //
            /*图片的加载使用开源框架picasso 将图片加载到ImageView中*/
            if (album.getCoverUrlLarge() != null && albumCoverIv != null) {
                Picasso.get().load(album.getCoverUrlLarge()).into(albumCoverIv);
            }

        }
    }
    
    public void setOnRecommendItemClickListner(OnRecommendItemClickListner listner)
    {
        this.mOnRecommendItemClickListner = listner;
    }

    public interface OnRecommendItemClickListner{
        //void onItemClick(int position);

        void onItemClick(int cliclPosition, Album album);
    }

    public void setOnRecommendItemLongClickListner(OnRecommendItemLongClickListner listner){
        this.mLongClickListener = listner;
    }
    
    public interface OnRecommendItemLongClickListner{
        //void onItemClick(int position);

        void onItemLongClick(int cliclPosition, Album album);
    }


}
