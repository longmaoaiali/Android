package com.cvte.taobaounion.ui.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cvte.taobaounion.R;
import com.cvte.taobaounion.base.BaseFragment;
import com.cvte.taobaounion.model.domain.Histories;
import com.cvte.taobaounion.model.domain.SearchRecommand;
import com.cvte.taobaounion.model.domain.SearchResult;
import com.cvte.taobaounion.presenter.ISearchPresenter;
import com.cvte.taobaounion.presenter.impl.TicketPresenterImpl;
import com.cvte.taobaounion.ui.activity.TicketActivity;
import com.cvte.taobaounion.ui.adapter.SearchAdapter;
import com.cvte.taobaounion.ui.custom.TextFlowLayout;
import com.cvte.taobaounion.utils.KeyboardUtil;
import com.cvte.taobaounion.utils.LogUtils;
import com.cvte.taobaounion.utils.PresenterManager;
import com.cvte.taobaounion.utils.SizeUtils;
import com.cvte.taobaounion.view.ISearchViewCallback;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

/**
 * Created by user on 2020/10/21.
 */

public class SearchFragment extends BaseFragment implements ISearchViewCallback {

    private ISearchPresenter mSearchPresenter;
    private static final String TAG = "SearchFragment";

    @BindView(R.id.search_history_view)
    public TextFlowLayout mTextFlowLayoutHistory;

    @BindView(R.id.search_recommend_view)
    public TextFlowLayout mTextFlowLayoutRecommend;

    @BindView(R.id.search_recommend_container)
    public View mRecommendContainer;

    @BindView(R.id.search_history_container)
    public View mHistoryContainer;

    @BindView(R.id.search_history_delete)
    public View mHistoryDelete;

    @BindView(R.id.search_result_list)
    public RecyclerView mResultListView;

    @BindView(R.id.search_btn)
    public TextView mSearchBtn;

    @BindView(R.id.search_clean_btn)
    public ImageView mCleanInputBtn;

    @BindView(R.id.search_input_box)
    public EditText mSearchInputBox;




    private SearchAdapter mSearchAdapter;


    @Override
    protected int getRootVireResId() {
        return R.layout.fragment_search;
    }

    @Override
    protected void initPresenter() {
        mSearchPresenter = PresenterManager.getInstance().getSearchPresenter();
        mSearchPresenter.registerViewCallback(this);
        /*获取搜索推荐词*/
        mSearchPresenter.getRecommendWords();
        //mSearchPresenter.doSearch("毛衣");
        mSearchPresenter.getHistories();
    }

