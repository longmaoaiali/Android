package com.cvte.ximalaya;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cvte.ximalaya.adapters.RecommendListAdapter;
import com.cvte.ximalaya.adapters.SearchRecommendAdapter;
import com.cvte.ximalaya.base.BaseActivity;
import com.cvte.ximalaya.interfaces.ISearchCallback;
import com.cvte.ximalaya.presenters.AlbumDetialPresenter;
import com.cvte.ximalaya.presenters.SearchPresenter;
import com.cvte.ximalaya.utils.LogUtil;
import com.cvte.ximalaya.views.FlowTextLayout;
import com.cvte.ximalaya.views.UILoader;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by user on 2020/9/25.
 */

public class SearchActivity extends BaseActivity implements ISearchCallback, RecommendListAdapter.OnRecommendItemClickListner {

    private View mBackBtn;
    private EditText mInputText;
    private TextView mSearchBtn;
    private FrameLayout mSearchResult;
    private static final String TAG="SearchActivity";
    private SearchPresenter mSearchPresenter;
    private UILoader mUILoader;
    private RecyclerView mResultListView;
    private RecommendListAdapter mRecommendListAdapter;
    private FlowTextLayout mFlowTextLayout;
    private InputMethodManager mInm;
    private View mDelInputBtn;
    private RecyclerView mSearchRecommendList;
    private SearchRecommendAdapter mSearchRecommendAdapter;
    private TwinklingRefreshLayout mRefreshLayout;

    private boolean mNeedSuggestWords = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        initView();

        initPresenter();

        initViewListener();
    }

    private void initPresenter() {
        mInm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        mSearchPresenter = SearchPresenter.getInstance();
        //注册回调接口
        mSearchPresenter.registerViewCallback(this);

        //获取热词
        mSearchPresenter.getHotWord();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSearchPresenter != null) {
            mSearchPresenter.registerViewCallback(this);
            mSearchPresenter = null;
        }
    }

    private void initViewListener() {

        mRecommendListAdapter.setOnRecommendItemClickListner(this);

        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                //todo:onloadermore
                if (mSearchPresenter != null) {
                    mSearchPresenter.loadMore();
                }

            }
        });

        if (mSearchRecommendAdapter != null) {
            mSearchRecommendAdapter.setItemClickListener(new SearchRecommendAdapter.ItemClickListener() {
                @Override
                public void onItemClick(String keyword) {
                    //只有在输入时会有联想
                    mNeedSuggestWords = false;
                    LogUtil.d(TAG,"recommend item keyword-->" + keyword);
                    mInputText.setText(keyword);
                    mInputText.setSelection(keyword.length());
                    if (mSearchPresenter != null) {
                        mSearchPresenter.doSearch(keyword);
                    }
                }
            });
        }

        mDelInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputText.setText("");
            }
        });

        if (mFlowTextLayout != null) {
            mFlowTextLayout.setClickListener(new FlowTextLayout.ItemClickListener() {
                @Override
                public void onItemClick(String text) {
                    mNeedSuggestWords = false;
                    //Toast.makeText(SearchActivity.this,text,Toast.LENGTH_SHORT).show();
                    //将热词放入输入框 并发起搜索
                    mInputText.setText(text);
                    mInputText.setSelection(text.length());
                    if (mSearchPresenter != null) {
                        mSearchPresenter.doSearch(text);
                    }
                }
            });
        }


        if (mUILoader != null) {
            mUILoader.setOnRetryClickListener(new UILoader.OnRetryClickListener() {
                @Override
                public void onRetryClick() {
                    if (mSearchPresenter != null) {
                        mSearchPresenter.reSearch();
                        mUILoader.updateStatus(UILoader.UIStatus.LOADING);
                    }
                }
            });
        }


        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mInputText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                //搜索逻辑
//                String keyword = mInputText.getText().toString().trim();
//                if (mSearchPresenter != null) {
//                    mSearchPresenter.doSearch(keyword);
//                    if (mUILoader != null) {
//                        mUILoader.updateStatus(UILoader.UIStatus.LOADING);
//                    }
                //}
            }
        });

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //搜索逻辑
                String keyword = mInputText.getText().toString().trim();
                if (mSearchPresenter != null) {
                    mSearchPresenter.doSearch(keyword);
                    if (mUILoader != null) {
                        mUILoader.updateStatus(UILoader.UIStatus.LOADING);
                    }
                }
            }
        });

        mInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                LogUtil.d(TAG,"content-->" +s );
