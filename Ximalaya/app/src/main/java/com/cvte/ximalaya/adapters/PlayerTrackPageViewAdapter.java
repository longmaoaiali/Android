package com.cvte.ximalaya.adapters;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cvte.ximalaya.R;
import com.cvte.ximalaya.utils.LogUtil;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2020/9/14.
 */

public class PlayerTrackPageViewAdapter extends PagerAdapter{

    private List<Track> mData = new ArrayList<>();
    private static final String TAG = "PlayerTrackPageViewAdapter";

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //track_page_item
        View itemView = LayoutInflater.from(container.getContext()).inflate(R.layout.item_track_pager,container,false);
        container.addView(itemView);

        ImageView item = itemView.findViewById(R.id.track_page_item);
        Track track = mData.get(position);
        String coverUrlSmall =  track.getCoverUrlSmall();
        //图片的URL可能为空
        LogUtil.d(TAG,"coverUrlSmall is " + coverUrlSmall);
        if (item != null && !coverUrlSmall.isEmpty()) {
            Picasso.get().load(coverUrlSmall).into(item);
        }else{
            LogUtil.d(TAG,"ImageView or coverUrlLarge is null ");
        }
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    public void setData(List<Track> list) {
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();
    }
}
