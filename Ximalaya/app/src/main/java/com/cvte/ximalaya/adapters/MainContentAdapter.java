package com.cvte.ximalaya.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.cvte.ximalaya.utils.FragmentCreator;
import com.cvte.ximalaya.utils.LogUtil;

/**
 * Created by user on 2020/9/2.
 */

public class MainContentAdapter extends FragmentPagerAdapter {
    private static final String TAG = "MainContentAdapter";

    public MainContentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        LogUtil.d(TAG,"ViewPager Adapter getItem " + position);
        return FragmentCreator.getFragment(position);
    }

    @Override
    public int getCount() {
        //LogUtil.d(TAG,"ViewPager Adapter get Item count "+FragmentCreator.PAGE_COUNT);
        return FragmentCreator.PAGE_COUNT;
    }
}
