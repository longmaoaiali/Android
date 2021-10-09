package com.cvte.wifip2pfiletransfer.view;

import android.app.Dialog;
import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.cvte.wifip2pfiletransfer.R;

/**
 * Created by user on 2020/11/2.
 */

public class LoadingDialog extends Dialog {

    private final TextView mTvHint;
    private final ImageView mIvLoading;
    private final Animation mAnimation;

    public LoadingDialog(Context context) {
        super(context, R.style.LoadingDialogTheme);
        setContentView(R.layout.dialog_loading);
        mIvLoading = findViewById(R.id.tv_loading);
        mTvHint = findViewById(R.id.tv_loadinghint);
        mAnimation = AnimationUtils.loadAnimation(context, R.anim.loading_dialog);
    }

    public void show(String hintText,boolean cancelable,boolean cancleedOnTouchOutside){
        setCancelable(cancelable);
        setCanceledOnTouchOutside(cancleedOnTouchOutside);
        mTvHint.setText(hintText);
        mIvLoading.startAnimation(mAnimation);
        this.show();
    }

    @Override
    public void cancel() {
        super.cancel();
        mAnimation.cancel();
        mIvLoading.clearAnimation();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mAnimation.cancel();
        mIvLoading.clearAnimation();
    }
}
