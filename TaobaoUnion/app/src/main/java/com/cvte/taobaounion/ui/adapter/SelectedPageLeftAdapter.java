package com.cvte.taobaounion.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cvte.taobaounion.R;
import com.cvte.taobaounion.model.domain.SelectedPageCategory;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by user on 2020/11/9.
 */

public class SelectedPageLeftAdapter extends RecyclerView.Adapter<SelectedPageLeftAdapter.InnerHolder> {
    private OnLeftItemClickListener mOnLeftItemClickListener;
    List<SelectedPageCategory.DataBean> mDataBeans = new ArrayList<>();

    int mCurrentSelectedPosition = 0;

    @Override
    public InnerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_page_left,parent,false);

        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(InnerHolder holder, int position) {
        SelectedPageCategory.DataBean dataBean = mDataBeans.get(position);
        TextView itemTitle = holder.itemView.findViewById(R.id.left_category_tv);
        if (mCurrentSelectedPosition == position) {
            itemTitle.setBackgroundColor(itemTitle.getResources().getColor(R.color.colorEEEEEE,null));
        } else {
            itemTitle.setBackgroundColor(itemTitle.getResources().getColor(R.color.colorWhite,null));
        }

        itemTitle.setText(dataBean.getFavorites_title());

        itemTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnLeftItemClickListener != null && mCurrentSelectedPosition != position) {
                    //根据点击事件修改当前选中的位置
                    mCurrentSelectedPosition = position;
                    mOnLeftItemClickListener.onLeftItemClick(dataBean);
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataBeans.size();
    }

    public void setData(SelectedPageCategory categories) {
        List<SelectedPageCategory.DataBean> data = categories.getData();
        if (data != null) {
            this.mDataBeans.clear();
            this.mDataBeans.addAll(data);
            if (mDataBeans.size()>0) {
                //第一次进入activity也需要代码设置点击事件发起网络请求
                mOnLeftItemClickListener.onLeftItemClick(mDataBeans.get(mCurrentSelectedPosition));
            }
            notifyDataSetChanged();
        }
        //mDataBeans = categories.getData();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(View itemView) {
            super(itemView);
        }
    }

    public void setOnLeftItemClickListener(OnLeftItemClickListener listener){
        this.mOnLeftItemClickListener = listener;

    }

    public interface OnLeftItemClickListener{
        void onLeftItemClick(SelectedPageCategory.DataBean item);
    }
}
