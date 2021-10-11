package com.cvte.taobaounion.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cvte.taobaounion.R;
import com.cvte.taobaounion.base.BaseFragment;
import com.cvte.taobaounion.model.domain.Categories;
import com.cvte.taobaounion.presenter.impl.HomePresenterImpl;
import com.cvte.taobaounion.ui.activity.MainActivity;
import com.cvte.taobaounion.ui.activity.ScanQrCodeActivity;
import com.cvte.taobaounion.ui.adapter.HomePagerAdapter;
import com.cvte.taobaounion.view.IHomeCallback;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;

/**
 * Created by user on 2020/10/21.
 */

public class HomeFragment extends BaseFragment implements IHomeCallback {


    private HomePresenterImpl mHomePresenter;
    @BindView(R.id.home_indicator)
    public TabLayout mTabLayout;
    @BindView(R.id.home_pager)
    public ViewPager mHomePager;
    @BindView(R.id.home_search_input_box)
    public View mHomePagerInputBox;

    @BindView(R.id.scan_icon)
    public View mScanBtn;


    private HomePagerAdapter mHomePagerAdapter;


    @Override
    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.base_home_fragment_layout,container,false);
    }

    @Override
    protected void initView(View rootView) {
        /*设置Layout 与 ViewPager */
        //设置ViewPager
        mTabLayout.setupWithViewPager(mHomePager);
        //ViewPager设置适配器
        mHomePagerAdapter = new HomePagerAdapter(getChildFragmentManager());
        //设置homePager的适配器
        mHomePager.setAdapter(mHomePagerAdapter);
    }

    @Override
    protected void onRetryClick() {
        //网络错误被点击
        if (mHomePresenter != null) {
            mHomePresenter.getCategories();
        }
    }

    @Override
    protected int getRootVireResId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initPresenter() {
        mHomePresenter = new HomePresenterImpl();
        mHomePresenter.registerViewCallback(this);
        mHomePagerInputBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo:跳转到搜索页面
                FragmentActivity activity = getActivity();
                if (activity instanceof MainActivity) {
                    ((MainActivity) activity).switch2SearchPage();
                }
            }
        });

        mScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo；跳转扫码点击事件
                startActivity(new Intent(getContext(),ScanQrCodeActivity.class));
            }
        });
    }

    @Override
    protected void loadData() {
        //setUpState(State.LOADING);
        mHomePresenter.getCategories();
    }

    @Override
    public void onCategoriesloaded(Categories categories) {

        setUpState(State.SUCCESS);

        //加载的数据回调回来
        if (mHomePagerAdapter != null) {
            /*ViewPager的预加载*/
            //mHomePager.setOffscreenPageLimit(categories.getData().size());
            mHomePagerAdapter.setCategories(categories);
        }
    }

    @Override
    public void onNetworkError() {
        setUpState(State.ERROR);
    }

    @Override
    public void onLoading() {
        setUpState(State.LOADING);
    }

    @Override
    public void onEmpty() {
        setUpState(State.EMPTY);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        release();
    }

    private void release() {
        if (mHomePresenter != null) {
            mHomePresenter.unregisterViewCallback(this);
        }
    }
}
