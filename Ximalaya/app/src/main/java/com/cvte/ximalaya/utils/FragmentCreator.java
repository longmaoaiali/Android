package com.cvte.ximalaya.utils;

import com.cvte.ximalaya.base.BaseFragment;
import com.cvte.ximalaya.fragments.HistoryFragment;
import com.cvte.ximalaya.fragments.RecommendFragment;
import com.cvte.ximalaya.fragments.SubscriptionFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 2020/9/2.
 */

public class FragmentCreator {
    public final static int INDEX_RECOMMEND = 0;
    public final static int INDEX_SUBSCRIPTION = 1;
    public final static int INDEX_HISTORY = 2;

    public final static int PAGE_COUNT=3;
    private static final String TAG = "FragmentCreator";

    private static Map<Integer, BaseFragment> sCache = new HashMap<>();

    public static BaseFragment getFragment(int index){
        LogUtil.d(TAG,"getFragment index--> " + index);
        BaseFragment baseFragment = sCache.get(index);
        if(baseFragment != null){
            return baseFragment;
        }

        switch (index){
            case INDEX_RECOMMEND:
                baseFragment = new RecommendFragment();
                break;
            case INDEX_SUBSCRIPTION:
                baseFragment = new SubscriptionFragment();
                break;
            case INDEX_HISTORY:
                baseFragment = new HistoryFragment();
                break;
        }

        sCache.put(index,baseFragment);
        LogUtil.d(TAG,"Fragment index -->"+index+"will return baseFragment");
        return baseFragment;
    }

}
