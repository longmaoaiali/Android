package com.cvte.taobaounion.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.cvte.taobaounion.model.domain.HomePagerContent;
import com.cvte.taobaounion.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.viewpager.widget.PagerAdapter;

/**
 * Created by user on 2020/10/27.
 */

public class LooperPagerAdapter extends PagerAdapter{
    private List<HomePagerContent.DataBean> mDatas = new ArrayList<>();
    private OnLooperPageClickListener mOnLooperPageClickListener = null;

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);

    }

    public int getDataSize(){
        return mDatas.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //处理无限轮播的越界问题
        int realPosition = position % mDatas.size();
        HomePagerContent.DataBean dataBean = mDatas.get(realPosition);
        //ViewGroup.LayoutParams layoutParams = container.getLayoutParams();
        int width = container.getMeasuredWidth();
        int height = container.getMeasuredHeight();
        int ivSize = (width>height?width:height)/2;

        String coverUrl = UrlUtils.getCoverPath(dataBean.getPict_url(),ivSize);
        ImageView iv = new ImageView(container.getContext());

        /*设置图片填充*/
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        iv.setLayoutParams(layoutParams);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Glide.with(container.getContext()).load(coverUrl).into(iv);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnLooperPageClickListener != null) {
                    HomePagerContent.DataBean item = mDatas.get(realPosition);
                    mOnLooperPageClickListener.onLooperItemClick(item);
                }
            }
        });
        container.addView(iv);
        return iv;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setData(List<HomePagerContent.DataBean> contents) {
        mDatas.clear();
        mDatas.addAll(contents);
        notifyDataSetChanged();
    }

    public void setOnLooperPageClickListener(OnLooperPageClickListener listener){
        this.mOnLooperPageClickListener = listener;
    }

    public interface OnLooperPageClickListener {
        void onLooperItemClick(HomePagerContent.DataBean item);
    }
}
