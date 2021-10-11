package com.cvte.ximalaya.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;

import com.cvte.ximalaya.R;

/**
 * Created by user on 2020/10/14.
 */

public class ConfirmCheckBoxDialog extends Dialog {

    private View mCancleBtn;
    private View mGiveUpBtn;
    private onDialogListener mDialogListener = null;
    private CheckBox mCheckBox;

    public ConfirmCheckBoxDialog(@NonNull Context context) {
        this(context,0);
    }

    public ConfirmCheckBoxDialog(@NonNull Context context, int themeResId) {
        this(context, true,null);
    }

    protected ConfirmCheckBoxDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_check_box_confirm);
        initView();

        ininViewListener();
    }

    private void ininViewListener() {
        mCancleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialogListener != null) {

                    mDialogListener.onCancelSub();
                    dismiss();
                }
            }
        });


        mGiveUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialogListener != null) {
                    boolean checked = mCheckBox.isChecked();
                    mDialogListener.onGiveUp(checked);
                    dismiss();
                }
            }
        });

    }


    private void initView() {
        mCancleBtn = this.findViewById(R.id.cancel_sub);
        mGiveUpBtn = this.findViewById(R.id.give_up);
        mCheckBox = this.findViewById(R.id.checkbox_all);
    }

    public void setonDialogListener(onDialogListener listener){
        this.mDialogListener  = listener;
    }

    public interface onDialogListener{
        void onCancelSub();
        void onGiveUp(boolean checked);

    }

}
