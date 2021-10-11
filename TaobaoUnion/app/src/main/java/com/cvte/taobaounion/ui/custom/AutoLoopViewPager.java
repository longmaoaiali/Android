package com.cvte.taobaounion.ui.custom;

import android.content.Context;
import android.media.AudioAttributes;
import android.util.AttributeSet;

import androidx.viewpager.widget.ViewPager;

/**
 * Created by user on 2020/11/3.
 */

/**
 * 自动轮播
 */
public class AutoLoopViewPager extends ViewPager{

    public static final int DEFAULT_DURATION = 3000;

    private int mDuration = DEFAULT_DURATION;

    public AutoLoopViewPager(Context context) {
        super(context);
    }

    public AutoLoopViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDuration(int duration){
        mDuration = duration;
    }

    private boolean isLoop = false;

    public void startLoop(){
        isLoop = true;
        post(new Runnable() {
            @Override
            public void run() {
                int currentItem = getCurrentItem();
                currentItem++;
                setCurrentItem(currentItem);
                if (isLoop) {
                    postDelayed(this,mDuration);
                }
            }
        });
    }

    public void stopLoop(){
        isLoop = false;
    }
}
