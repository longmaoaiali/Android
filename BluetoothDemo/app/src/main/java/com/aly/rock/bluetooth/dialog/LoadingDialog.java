package com.aly.rock.bluetooth.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.StringRes;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.aly.rock.bluetooth.R;

public class LoadingDialog extends Dialog {

    private ImageView iv_loading;

    private TextView tv_hint;

    private Animation animation;

    public LoadingDialog(Context context) {
        super(context, R.style.LoadingDialogTheme);
        setContentView(R.layout.dialog_loading);
        iv_loading = findViewById(R.id.iv_loading);
        tv_hint = findViewById(R.id.tv_hint);
        animation = AnimationUtils.loadAnimation(context, R.anim.loading_dialog);
    }

    public void show(String hintText, boolean cancelable, boolean canceledOnTouchOutside) {
        setCancelable(cancelable);
        setCanceledOnTouchOutside(canceledOnTouchOutside);
        tv_hint.setText(hintText);
        iv_loading.startAnimation(animation);
        show();
    }

    public void show(@StringRes int hintTextRes, boolean cancelable, boolean canceledOnTouchOutside) {
        setCancelable(cancelable);
        setCanceledOnTouchOutside(canceledOnTouchOutside);
        tv_hint.setText(hintTextRes);
        iv_loading.startAnimation(animation);
        show();
    }

    @Override
    public void cancel() {
        super.cancel();
        animation.cancel();
        iv_loading.clearAnimation();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        animation.cancel();
        iv_loading.clearAnimation();
    }

}