package com.cvte.taobaounion.ui.adapter;

import com.cvte.taobaounion.model.domain.Categories;
import com.cvte.taobaounion.ui.fragment.HomePagerFragment;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * Created by user on 2020/10/23.
 */

public class HomePagerAdapter extends FragmentPagerAdapter{

    private  List<Categories.DataBean> categoryList = new ArrayList<>();

    public HomePagerAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return categoryList.get(position).getTitle();
    }

    @Override
    public Fragment getItem(int position) {
        Categories.DataBean dataBean = categoryList.get(position);
        HomePagerFragment homePagerFragment = HomePagerFragment.newInstance(dataBean);
        return homePagerFragment;
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    public void setCategories(Categories categories) {
        this.categoryList.clear();
        List<Categories.DataBean> datas = categories.getData();
        this.categoryList.addAll(datas);
        notifyDataSetChanged();
    }
}
