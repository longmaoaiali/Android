package com.cvte.androidnetwork.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cvte.androidnetwork.R;
import com.cvte.androidnetwork.domain.GetTextItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2020/8/26.
 */

public class GetResultListAdapter extends RecyclerView.Adapter<GetResultListAdapter.InnerHolder>{

    private List<GetTextItem.DataBean> mData = new ArrayList<GetTextItem.DataBean>();

    @Override
    public InnerHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_get_text,parent,false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(InnerHolder holder, int position) {
        //将layout中的组件与Data数据绑定
        View itemView = holder.itemView;
        TextView titleTv = itemView.findViewById(R.id.item_title);
        GetTextItem.DataBean dataBean = mData.get(position);
        titleTv.setText(dataBean.getTitle());

        ImageView cover = itemView.findViewById(R.id.item_iv);
        //使用框架去加载URL定位的图片
        Glide.with(itemView.getContext()).load("http://10.0.2.2:9102" + mData.get(position).getCover()).into(cover);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setData(GetTextItem data) {
        mData.clear();
        mData.addAll(data.getData());
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(View itemView) {
            super(itemView);
        }
    }
}