    @Override
    protected void initViewListener() {
        super.initViewListener();
        //历史与推荐搜索热词的点击事件
        mTextFlowLayoutHistory.setOnFlowTextItemClickListener(new TextFlowLayout.OnFlowTextItemClickListener() {
            @Override
            public void onFlowItemClick(String text) {
                if (mSearchPresenter != null) {
                    mResultListView.scrollToPosition(0);
                    mSearchInputBox.setText(text);
                    mSearchPresenter.doSearch(text);
                }
            }
        });

        mTextFlowLayoutRecommend.setOnFlowTextItemClickListener(new TextFlowLayout.OnFlowTextItemClickListener() {
            @Override
            public void onFlowItemClick(String text) {
                if (mSearchPresenter != null) {
                    mResultListView.scrollToPosition(0);
                    mSearchInputBox.setText(text);
                    mSearchPresenter.doSearch(text);
                }
            }
        });

        //发起搜索
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSearchInputBox.getText().toString().trim().length()>0) {
                    //非空格的输入就进行搜索
                    if (mSearchPresenter != null) {
                        mSearchPresenter.doSearch(mSearchInputBox.getText().toString().trim());
                        KeyboardUtil.hide(getContext(),v);
                    }
                } else {
                    KeyboardUtil.hide(getContext(),v);
                }
            }
        });

        //输入框清除按钮
        mCleanInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchInputBox.setText("");
                //显示历史记录界面
                switch2HistoryPage();
            }
        });

        //输入框内容变化
        mSearchInputBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //有输入就显示 包括空格
                mCleanInputBtn.setVisibility(s.toString().length() > 0 ? View.VISIBLE:View.GONE);
                //输入的是字符
                mSearchBtn.setText(s.toString().trim().length()>0?"搜索":"取消");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        //输入框发起搜索
        mSearchInputBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                LogUtils.d(TAG,"onEditorAction-->");
                if (actionId == EditorInfo.IME_ACTION_SEARCH && mSearchPresenter != null) {
                    String keyword = v.getText().toString().trim();
                    LogUtils.d(TAG,"onEditorAction-->keyword"+keyword);
                    if (TextUtils.isEmpty(keyword)) {
                        return  false;
                    }
                    LogUtils.d(TAG,"onEditorAction-->doSearch(keyword) " + keyword);
                    mSearchPresenter.doSearch(keyword);
                }
                return false;
            }
        });

        mSearchAdapter.setOnListItemClickListener(new SearchAdapter.OnListItemClickListener() {
            @Override
            public void onItemClick(SearchResult.DataBean.TbkDgMaterialOptionalResponseBean.ResultListBean.MapDataBean item) {
                String title = item.getTitle();
                String url = item.getCoupon_share_url();
                if (TextUtils.isEmpty(url)) {
                    url = item.getPict_url();
                }
                String cover = item.getPict_url();

                TicketPresenterImpl mTicketPresenter = PresenterManager.getInstance().getTicketPresenter();
                mTicketPresenter.getTicket(title,url,cover);
                startActivity(new Intent(getContext(), TicketActivity.class));
            }
        });

        mHistoryDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSearchPresenter != null) {
                    mSearchPresenter.delHistories();
                }
            }
        });
    }

    private void switch2HistoryPage() {
        if (mSearchPresenter != null) {
            mSearchPresenter.getHistories();
        }

        if (mTextFlowLayoutHistory.getContentSize() != 0) {
            mHistoryContainer.setVisibility(View.VISIBLE);
        } else {
            mHistoryContainer.setVisibility(View.GONE);
        }

        if (mTextFlowLayoutRecommend.getContentSize() != 0) {
            mRecommendContainer.setVisibility(View.VISIBLE);
        } else {
            mRecommendContainer.setVisibility(View.GONE);
        }

        mResultListView.setVisibility(View.GONE);
    }

    @Override
    protected void initView(View rootView) {
        setUpState(State.SUCCESS);
        mResultListView.setLayoutManager(new LinearLayoutManager(getContext()));
        mSearchAdapter = new SearchAdapter();
        mResultListView.setAdapter(mSearchAdapter);
        mResultListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = SizeUtils.dip2px(getContext(),1.5f);
                outRect.bottom = SizeUtils.dip2px(getContext(),1.5f);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSearchPresenter != null) {
            mSearchPresenter.unregisterViewCallback(this);
        }
    }

    @Override
    public void onNetworkError() {

    }

    @Override
    public void onLoading() {

    }

    @Override
    public void onEmpty() {

    }

    @Override
    public void onHistoriesLoaded(Histories histories) {
        //LogUtils.d(TAG,"onHistoriesLoaded-->"+histories.getHistories().get(0));
        if (histories == null || histories.getHistories().size() == 0) {
            mHistoryContainer.setVisibility(View.GONE);
        } else {
            mHistoryContainer.setVisibility(View.VISIBLE);
            mTextFlowLayoutHistory.setTextList(histories.getHistories());
        }
    }

    @Override
    public void onHistoriesDeleted() {
        if (mSearchPresenter != null) {
            //更新UI显示
            mSearchPresenter.getHistories();
        }
    }

    @Override
    public void onSearchSuccess(SearchResult result) {
        LogUtils.d(TAG,"search result --> "+result.getData().getTbk_dg_material_optional_response().getResult_list().getMap_data().get(0).getCoupon_info());
        //mSearchAdapter
        /*隐藏历史记录和推荐 显示搜索结果*/
        mRecommendContainer.setVisibility(View.GONE);
        mHistoryContainer.setVisibility(View.GONE);
        mResultListView.setVisibility(View.VISIBLE);

        mSearchAdapter.setData(result);

    }

    @Override
    public void onMoreLoaded(SearchResult result) {

    }

    @Override
    public void onMoreLoadedError() {

    }

    @Override
    public void onMoreLoadedEmpty() {

    }

    @Override
    public void onRecommendWordsLoaded(List<SearchRecommand.DataBean> recommendWoeds) {
        //推荐词回调
        LogUtils.d(TAG,"size--> "+recommendWoeds.size());
        List<String> recommendList = new ArrayList<>();
        for (SearchRecommand.DataBean recommendWoed : recommendWoeds) {
            recommendList.add(recommendWoed.getKeyword());
        }

        if (recommendList==null || recommendList.size()==0) {
            mRecommendContainer.setVisibility(View.GONE);
        } else {
            mRecommendContainer.setVisibility(View.VISIBLE);
            mTextFlowLayoutRecommend.setTextList(recommendList);
        }

    }
}
