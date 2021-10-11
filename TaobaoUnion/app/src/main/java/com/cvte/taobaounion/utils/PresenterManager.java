package com.cvte.taobaounion.utils;

import com.cvte.taobaounion.presenter.IOnSellPagePresenter;
import com.cvte.taobaounion.presenter.ISelectedPagePresenter;
import com.cvte.taobaounion.presenter.impl.CategoryPagerPresenterImpl;
import com.cvte.taobaounion.presenter.impl.HomePresenterImpl;
import com.cvte.taobaounion.presenter.impl.IOnSellPagePresenterImpl;
import com.cvte.taobaounion.presenter.impl.SearchPresenterImpl;
import com.cvte.taobaounion.presenter.impl.SelectedPagerPresenterImpl;
import com.cvte.taobaounion.presenter.impl.TicketPresenterImpl;

/**
 * Created by user on 2020/11/3.
 */

public class PresenterManager {

    private static final PresenterManager instance = new PresenterManager();
    private final ISelectedPagePresenter mSelectedPagePresenter;
    private final IOnSellPagePresenter mOnSellPagePresenter;
    private final SearchPresenterImpl mSearchPresenter;

    public HomePresenterImpl getHomePresenter() {
        return mHomePresenter;
    }

    public CategoryPagerPresenterImpl getCategoryPagerPresenter() {
        return mCategoryPagerPresenter;
    }

    private final HomePresenterImpl mHomePresenter;
    private final CategoryPagerPresenterImpl mCategoryPagerPresenter;

    public TicketPresenterImpl getTicketPresenter() {
        return mTicketPresenter;
    }

    private final TicketPresenterImpl mTicketPresenter;

    public static PresenterManager getInstance() {
        return instance;
    }

    public ISelectedPagePresenter getSelectedPagerPresenter(){
        return mSelectedPagePresenter;
    }

    public IOnSellPagePresenter getOnSellPagePresenter() {
        return mOnSellPagePresenter;
    }

    public SearchPresenterImpl getSearchPresenter() {
        return mSearchPresenter;
    }

    private PresenterManager(){
        mHomePresenter = new HomePresenterImpl();
        mCategoryPagerPresenter = new CategoryPagerPresenterImpl();
        mTicketPresenter = new TicketPresenterImpl();
        mSelectedPagePresenter = new SelectedPagerPresenterImpl();
        mOnSellPagePresenter = new IOnSellPagePresenterImpl();
        mSearchPresenter = new SearchPresenterImpl();
    }
}