//                LogUtil.d(TAG,"start-->" +start );
//                LogUtil.d(TAG,"before-->" +before );
//                LogUtil.d(TAG,"count-->" +count );
                if (TextUtils.isEmpty(s)) {
                    mSearchPresenter.getHotWord();
                    mDelInputBtn.setVisibility(View.GONE);
                }else{
                    mDelInputBtn.setVisibility(View.VISIBLE);
                    //内容不为空触发联想查询
                    if (mNeedSuggestWords) {
                        getSuggestWord(s.toString());
                    }else {
                        mNeedSuggestWords = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });




    }

    /**
     * 获取联想的关键词
     * @param keyword
     */
    private void getSuggestWord(String keyword) {
        if (mSearchPresenter != null) {
            mSearchPresenter.getRecommendWord(keyword);
        }
    }

    private void initView() {
        mBackBtn = this.findViewById(R.id.search_back);
        mInputText = this.findViewById(R.id.search_edit);
        mInputText.postDelayed(new Runnable() {
            @Override
            public void run() {
                mInputText.requestFocus();
                mInm.showSoftInput(mInputText, InputMethodManager.SHOW_IMPLICIT);
            }
        },500);


        mSearchBtn = this.findViewById(R.id.search_btn);
        mSearchResult = this.findViewById(R.id.search_container);

        mDelInputBtn = this.findViewById(R.id.search_input_btn);
        mDelInputBtn.setVisibility(View.GONE);

        //mFlowTextLayout = this.findViewById(R.id.flow_text_layout);
        if (mUILoader == null) {
            mUILoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView();
                }
            };

            //Fragment添加View之前需要删除View
            if (mUILoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUILoader.getParent()).removeView(mUILoader);
            }
            mSearchResult.addView(mUILoader);
        }

    }

    /**
     * 创建搜索成功的VIew
     * @return
     */
    private View createSuccessView() {
        View resultView = LayoutInflater.from(this).inflate(R.layout.search_result_layout,null);
        //控件刷新效果
        mRefreshLayout = resultView.findViewById(R.id.search_result_refresh_layout);
        //禁止上拉
        mRefreshLayout.setEnableRefresh(false);


        //显示热词
        mFlowTextLayout = resultView.findViewById(R.id.recommend_hot_word_list);


        mResultListView = resultView.findViewById(R.id.result_list_view);
        //设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mResultListView.setLayoutManager(layoutManager);

        //设置适配器 直接复用RecommendList的Adapter
        mRecommendListAdapter = new RecommendListAdapter();
        mResultListView.setAdapter(mRecommendListAdapter);


        mResultListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                /*这里是需要像素值 使用magic*/
                outRect.top = UIUtil.dip2px(view.getContext(),5);
                outRect.bottom = UIUtil.dip2px(view.getContext(),5);
                outRect.left = UIUtil.dip2px(view.getContext(),5);
                outRect.right = UIUtil.dip2px(view.getContext(),5);
            }
        });

        //搜索推荐 search_recommend_list_view
        mSearchRecommendList = resultView.findViewById(R.id.search_recommend_list_view);
        LinearLayoutManager recommendLayoutManager = new LinearLayoutManager(this);
        mSearchRecommendList.setLayoutManager(recommendLayoutManager);

        mSearchRecommendAdapter = new SearchRecommendAdapter();
        mSearchRecommendList.setAdapter(mSearchRecommendAdapter);
        mSearchRecommendList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(),2);
                outRect.bottom = UIUtil.dip2px(view.getContext(),2);
                outRect.left = UIUtil.dip2px(view.getContext(),5);
                outRect.right = UIUtil.dip2px(view.getContext(),5);
            }
        });

        return resultView;
    }

    @Override
    public void onSearchResultLoaded(List<Album> result) {
        handlerSearchResult(result);

        //隐藏键盘
        mInm.hideSoftInputFromWindow(mInputText.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        LogUtil.d(TAG,"liuyu onSearchResultLoaded");

    }

    private void handlerSearchResult(List<Album> result) {
        hideAllView();
        mRefreshLayout.setVisibility(View.VISIBLE);
        if (result != null) {
            if (result.size() == 0) {
                if (mUILoader != null) {
                    mUILoader.updateStatus(UILoader.UIStatus.EMPTY);
                }
            }else{
                if (mUILoader != null && mRecommendListAdapter != null) {
                    LogUtil.d(TAG,"mUILoader != null && mRecommendListAdapter != null");
                    mRecommendListAdapter.setData(result);
                    mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
                }
            }
        }
    }

    @Override
    public void onHotWordLoaded(List<HotWord> hotWordList) {
        hideAllView();
        mFlowTextLayout.setVisibility(View.VISIBLE);

        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        LogUtil.d(TAG,"hotWorldList-- >"+hotWordList.size());
        List<String> hotwords = new ArrayList<>();
        hotwords.clear();
        for (HotWord hotWord : hotWordList) {
            String serachWord = hotWord.getSearchword();
            hotwords.add(serachWord);
        }
        Collections.sort(hotwords);
        if (mFlowTextLayout != null) {
            mFlowTextLayout.setTextContents(hotwords);
        }
    }

    @Override
    public void onLoadMoreResult(List<Album> result, boolean isOkay) {
        //处理加载更多的结果
        if (mRefreshLayout != null) {
            mRefreshLayout.finishLoadmore();
        }

        if (isOkay) {
            handlerSearchResult(result);
        }else{
            Toast.makeText(SearchActivity.this,"没有更多内容",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRecommendWordLoaded(List<QueryResult> keyWordList) {
        //联想词回调显示
        if (mRecommendListAdapter != null) {
            mSearchRecommendAdapter.setData(keyWordList);
        }
        //todo:控制UI的显示
        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }

        //控制显示与隐藏
        hideAllView();
        mSearchRecommendList.setVisibility(View.VISIBLE);

    }

    @Override
    public void onError(int errorCode, String errorMsg) {
        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
        }
    }

    private void hideAllView(){
        mSearchRecommendList.setVisibility(View.GONE);
        mRefreshLayout.setVisibility(View.GONE);
        mFlowTextLayout.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(int cliclPosition, Album album) {
        AlbumDetialPresenter.getInstance().setAlbumDetail(album);
        //item被点击了
        Intent intent = new Intent(this,DetailActivity.class);
        startActivity(intent);
    }

}
