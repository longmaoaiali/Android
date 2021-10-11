package com.cvte.ximalaya.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by user on 2020/9/2.
 */

public abstract class BaseFragment extends Fragment{

    private View mRootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mRootView = onSubViewLoaded(inflater,container);
        return mRootView;

    }

    // 8.54 LayoutInflater layoutInflater = LayoutInflater.from(context)

    protected abstract View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container);
}
