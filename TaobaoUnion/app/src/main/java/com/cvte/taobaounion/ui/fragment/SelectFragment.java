package com.cvte.taobaounion.ui.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cvte.taobaounion.R;
import com.cvte.taobaounion.base.BaseFragment;
import com.cvte.taobaounion.model.domain.SelectedContentNew;
import com.cvte.taobaounion.model.domain.SelectedPageCategory;
import com.cvte.taobaounion.presenter.ISelectedPagePresenter;
import com.cvte.taobaounion.presenter.ITicketPresenter;
import com.cvte.taobaounion.ui.activity.TicketActivity;
import com.cvte.taobaounion.ui.adapter.SelectedPageContentAdapter;
import com.cvte.taobaounion.ui.adapter.SelectedPageLeftAdapter;
import com.cvte.taobaounion.utils.LogUtils;
import com.cvte.taobaounion.utils.PresenterManager;
import com.cvte.taobaounion.utils.SizeUtils;
import com.cvte.taobaounion.view.ISelectedPageCallback;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

/**
 * Created by user on 2020/10/21.
 */

public class SelectFragment extends BaseFragment implements ISelectedPageCallback {

    private static final String TAG = "SelectFragment";
    private ISelectedPagePresenter mSelectedPagePresenter;

    @BindView(R.id.left_category_list)
    public RecyclerView leftCategoryList;

    @BindView(R.id.right_category_content)
    public RecyclerView rightCategoryContentList;
    private SelectedPageLeftAdapter mSelectedPageLeftAdapter;
    private SelectedPageContentAdapter mSelectedPageContentAdapter;

    @Override
    protected int getRootVireResId() {
        return R.layout.fragment_select;
    }

    @Override
    protected void initView(View rootView) {
        setUpState(State.SUCCESS);

        leftCategoryList.setLayoutManager(new LinearLayoutManager(getContext()));
        mSelectedPageLeftAdapter = new SelectedPageLeftAdapter();
        leftCategoryList.setAdapter(mSelectedPageLeftAdapter);

        rightCategoryContentList.setLayoutManager(new LinearLayoutManager(getContext()));
        mSelectedPageContentAdapter = new SelectedPageContentAdapter();
        rightCategoryContentList.setAdapter(mSelectedPageContentAdapter);
        rightCategoryContentList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int topAndBottom = SizeUtils.dip2px(getContext(),4);
                int leftAndRight = SizeUtils.dip2px(getContext(),6);

                outRect.top = topAndBottom;
                outRect.bottom = topAndBottom;
                outRect.right = leftAndRight;
                outRect.left = leftAndRight;
            }
        });
    }

    @Override
    protected void initPresenter() {
        super.initPresenter();
        mSelectedPagePresenter = PresenterManager.getInstance().getSelectedPagerPresenter();
        mSelectedPagePresenter.registerViewCallback(this);
        mSelectedPagePresenter.getCategories();
    }

    @Override
    protected void initViewListener() {
        super.initViewListener();
        mSelectedPageContentAdapter.setOnSelectedPageContentClickListener(new SelectedPageContentAdapter.OnSelectedPageContentClickListener() {
            @Override
            public void onContentItemClick(SelectedContentNew.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean item) {
                //todo: 处理looper recyclerView 点击事件
                String title = item.getTitle();
                String url = item.getCoupon_click_url();
                if (TextUtils.isEmpty(url)) {
                    url = item.getClick_url();
                }
                String cover = item.getPict_url();

                ITicketPresenter mTicketPresenter = PresenterManager.getInstance().getTicketPresenter();
                mTicketPresenter.getTicket(title,url,cover);
                startActivity(new Intent(getContext(), TicketActivity.class));

            }
        });

        mSelectedPageLeftAdapter.setOnLeftItemClickListener(new SelectedPageLeftAdapter.OnLeftItemClickListener() {
            @Override
            public void onLeftItemClick(SelectedPageCategory.DataBean item) {
                //左边分类的点击事件
                mSelectedPagePresenter.getContentByCategory(item);

                LogUtils.d(TAG,"leftRecyclerView  click CategoryID--> "+item.getFavorites_id());
                LogUtils.d(TAG,"leftRecyclerView  click --> "+item.getFavorites_title());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSelectedPagePresenter != null) {
            mSelectedPagePresenter.unregisterViewCallback(this);
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

    }

    @Override
    protected void onRetryClick() {
        //点击重试
        if (mSelectedPagePresenter != null) {
            mSelectedPagePresenter.reloadContent();
        }

    }



    @Override
    public void onCategoriesLoaded(SelectedPageCategory categories) {
        setUpState(State.SUCCESS);
        if (categories != null) {
            mSelectedPageLeftAdapter.setData(categories);
        }
        LogUtils.d(TAG,"onCategoriesLoaded-->"+categories);
        List<SelectedPageCategory.DataBean> data = categories.getData();
        mSelectedPagePresenter.getContentByCategory(data.get(0));
    }

    @Override
    public void onContentLoaded(SelectedContentNew content) {

        mSelectedPageContentAdapter.setData(content);
        LogUtils.d(TAG,"onContentLoaded-->");
        //转换到新的界面就会重定位到开头
        rightCategoryContentList.scrollToPosition(0);
    }
}
