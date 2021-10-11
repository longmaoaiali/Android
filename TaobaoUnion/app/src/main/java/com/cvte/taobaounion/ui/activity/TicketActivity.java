package com.cvte.taobaounion.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cvte.taobaounion.R;
import com.cvte.taobaounion.base.BaseActivity;
import com.cvte.taobaounion.model.domain.TicketResult;
import com.cvte.taobaounion.presenter.ITicketPresenter;
import com.cvte.taobaounion.utils.PresenterManager;
import com.cvte.taobaounion.utils.ToastUtils;
import com.cvte.taobaounion.utils.UrlUtils;
import com.cvte.taobaounion.view.ITicketPagerCallback;

import butterknife.BindView;

/**
 * Created by user on 2020/11/3.
 */

public class TicketActivity extends BaseActivity implements ITicketPagerCallback {

    private static final String TAG = "TicketActivity";
    private ITicketPresenter mTicketPresenter;

    @BindView(R.id.ticket_cover)
    public ImageView mTicketCover;

    @BindView(R.id.ticket_code)
    public EditText mTicketCode;

    @BindView(R.id.ticket_copy_btn)
    public TextView mTicketCopyBtn;

    @BindView(R.id.ticket_press_back)
    public ImageView mTicketPressBack;

    @BindView(R.id.ticket_cover_loading)
    public View mTicketCoverLoading;

    @BindView(R.id.ticket_load_retry)
    public TextView mTicketLoadRetry;
    private boolean mHasTaobaoApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initPresenter();
        initViewListener();
    }

    private void initViewListener() {
        mTicketCopyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //复制淘口令
                String ticketCode = mTicketCode.getText().toString().trim();
                ClipboardManager cm = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("taobao_ticket_code",ticketCode);
                cm.setPrimaryClip(clipData);
                /*如果有淘宝则跳转，没有就打印已复制toast*/
                if (mHasTaobaoApp) {
                    //todo:跳转打开淘宝
                    Intent taobaoIntent = new Intent();
                    /*ActivityTaskManager: START u0 {act=android.intent.action.MAIN
                    cat=[android.intent.category.LAUNCHER] flg=0x10200000 hwFlg=0x10
                    cmp=com.taobao.taobao/com.taobao.tao.welcome.Welcome bnds=[546,431][798,734]}*/
                    //taobaoIntent.setAction("android.intent.action.MAIN");
                    //taobaoIntent.addCategory("android.intent.category.LAUNCHER");
                    /*pkg=com.taobao.taobao cmp=com.taobao.taobao/com.taobao.tao.TBMainActivity*/
                    ComponentName componentName = new ComponentName("com.taobao.taobao","com.taobao.tao.TBMainActivity");
                    taobaoIntent.setComponent(componentName);
                    startActivity(taobaoIntent);
                } else {
                    ToastUtils.showToast("已经复制，粘贴分享或打开淘宝");
                }

            }
        });

        mTicketPressBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {

    }

    private void initPresenter() {
        mTicketPresenter = PresenterManager.getInstance().getTicketPresenter();
        if (mTicketPresenter != null) {
            mTicketPresenter.registerViewCallback(this);
        }

        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo("com.taobao.taobao",PackageManager.MATCH_UNINSTALLED_PACKAGES);
            mHasTaobaoApp = packageInfo!=null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            mHasTaobaoApp = false;
        }
        mTicketCopyBtn.setText(mHasTaobaoApp? "打开淘宝领券" : "复制口令" );

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_ticket;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTicketPresenter != null) {
            mTicketPresenter.unregisterViewCallback(this);
        }
    }

    @Override
    public void onNetworkError() {
        if (mTicketCoverLoading != null) {
            mTicketCoverLoading.setVisibility(View.GONE);
        }

        if (mTicketLoadRetry != null) {
            mTicketLoadRetry.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoading() {
        if (mTicketCoverLoading != null) {
            mTicketCoverLoading.setVisibility(View.VISIBLE);
        }

        if (mTicketLoadRetry != null) {
            mTicketLoadRetry.setVisibility(View.GONE);
        }
    }

    @Override
    public void onEmpty() {

    }

    @Override
    public void onTicketLoaded(String cover, TicketResult result) {

        Log.d(TAG,"cover url--> " + cover);
        if (mTicketCover != null && !TextUtils.isEmpty(cover)) {
            String coverPath = UrlUtils.getCoverPath(cover);
            Log.d(TAG,"coverPath url--> " + coverPath);
            Glide.with(this).load(coverPath).into(mTicketCover);
        }

        //处理结果没有图片的case
        if (TextUtils.isEmpty(cover)) {
            mTicketCover.setImageResource(R.mipmap.no_image);
        }

        if(result.getData().getTbk_tpwd_create_response()!=null){
            mTicketCode.setText(result.getData().getTbk_tpwd_create_response().getData().getModel());
        }
        if (mTicketCoverLoading != null) {
            mTicketCoverLoading.setVisibility(View.GONE);
        }
    }
}
